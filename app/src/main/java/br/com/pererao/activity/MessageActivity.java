package br.com.pererao.activity;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import br.com.pererao.R;
import br.com.pererao.SharedPref;
import br.com.pererao.activity.ui.APIService;
import br.com.pererao.activity.ui.chat.ChatActivity;
import br.com.pererao.adapter.MessageAdapter;
import br.com.pererao.model.Chat;
import br.com.pererao.model.User;
import br.com.pererao.notifications.Client;
import br.com.pererao.notifications.Data;
import br.com.pererao.notifications.MyResponse;
import br.com.pererao.notifications.Sender;
import br.com.pererao.notifications.Token;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    ImageView profile_user_image_chat, iv_preview_send_chat, iv_close_preview;
    RelativeLayout rl_preview;
    TextView username_chat;
    FloatingActionButton fab_send_message, fab_open_close;
    TextInputLayout ti_ed_message_user;
    TextInputEditText ed_message_user;
    FirebaseUser mFirebaseUser;
    private static final String USUARIO = "Usuario";
    DatabaseReference mDatabaseReference;
    Intent intent;
    Toolbar toolbar;
    SharedPref sharedPref;
    String id, imgURL;
    private String str = "";
    ValueEventListener seenListener;
    LinearLayout ll_fab_send, ll_opcao, ll_cam, ll_gallery, ll_intent;
    Bitmap bitmap = null;

    public final int REQ_CD_CAM = 101;
    public final int REQ_CD_GALL = 102;
    private Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    private Intent file = new Intent(Intent.ACTION_GET_CONTENT);
    private File _file_cam;
    private String imagePath = "";
    private String imageName = "";
    private int image = 0;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;
    APIService apiService;
    boolean notify = false;
    boolean clicked = false;
    boolean isImg = false;
    LoadingDialog loadingDialog = new LoadingDialog(MessageActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        sharedPref.CarregamentoTemaEscuro();
        setContentView(R.layout.activity_message);

        //TODO: DECLARAÇÕES
        ll_fab_send = findViewById(R.id.ll_fab_send);
        fab_send_message = findViewById(R.id.fab_send_message);
        fab_open_close = findViewById(R.id.fab_open_close_opcao);

        ti_ed_message_user = findViewById(R.id.ti_chat_message);
        ed_message_user = findViewById(R.id.et_chat_message);
        recyclerView = findViewById(R.id.recycler_view);
        iv_preview_send_chat = findViewById(R.id.img_preview_send_chat);
        iv_close_preview = findViewById(R.id.fecha_preview);
        rl_preview = findViewById(R.id.rl_preview);
        profile_user_image_chat = findViewById(R.id.img_user_chat);
        username_chat = findViewById(R.id.username_chat);

        ll_intent = findViewById(R.id.ll_intent);
        ll_opcao = findViewById(R.id.ll_opcao);
        ll_gallery = findViewById(R.id.ll_gallery);
        ll_cam = findViewById(R.id.ll_cam);

        intent = getIntent();
        id = intent.getStringExtra("id");

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(USUARIO).child(id);

        //ToolBar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.toolbar.setTitle("");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoChatActivity();
            }
        });

        fab_open_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clicked) {
                    clicked = false;
                    ll_intent.setVisibility(View.GONE);
                    fab_open_close.setImageResource(R.drawable.ic_clip);
                } else {
                    clicked = true;
                    ll_intent.setVisibility(View.VISIBLE);
                    fab_open_close.setImageResource(R.drawable.ic_close);
                }
                Log.w("BOOLEAN", "esta:" + clicked);
            }
        });

        final MediaPlayer send_message_sound = MediaPlayer.create(getApplicationContext(), R.raw.send_message);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        ti_ed_message_user.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                final String _charSeq = charSequence.toString();
                if (_charSeq.length() > 5000) {
                    str = _charSeq.substring(0, 5000);
                    ti_ed_message_user.getEditText().setText("");
                }
                if (_charSeq.length() == 0) {
                    ll_fab_send.setVisibility(View.GONE);
                    ti_ed_message_user.getEditText().append(str);
                    str = "";
                } else {
                    ll_fab_send.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int i;
                //remove espaço dos primeiros caracteres
                for (i = 0; i < editable.length() && Character.isWhitespace(editable.charAt(i)); i++) {
                    Toast.makeText(getApplicationContext(), "Texto vázio.", Toast.LENGTH_SHORT).show();
                }
                editable.replace(0, i, "");
                //remove deixando apenas 1 caractere de espaço no final do texto
                for (i = editable.length(); i > 1 && Character.isWhitespace(editable.charAt(i - 1)) && Character.isWhitespace(editable.charAt(i - 2)); i--) {
                    Toast.makeText(getApplicationContext(), "Máximo de um espaço.", Toast.LENGTH_SHORT).show();
                }
                editable.replace(i, editable.length(), "");
            }
        });

        fab_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msg = ti_ed_message_user.getEditText().getText().toString().trim();
                if (!(TextUtils.isEmpty(msg))) {

                    long messageTime = new Date().getTime();
                    send_message_sound.start();
                    sendMessage(msg, id, mFirebaseUser.getUid(), messageTime);
                } else {
                    Toast.makeText(getApplicationContext(), "Campo Vázio", Toast.LENGTH_SHORT).show();
                }
                ed_message_user.setText("");
            }
        });

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                //User user = snapshot.getValue(User.class);
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    username_chat.setText(user.getNomeUser());
                    if (user.getUserUrl().equals("default")) {
                        profile_user_image_chat.setImageResource(R.drawable.ic_user_icon);
                    } else {
                        Glide.with(getApplicationContext())
                                .load(user.getUserUrl())
                                .into(profile_user_image_chat);
                    }

                    // }
                    Chat c = dataSnapshot.getValue(Chat.class);
                    assert c != null;
                    c.setReceiver(mFirebaseUser.getUid());
                    long dateMessage = c.getMessageTime();
                    readMessage(mFirebaseUser.getUid(), id, dateMessage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        seenMessage(id);

        file.setType("image/*");
        file.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        _file_cam = FileUtil.createNewPictureFile(getApplicationContext());
        Uri _uri_cam = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            _uri_cam = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", _file_cam);
        } else {
            _uri_cam = Uri.fromFile(_file_cam);
        }
        cam.putExtra(MediaStore.EXTRA_OUTPUT, _uri_cam);
        cam.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        ll_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = false;
                ll_intent.setVisibility(View.GONE);
                fab_open_close.setImageResource(R.drawable.ic_clip);
                startActivityForResult(cam, REQ_CD_CAM);
            }
        });

        ll_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = false;
                ll_intent.setVisibility(View.GONE);
                fab_open_close.setImageResource(R.drawable.ic_clip);
                startActivityForResult(file, REQ_CD_GALL);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CD_CAM:
                if (resultCode == RESULT_OK) {
                    String _filePath = _file_cam.getAbsolutePath();

                    imagePath = _filePath;
                    imageName = Uri.parse(_filePath).getLastPathSegment();

                    sendFotoCam(Uri.fromFile(new File(imagePath)), imageName);

                }
                break;
            case REQ_CD_GALL:
                if (resultCode == RESULT_OK) {
                    Uri uriz = data.getData();
                    assert uriz != null;
                    imageName = uriz.getLastPathSegment();

                    final DatabaseReference mDataRefChat = FirebaseDatabase.getInstance().getReference();
                    final Chat chat = new Chat();
                    final HashMap<String, Object> hashMap = new HashMap<>();
                    chat.setKeyMessage(mDataRefChat.push().getKey());
                    chat.setImgKey(imageName);
                    loadingDialog.startLoadingDialog();
                    StorageReference mStorageReference = FirebaseStorage.getInstance().getReference().child("user_photo");
                    final StorageReference imageFile = mStorageReference.child(chat.getKeyMessage()).child(imageName);
                    imageFile.putFile(uriz).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    loadingDialog.dismissDialog();
                                    String url = uri.toString();
                                    chat.setImgUrl(url);
                                    chat.setMessage("");
                                    long messageTime = new Date().getTime();
                                    chat.setMessageTime(messageTime);
                                    chat.setSender(mFirebaseUser.getUid());
                                    chat.setReceiver(id);
                                    chat.setIsseen(false);

                                    hashMap.put("keyMessage", chat.getKeyMessage());
                                    hashMap.put("message", chat.getMessage());
                                    hashMap.put("receiver", chat.getReceiver());
                                    hashMap.put("sender", chat.getSender());
                                    hashMap.put("messageTime", chat.getMessageTime());
                                    hashMap.put("isseen", chat.isIsseen());
                                    hashMap.put("imgKey", chat.getImgKey());
                                    hashMap.put("imgUrl", chat.getImgUrl());

                                    mDataRefChat.child("Chat").child("Mensagens").child(chat.getKeyMessage()).setValue(hashMap);
                                    chatListNotificacao(chat.getReceiver(),"Foto 📷", "Lhe enviou uma imagem");
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Falha ao enviar a imagem.", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissDialog();
                        }
                    });

                }
                break;
            default:
                break;
        }

    }

    private void sendFotoCam(Uri filePath, String imageName) {

        final DatabaseReference mDataRefChat = FirebaseDatabase.getInstance().getReference();
        final Chat chat = new Chat();
        final HashMap<String, Object> hashMap = new HashMap<>();
        chat.setKeyMessage(mDataRefChat.push().getKey());
        chat.setImgKey(imageName);
        loadingDialog.startLoadingDialog();
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference().child("user_photo");
        final StorageReference imageFile = mStorageReference.child(chat.getKeyMessage()).child(imageName);
        imageFile.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {//Uri.parse(_filePath)
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        loadingDialog.dismissDialog();
                        String url = uri.toString();
                        chat.setImgUrl(url);
                        chat.setMessage("");
                        long messageTime = new Date().getTime();
                        chat.setMessageTime(messageTime);
                        chat.setSender(mFirebaseUser.getUid());
                        chat.setReceiver(id);
                        chat.setIsseen(false);

                        hashMap.put("keyMessage", chat.getKeyMessage());
                        hashMap.put("message", chat.getMessage());
                        hashMap.put("receiver", chat.getReceiver());
                        hashMap.put("sender", chat.getSender());
                        hashMap.put("messageTime", chat.getMessageTime());
                        hashMap.put("isseen", chat.isIsseen());
                        hashMap.put("imgKey", chat.getImgKey());
                        hashMap.put("imgUrl", chat.getImgUrl());

                        mDataRefChat.child("Chat").child("Mensagens").child(chat.getKeyMessage()).setValue(hashMap);
                        chatListNotificacao(chat.getReceiver(),"Foto 📷", "Lhe enviou uma imagem");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Falha ao enviar a imagem.", Toast.LENGTH_SHORT).show();
                loadingDialog.dismissDialog();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pre_orcamento:
                int cont = 0;
                cont = cont + 1;
                if (cont == 1) {
                    Intent intent = new Intent(getApplicationContext(), PreOrcamentoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("id", id);
                    ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
                    ActivityCompat.startActivity(getApplicationContext(), intent, activityOptionsCompat.toBundle());
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void seenMessage(final String userid) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Chat").child("Mensagens");
        seenListener = mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (dataSnapshot.exists()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        assert chat != null;
                        if (chat.getReceiver().equals(mFirebaseUser.getUid()) && chat.getSender().equals(userid) && !chat.getReceiver().equals(userid) && !chat.getSender().equals(mFirebaseUser.getUid())) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            chat.setIsseen(true);
                            hashMap.put("isseen", chat.isIsseen());
                            //mDataRefChat.child("Chat").getRef().updateChildren(hashMap);
                            snapshot.getRef().updateChildren(hashMap);
                        } else {
                            chat.setIsseen(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String message, final String receiver, String sender, long dateMessage) {
        final DatabaseReference mDataRefChat = FirebaseDatabase.getInstance().getReference();
        final Chat chat = new Chat();
        final HashMap<String, Object> hashMap = new HashMap<>();
        chat.setKeyMessage(mDataRefChat.push().getKey());
        chat.setMessage(message);
        chat.setReceiver(receiver);
        chat.setSender(sender);
        chat.setMessageTime(dateMessage);
        chat.setIsseen(false);
        chat.setImgUrl("default");
        chat.setImgKey("default");

        hashMap.put("keyMessage", chat.getKeyMessage());
        hashMap.put("message", chat.getMessage());
        hashMap.put("receiver", chat.getReceiver());
        hashMap.put("sender", chat.getSender());
        hashMap.put("messageTime", chat.getMessageTime());
        hashMap.put("isseen", chat.isIsseen());
        hashMap.put("imgKey", chat.getImgKey());
        hashMap.put("imgUrl", chat.getImgUrl());
        mDataRefChat.child("Chat").child("Mensagens").child(chat.getKeyMessage()).setValue(hashMap);
        chatListNotificacao(receiver, message, "Nova Mensagem");

    }

    private void chatListNotificacao(final String receiver, String message, final String title) {

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist").child(mFirebaseUser.getUid()).child(id);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefMY = FirebaseDatabase.getInstance().getReference("Chatlist").child(id).child(mFirebaseUser.getUid());
        chatRefMY.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRefMY.child("id").setValue(mFirebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(USUARIO).child(mFirebaseUser.getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                if (notify) {
                    sendNotification(receiver, user.getNomeUser(), msg, title);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(String receiver, final String username, final String message, final String title) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(mFirebaseUser.getUid(), R.mipmap.ic_launcher, username + ": " + message, title, id);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(MessageActivity.this, "Falha", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessage(final String myId, final String userId, final long dateMessage) {
        mChat = new ArrayList<>();
        final DatabaseReference mDataBaseRead = FirebaseDatabase.getInstance().getReference("Chat").child("Mensagens");
        mDataBaseRead.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.exists()) {

                        final Chat chat = snapshot.getValue(Chat.class);
                        assert chat != null;

                        if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(myId)) {
                            mChat.add(chat);
                        }
                        messageAdapter = new MessageAdapter(MessageActivity.this, mChat, dateMessage);
                        recyclerView.setAdapter(messageAdapter);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void currentUser(String userid) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    private void status(String status) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(USUARIO).child(mFirebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        mDatabaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("On-line");
        currentUser(id);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Chat").child("Mensagens");
        mDatabaseReference.removeEventListener(seenListener);
        status("Off-line");
        currentUser("none");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Chat").child("Mensagens");
        mDatabaseReference.removeEventListener(seenListener);
        status("Off-line");
        currentUser("none");
    }

    @Override
    public void onBackPressed() {
        if (clicked) {
            clicked = false;
            ll_intent.setVisibility(View.GONE);
            fab_open_close.setImageResource(R.drawable.ic_clip);
        } else if (isImg) {
            isImg = false;
            rl_preview.setVisibility(View.GONE);
            Glide.with(getApplicationContext())
                    .load(R.mipmap.ic_launcher)
                    .placeholder(R.drawable.progress_animation)
                    .into(iv_preview_send_chat);
        } else {
            gotoChatActivity();
        }
    }

    private void gotoChatActivity() {
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(getApplicationContext(), intent, activityOptionsCompat.toBundle());
        finish();
    }
}
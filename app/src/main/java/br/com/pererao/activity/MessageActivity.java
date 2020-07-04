package br.com.pererao.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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

    CircleImageView profile_user_image_chat;
    TextView username_chat;
    ImageButton imb_send_message;
    TextInputLayout ti_ed_message_user;
    TextInputEditText ed_message_user;
    FirebaseUser mFirebaseUser;
    private static final String USUARIO = "Usuario";
    DatabaseReference mDatabaseReference;
    Intent intent;
    Toolbar toolbar;
    SharedPref sharedPref;
    String id;
    ValueEventListener seenListener;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;
    APIService apiService;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        sharedPref.CarregamentoTemaEscuro();
        setContentView(R.layout.activity_message);

        //TODO: DECLARAÇÕES
        imb_send_message = findViewById(R.id.btn_send_message);
        ti_ed_message_user = findViewById(R.id.ti_chat_message);
        ed_message_user = findViewById(R.id.et_chat_message);
        recyclerView = findViewById(R.id.recycler_view);
        profile_user_image_chat = findViewById(R.id.img_user_chat);
        username_chat = findViewById(R.id.username_chat);

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

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        imb_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msg = ti_ed_message_user.getEditText().getText().toString();
                if (!(TextUtils.isEmpty(msg))) {

                    long messageTime = new Date().getTime();

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
                    /*Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("id", id);
                    ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
                    ActivityCompat.startActivity(getApplicationContext(), intent, activityOptionsCompat.toBundle());
                    finish();*/
                    Toast.makeText(getApplicationContext(), "Pré-Orçamento", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void seenMessage(final String userid) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Chat");
        seenListener = mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (dataSnapshot.exists()) {
                        Chat chat = snapshot.getValue(Chat.class);
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
        DatabaseReference mDataRefChat = FirebaseDatabase.getInstance().getReference();
        Chat chat = new Chat(message, receiver, sender, dateMessage, false);

        mDataRefChat.child("Chat").push().setValue(chat);

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

        final String msg = message;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(USUARIO).child(mFirebaseUser.getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotification(receiver, user.getNomeUser(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(mFirebaseUser.getUid(), R.mipmap.ic_launcher, username + ": " + message, "Nova Mensagem", id);
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
        final DatabaseReference mDataBaseRead = FirebaseDatabase.getInstance().getReference("Chat");
        mDataBaseRead.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;

                    if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(myId)) {
                        mChat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, dateMessage);
                    recyclerView.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
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
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Chat");
        mDatabaseReference.removeEventListener(seenListener);
        status("Off-line");
        currentUser("none");
    }

    @Override
    public void onBackPressed() {
        gotoChatActivity();
    }

    private void gotoChatActivity() {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(getApplicationContext(), intent, activityOptionsCompat.toBundle());
        finish();
    }
}
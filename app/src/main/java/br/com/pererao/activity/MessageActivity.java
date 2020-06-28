package br.com.pererao.activity;

import android.content.Intent;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import br.com.pererao.R;
import br.com.pererao.SharedPref;
import br.com.pererao.activity.ui.chat.ChatActivity;
import br.com.pererao.adapter.MessageAdapter;
import br.com.pererao.model.Chat;
import br.com.pererao.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

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

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

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

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

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
                    long dateMessage = c.getMessageTime();
                    readMessage(mFirebaseUser.getUid(), id, user.getUserUrl(), dateMessage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        imb_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = ti_ed_message_user.getEditText().getText().toString();
                if (!(TextUtils.isEmpty(msg))) {

                    long messageTime = new Date().getTime();

                    sendMessage(msg, mFirebaseUser.getUid(), id, messageTime);
                } else {
                    Toast.makeText(getApplicationContext(), "Campo Vázio", Toast.LENGTH_SHORT).show();
                }
                ed_message_user.setText("");
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
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    private void sendMessage(String message, String receiver, String sender, long dateMessage) {
        DatabaseReference mDataRefChat = FirebaseDatabase.getInstance().getReference();
        Chat chat = new Chat(message, receiver, sender, dateMessage);

        mDataRefChat.child("Chat").push().setValue(chat);
    }

    private void readMessage(final String myId, final String userId, final String imageUrl, final long dateMessage) {
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

    @Override
    public void onBackPressed() {
        gotoChatActivity();
    }

    private void gotoChatActivity() {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(getApplicationContext(), intent, activityOptionsCompat.toBundle());
        finish();
    }
}
package br.com.pererao.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.snackbar.Snackbar;
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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import br.com.pererao.R;
import br.com.pererao.SharedPref;
import br.com.pererao.SnackBarCustom;
import br.com.pererao.adapter.MessageAdapter;
import br.com.pererao.model.Chat;
import br.com.pererao.model.User;

public class MessageActivity extends AppCompatActivity {

    ImageView profile_user_image_chat;
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
    String id, messageDate;
    boolean doubleBackToExitPressedOnce = false;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        if (sharedPref.CarregamentoTemaEscuro()) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
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
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoProfile();
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
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                username_chat.setText(user.getNomeUser());
                if (user.getUserUrl().equals("default")) {
                    Glide.with(getApplicationContext())
                            .load("https://firebasestorage.googleapis.com/v0/b/pererao2k20.appspot.com/o/user_photo%2Fman_user.png?alt=media")
                            .transform(new CircleCrop())
                            .into(profile_user_image_chat);
                } else {
                    Glide.with(getApplicationContext())
                            .load(user.getUserUrl())
                            .transform(new CircleCrop())
                            .into(profile_user_image_chat);
                }
                // }
                Chat c = dataSnapshot.getValue(Chat.class);
                assert c != null;
                //CharSequence cs = DateFormat.format("dd/MM/yyyy (HH:mm:ss)",c.getMessageTime());
                //String dateMessage = cs.toString();
                long dateMessage = c.getMessageTime();
                readMessage(mFirebaseUser.getUid(), id, user.getUserUrl(), dateMessage);

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
                    // CharSequence dateChar = DateFormat.format("dd/MM/yyyy (HH:mm:ss)", messageTime);
                    //String dateMessage = dateChar.toString();
                    //Log.i("DATESTRINGCHARSEQUENCE", "STRING: " + dateMessage + " CHAR: " + dateChar + " LONG: " + messageTime);

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
                    Intent intent = new Intent(MessageActivity.this, PreOrcamentoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id", id);
                    startActivity(intent);
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
                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageUrl, dateMessage);
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
            gotoProfile();
    }

    private void gotoProfile() {
        Intent intent = new Intent(MessageActivity.this, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
package br.com.pererao.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import br.com.pererao.R;
import br.com.pererao.activity.ui.APIService;
import br.com.pererao.adapter.ItemPreOrcamentoAdapter;
import br.com.pererao.adapter.MessageAdapter;
import br.com.pererao.model.Chat;
import br.com.pererao.model.ItemPreOrcamento;
import br.com.pererao.model.User;
import br.com.pererao.notifications.Client;
import br.com.pererao.notifications.Data;
import br.com.pererao.notifications.MyResponse;
import br.com.pererao.notifications.Sender;
import br.com.pererao.notifications.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreOrcamentoActivity extends AppCompatActivity {

    RelativeLayout relativeLayout;
    EditText et_date_val;
    TextView ttItens, tv_name_cliente, tv_name_prestador;
    String id;
    Intent intent;
    Toolbar toolbar;
    Calendar calendar = Calendar.getInstance();
    FloatingActionButton fab_addItem;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    DatabaseReference mDatabaseReference, mDatabaseReferenceMy;
    ItemPreOrcamentoAdapter mAdapter;
    List<ItemPreOrcamento> mItemPreOrcamento;
    LinearLayout ll_header;
    boolean notify = false;
    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preorcamento);

        relativeLayout = findViewById(R.id.relative_layout);
        et_date_val = findViewById(R.id.et_date_val);
        ttItens = findViewById(R.id.tv_total_val_itens);
        tv_name_cliente = findViewById(R.id.tv_name_cliente);
        tv_name_prestador = findViewById(R.id.tv_name_prestador);
        fab_addItem = findViewById(R.id.fab_add_preItem);
        recyclerView = findViewById(R.id.recycler_view);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        ll_header = findViewById(R.id.ll_header);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.pre_orcamento);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMessageActivity();
            }
        });

        intent = getIntent();
        id = intent.getStringExtra("id");

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Usuario").child(id);
        mDatabaseReferenceMy = FirebaseDatabase.getInstance().getReference("Usuario").child(mFirebaseUser.getUid());

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateEditText();
            }

        };

        et_date_val.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(PreOrcamentoActivity.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        fab_addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //createNewItem();
            }
        });

        final MediaPlayer send_message_sound = MediaPlayer.create(getApplicationContext(), R.raw.send_message);

        fab_addItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                notify = true;
                sendPreOrcamento(id, mFirebaseUser.getUid());
                send_message_sound.start();
                return false;
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
                    String name = "Cliente " + user.getNomeUser();
                    tv_name_cliente.setText(name);

                    // }
                    ItemPreOrcamento itemPreOrcamento = dataSnapshot.getValue(ItemPreOrcamento.class);
                    assert itemPreOrcamento != null;
                    itemPreOrcamento.setReceiver(mFirebaseUser.getUid());
                    loadItens(mFirebaseUser.getUid(), id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseReferenceMy.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    String name = "Prestador " + user.getNomeUser();
                    tv_name_prestador.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadItens(final String myId, final String userId) {
        mItemPreOrcamento = new ArrayList<>();
        final DatabaseReference mDataBaseRead = FirebaseDatabase.getInstance().getReference("Chat").child("PreOrcamento");
        mDataBaseRead.child("Itens").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mItemPreOrcamento.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.exists()) {

                        final ItemPreOrcamento itemPreOrcamento = snapshot.getValue(ItemPreOrcamento.class);
                        assert itemPreOrcamento != null;

                        if (itemPreOrcamento.getReceiver().equals(myId) && itemPreOrcamento.getSender().equals(userId) || itemPreOrcamento.getReceiver().equals(userId) && itemPreOrcamento.getSender().equals(myId)) {
                            mItemPreOrcamento.add(itemPreOrcamento);
                        }

                        mAdapter = new ItemPreOrcamentoAdapter(PreOrcamentoActivity.this, mItemPreOrcamento);
                        recyclerView.setAdapter(mAdapter);
                        if (mAdapter.getItemCount() > 0) {
                            ll_header.setVisibility(View.VISIBLE);
                        } else {
                            ll_header.setVisibility(View.GONE);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendPreOrcamento(final String receiver, final String sender){
        DatabaseReference mDataRefPreOrc = FirebaseDatabase.getInstance().getReference();
        ItemPreOrcamento itemPreOrcamento = new ItemPreOrcamento();
        HashMap<String,Object> hashMap = new HashMap<>();

        itemPreOrcamento.setKeyPreOrc(mDataRefPreOrc.push().getKey());
        itemPreOrcamento.setItem("TesteFunfa?");
        itemPreOrcamento.setReceiver(receiver);
        itemPreOrcamento.setSender(sender);

        hashMap.put("keyPreOrc", itemPreOrcamento.getKeyPreOrc());
        hashMap.put("item", itemPreOrcamento.getItem());
        hashMap.put("receiver", itemPreOrcamento.getReceiver());
        hashMap.put("sender", itemPreOrcamento.getSender());

        mDataRefPreOrc.child("Chat").child("PreOrcamento").child("Itens").child(itemPreOrcamento.getKeyPreOrc()).setValue(hashMap);


        final String msg = "lhe enviou uma proposta de orçamento";
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Usuario").child(mFirebaseUser.getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
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
                    Data data = new Data(mFirebaseUser.getUid(), R.mipmap.ic_launcher, username + " " + message, "Pré Orçamento", id);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(PreOrcamentoActivity.this, "Falha", Toast.LENGTH_SHORT).show();
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

    private void createNewItem() {
        Intent intent = new Intent(getApplicationContext(), InsertPreOrcamento.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("id", id);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(getApplicationContext(), intent, activityOptionsCompat.toBundle());
        finish();
    }

    private void updateEditText() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
        et_date_val.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public void onBackPressed() {
        gotoMessageActivity();
    }

    private void gotoMessageActivity() {
        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("id", id);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(getApplicationContext(), intent, activityOptionsCompat.toBundle());
        finish();
    }

    /*private void status(String status) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(USUARIO).child(mFirebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        mDatabaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("On-line");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("Off-line");
    }*/

}
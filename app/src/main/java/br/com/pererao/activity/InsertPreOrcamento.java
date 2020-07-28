package br.com.pererao.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import br.com.pererao.R;
import br.com.pererao.SharedPref;
import br.com.pererao.adapter.ItemPreOrcamentoAdapter;
import br.com.pererao.model.ItemPreOrcamento;

public class InsertPreOrcamento extends AppCompatActivity {

    SharedPref sharedPref;
    EditText et_item, et_item_qtde, et_item_val;
    int qtdeItem = 0;
    FirebaseUser mFirebaseUser;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference mDatabaseReference, mDatabasePre;
    private final static String USUARIO = "Usuario";
    Toolbar toolbar;
    Intent intent;
    String id;
    FloatingActionButton fab_add_itens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        sharedPref.CarregamentoTemaEscuro();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertpreorcamento);

        et_item = findViewById(R.id.et_item);
        et_item_qtde = findViewById(R.id.et_item_qtde);
        et_item_val = findViewById(R.id.et_item_value);
        fab_add_itens = findViewById(R.id.fab_add_itens);

        intent = getIntent();
        id = intent.getStringExtra("id");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(USUARIO).child(mFirebaseUser.getUid());
        mDatabasePre = FirebaseDatabase.getInstance().getReference("Chat").child("PreOrcamento");

        //ToolBar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.pre_orcamento);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPreOrcamentoActivity();
            }
        });

        fab_add_itens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item = et_item.getText().toString();
                int qtde = Integer.parseInt(et_item_qtde.getText().toString());
                double val = Double.parseDouble(et_item_val.getText().toString());
                HashMap<String, Object> hashMap = new HashMap<>();
                ItemPreOrcamento itemPreOrcamento = new ItemPreOrcamento();
                itemPreOrcamento.setKeyPreOrc(mDatabasePre.push().getKey());
                itemPreOrcamento.setItem(item);
                itemPreOrcamento.setQtdeItem(qtde);
                itemPreOrcamento.setValorItem(val);
                double sub = itemPreOrcamento.getQtdeItem() * itemPreOrcamento.getValorItem();
                itemPreOrcamento.setSubTotal(sub);

                hashMap.put("keyPreOrc", itemPreOrcamento.getKeyPreOrc());
                hashMap.put("item", itemPreOrcamento.getItem());
                hashMap.put("qtdeItem", itemPreOrcamento.getQtdeItem());
                hashMap.put("valorItem", itemPreOrcamento.getValorItem());
                hashMap.put("subTotal", itemPreOrcamento.getSubTotal());
                Toast.makeText(getApplicationContext(), "Item adicionado", Toast.LENGTH_SHORT).show();
                mDatabasePre.child("Itens").child(itemPreOrcamento.getKeyPreOrc()).setValue(hashMap);

            }
        });

    }

    private void status(String status) {
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
    }

    public void onBackPressed() {
        gotoPreOrcamentoActivity();
    }

    private void gotoPreOrcamentoActivity() {
        Intent intent = new Intent(getApplicationContext(), PreOrcamentoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("id", id);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(getApplicationContext(), intent, activityOptionsCompat.toBundle());
        finish();
    }

}
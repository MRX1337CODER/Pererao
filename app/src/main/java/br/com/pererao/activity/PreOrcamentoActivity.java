package br.com.pererao.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import br.com.pererao.R;

public class PreOrcamentoActivity extends AppCompatActivity {

    RelativeLayout relativeLayout;
    String id;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preorcamento);

        relativeLayout = findViewById(R.id.relative_layout);

        intent = getIntent();
        id = intent.getStringExtra("id");
    }

    @Override
    public void onBackPressed() {
        gotoMessageActivity();
    }

    private void gotoMessageActivity() {
        Intent intent = new Intent(PreOrcamentoActivity.this, MessageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("id", id);
        startActivity(intent);
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

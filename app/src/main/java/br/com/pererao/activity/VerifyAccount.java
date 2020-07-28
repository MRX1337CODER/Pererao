package br.com.pererao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import br.com.pererao.R;

public class VerifyAccount extends AppCompatActivity {
    Button btn_resendCode;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    String UserID;
    RelativeLayout relativeLayout;
    private static final String TAG = "VerifyAccount";
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyaccount);

        //TODO: Declarações
        relativeLayout = findViewById(R.id.rl_verifyAccount);
        btn_resendCode = findViewById(R.id.btn_resend_code);
        toolbar = findViewById(R.id.toolbar);

        //Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        UserID = FirebaseAuth.getInstance().getUid();

        //Toolbar
        toolbar.setTitle("Verificar Conta");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLogin();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //FirebaseUser Fuser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            mFirebaseUser.reload();
            if (mFirebaseUser.isEmailVerified()) {
                gotoDashboard();
            } else {
                resendCode();
            }

        }

    }

    private void resendCode() {
        btn_resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mFirebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(v.getContext(), "E-mail De Verificação Foi Enviado, Clique Sobre Ele Para Confirar", Toast.LENGTH_SHORT).show();
                        gotoLogin();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Falha: E-mail não foi enviado " + e.getMessage());
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        gotoLogin();
    }

    private void gotoLogin() {
        Intent intent = new Intent(VerifyAccount.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void gotoDashboard() {
        Intent intent = new Intent(VerifyAccount.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
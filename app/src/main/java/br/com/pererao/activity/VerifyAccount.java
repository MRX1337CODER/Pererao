package br.com.pererao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import br.com.pererao.R;
import br.com.pererao.SnackBarCustom;

public class VerifyAccount extends AppCompatActivity {
    Button btn_resendCode;
    FirebaseAuth mFirebaseAuth, auth;
    FirebaseFirestore mFirebaseFirestore;
    FirebaseUser mUser;
    String UserID;
    RelativeLayout relativeLayout;
    private static final String TAG = "VerifyAccount";
    boolean doubleBackToExitPressedOnce = false;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyaccount);

        //TODO: Declarações
        relativeLayout = findViewById(R.id.rl_verifyAccount);
        auth = FirebaseAuth.getInstance();
        btn_resendCode = findViewById(R.id.btn_resend_code);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        UserID = FirebaseAuth.getInstance().getUid();
        mUser = mFirebaseAuth.getCurrentUser();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Verificar Conta");
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //TODO: Ações

        VerifyAuthentication();

        if (user != null) {
            boolean emailVerified = user.isEmailVerified();
            if (!emailVerified) {
                btn_resendCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(v.getContext(), "E-mail De Verificação Foi Enviado, Clique Sobre Ele Para Confirar", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Falha: E-mail não foi enviado " + e.getMessage());
                            }
                        });
                    }
                });
            } if (emailVerified) {
                Intent intent = new Intent(VerifyAccount.this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
        else {
            Intent intent = new Intent(VerifyAccount.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        VerifyAuthentication();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        SnackBarCustom.Snack(getApplication(), relativeLayout, "Pressione Voltar Novamente Para Sair", Snackbar.LENGTH_SHORT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void VerifyAuthentication() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (FirebaseAuth.getInstance().getUid() == null || user == null) {
            Intent intent = new Intent(VerifyAccount.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
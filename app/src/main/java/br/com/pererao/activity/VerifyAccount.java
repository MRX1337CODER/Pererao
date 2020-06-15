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

import java.util.Objects;

import br.com.pererao.R;
import br.com.pererao.SnackBarCustom;

public class VerifyAccount extends AppCompatActivity {
    Button btn_resendCode;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
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
        btn_resendCode = findViewById(R.id.btn_resend_code);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        UserID = FirebaseAuth.getInstance().getUid();
        toolbar = findViewById(R.id.toolbar);

        //Toolbar
        toolbar.setTitle("Verificar Conta");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
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
        FirebaseUser Fuser = mFirebaseAuth.getCurrentUser();
        Fuser.reload();
        if (Fuser == null){
            Log.i(TAG, "Sem Usuário");
            gotoLogin();
        }
        else if (Fuser.isEmailVerified() || mFirebaseAuth.getCurrentUser().isEmailVerified()){
            Log.i(TAG, "E-mail verificado");
            gotoDashboard();
        }
        else if (!Fuser.isEmailVerified() || !mFirebaseAuth.getCurrentUser().isEmailVerified()){
            Log.i(TAG, "E-mail NÂO verificado");
            resendCode();
        }

        /*if (mFirebaseUser == null){
            Log.i(TAG, "User Nulo");
            gotoLogin();
        }
        else if (mFirebaseAuth == null){
            Log.i(TAG, "Auth Nulo");
            gotoLogin();
        }
        else if (mFirebaseUser != null && !mFirebaseUser.isEmailVerified()){
            Log.i(TAG, "User N Nulo && E-mail não verificado");
            resendCode();
        }
        else if (mFirebaseAuth != null && !mFirebaseUser.isEmailVerified()){
            Log.i(TAG, "Auth N Nulo e E-mail n verificado");
            resendCode();
        }
        else if (mFirebaseUser != null && mFirebaseUser.isEmailVerified() || mFirebaseAuth.getCurrentUser() != null && mFirebaseUser.isEmailVerified()){
            Log.i(TAG, "E-mail verificado <<<<<<<<<");
            gotoDashboard();
        }*/


       /* if (mFirebaseUser == null) {
            gotoLogin();
        } else if (mFirebaseAuth == null){
            gotoLogin();
        }
        else {
            boolean ev = mFirebaseUser.isEmailVerified();
            boolean ev2 = mFirebaseAuth.getCurrentUser().isEmailVerified();
            Log.i(TAG, "emailUser:" + ev + "\nemailAuth:" + ev2);
            if (mFirebaseAuth.getCurrentUser().isEmailVerified() || mFirebaseUser.isEmailVerified()) {
                gotoDashboard();
            } else {
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
        }*/

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

    private void gotoLogin() {
        Intent intent = new Intent(VerifyAccount.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void gotoDashboard() {
        Intent intent = new Intent(VerifyAccount.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
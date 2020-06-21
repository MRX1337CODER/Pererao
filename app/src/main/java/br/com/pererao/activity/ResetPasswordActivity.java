package br.com.pererao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import br.com.pererao.Network;
import br.com.pererao.R;
import br.com.pererao.SharedPref;
import br.com.pererao.SnackBarCustom;
import br.com.pererao.activity.ui.configuration.Configuration;

public class ResetPasswordActivity extends AppCompatActivity {

    TextInputEditText et_new_password;
    TextInputLayout ti_et_new_password;
    ImageButton imb_NewPassCode;
    MaterialButton btn_goto;
    RelativeLayout relativeLayout;
    private static final String TAG = "ResetPassActivityTAG";
    LoadingDialog loadingDialog = new LoadingDialog(ResetPasswordActivity.this);
    Toolbar toolbar;
    SharedPref sharedPref;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Tema Escuro**/
        sharedPref = new SharedPref(this);
        if (sharedPref.CarregamentoTemaEscuro()) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //TODO: Declarações
        et_new_password = findViewById(R.id.et_pass);
        ti_et_new_password = findViewById(R.id.ti_pass);

        btn_goto = findViewById(R.id.btn_goto);
        imb_NewPassCode = findViewById(R.id.btn_newPass);
        relativeLayout = findViewById(R.id.rl_newPass);
        toolbar = findViewById(R.id.toolbar);

        //Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();

        toolbar.setTitle(R.string.reset_password);
        //ToolBar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO: Ações
        //Restaurar Senha
        imb_NewPassCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyNet();
            }
        });

        //Voltar Tela De Conexão
        btn_goto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        gotoActivity();
    }

    private void VerifyNet() {
        if (Network.isConnected(getApplication())) {
            NewPassCode();
        } else {
            SnackBarCustom.SnackSetAction(getApplication(), relativeLayout, R.color.snackBackground, false, "Sem Rede Disponível", "Tente Novamente", Snackbar.LENGTH_INDEFINITE, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Network.isConnected(getApplication())) {
                        VerifyNet();
                    }
                }
            });
        }
    }

    private void NewPassCode() {
        String old_pass = ti_et_new_password.getEditText().getText().toString();

        if (!validateEmailResetPass()) {
            return;
        }
        try {
            loadingDialog.startLoadingDialog();
            mFirebaseAuth.sendPasswordResetEmail(old_pass).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    loadingDialog.dismissDialog();
                    Toast.makeText(getApplicationContext(), "O LINK Foi Enviado Para Seu E-mail", Toast.LENGTH_SHORT).show();
                    gotoActivity();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loadingDialog.dismissDialog();
                    Toast.makeText(getApplicationContext(), "Erro! O LINK Não Pode Ser Enviado Pois Este E-mail É Inválido", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e);
        }

    }

    private boolean validateEmailResetPass() {
        String val = ti_et_new_password.getEditText().getText().toString().trim();
        //String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(val)) {
            ti_et_new_password.setError("E-mail Obrigatório");
            ti_et_new_password.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(val).matches()) {//!val.matches(checkEmail)) {
            ti_et_new_password.setError("E-mail Inválido");
            ti_et_new_password.requestFocus();
            return false;
        } else {
            ti_et_new_password.setError(null);
            ti_et_new_password.setErrorEnabled(false);
            return true;
        }
    }

    private void gotoActivity() {
        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(ResetPasswordActivity.this, intent, activityOptionsCompat.toBundle());
        finish();
    }
}
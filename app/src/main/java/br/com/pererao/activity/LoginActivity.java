package br.com.pererao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.pererao.Network;
import br.com.pererao.R;
import br.com.pererao.SharedPref;
import br.com.pererao.SnackBarCustom;

public class LoginActivity extends AppCompatActivity {

    SharedPref sharedPref;
    TextInputEditText et_email, et_password;
    TextInputLayout ti_et_email, ti_et_password;
    ImageButton btn_SingIn;
    RelativeLayout relativeLayout;
    MaterialButton btn_new_account, btn_forgot_password;
    private static final String TAG = "LoginActivityTAG";
    LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
    boolean doubleBackToExitPressedOnce = false;
    Toolbar toolbar;

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Revoke PrintScreen**/
        //     getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
        //           WindowManager.LayoutParams.FLAG_SECURE);
        /*Tema Escuro**/
        sharedPref = new SharedPref(this);
        if (sharedPref.CarregamentoTemaEscuro()) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TODO: Declarações
        ti_et_email = findViewById(R.id.ti_email_login);
        ti_et_password = findViewById(R.id.ti_password_login);
        et_email = findViewById(R.id.et_email_login);
        et_password = findViewById(R.id.et_password_login);
        btn_new_account = findViewById(R.id.btn_new_account);
        btn_forgot_password = findViewById(R.id.btn_forgot_password);
        btn_SingIn = findViewById(R.id.btn_submit_login);
        relativeLayout = findViewById(R.id.rl_login);
        toolbar = findViewById(R.id.toolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        //ToolBar
        toolbar.setTitle(R.string.tv_login);
        setSupportActionBar(toolbar);


        //TODO: Ações
        //TODO: Conectar(Login)
        btn_SingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyNet();
            }
        });

        //TODO: Nova Conta
        btn_new_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newAccount();
            }
        });

        //TODO: "Esqueci Minha Senha"
        btn_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPass();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser Fuser = mFirebaseAuth.getCurrentUser();
        if (Fuser != null) {
            Fuser.reload();
            if (!mFirebaseUser.isEmailVerified()) {
                Log.i(TAG, "Com Usuário e E-mail não verificado");
                gotoVerifyAccount();
            } else {
                Log.i(TAG, "Com Usuário e E-mail verificado");
                gotoDashboardActivity();
            }
        } else {
            Log.i(TAG, "Sem Usuário");
        }
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

    private void VerifyNet() {
        if (Network.isConnected(getApplication())) {
            ConfirmaCampos();
        } else {
            SnackBarCustom.SnackSetAction(getApplication(), relativeLayout, R.color.snackBackground, false, "Sem Rede Disponível", "Conectar", Snackbar.LENGTH_INDEFINITE, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
        }
    }

    /**
     * Confirma se os campos se coincídem
     */
    private void ConfirmaCampos() {
        String email = ti_et_email.getEditText().getText().toString().trim();
        String password = ti_et_password.getEditText().getText().toString().trim();
        //Verifica se os campos não estão vazios
        if (!validateEmailLogin() | !validatePasswordLogin()) {
            return;
        }
        ConectarLogin(email, password);
    }

    private void ConectarLogin(String email, String password) {
        loadingDialog.startLoadingDialog();
        try {
            //Busca o email e a senha no firebase, se existir faz o login, senão ele da erro
            mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Se der tudo certo
                        LimparCampos();
                        Toast.makeText(getApplicationContext(), "Conectado Com Sucesso", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismissDialog();
                        gotoVerifyAccount();

                    } else {
                        //Aqui mostra o erro
                        Toast.makeText(getApplicationContext(), "Erro! Não Há Registro Correspondente A Esse Identificador, O Usuário Pode Ter sido Excluído", Toast.LENGTH_LONG).show();
                        //LimparCampos();
                        loadingDialog.dismissDialog();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e);
        }
    }

    private boolean validateEmailLogin() {
        String val = ti_et_email.getEditText().getText().toString().trim();
        //String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(val)) {
            ti_et_email.setError("E-mail Obrigatório");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(val).matches()) {//!val.matches(checkEmail)) {
            ti_et_email.setError("E-mail Inválido");
            return false;
        } else {
            ti_et_email.setError(null);
            ti_et_email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePasswordLogin() {
        String val = ti_et_password.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(val)) {
            ti_et_password.setError("Senha Obrigatório");
            return false;
        } else if (et_password.length() < 8) {
            ti_et_password.setError("A Senha Deve Conter No Mínimo 8 Caracteres");
            return false;
        } else {
            ti_et_password.setError(null);
            ti_et_password.setErrorEnabled(false);
            return true;
        }
    }

    private void gotoVerifyAccount() {
        Intent intent = new Intent(LoginActivity.this, VerifyAccount.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void gotoDashboardActivity() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void LimparCampos() {
        et_email.setText("");
        et_password.setText("");
    }

    private void newAccount() {
        btn_new_account.setEnabled(false);
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(LoginActivity.this, intent, activityOptionsCompat.toBundle());
        finish();
    }

    private void ResetPass() {
        btn_forgot_password.setEnabled(false);
        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
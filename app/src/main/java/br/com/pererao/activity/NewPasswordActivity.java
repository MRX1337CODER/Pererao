package br.com.pererao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import br.com.pererao.Network;
import br.com.pererao.R;
import br.com.pererao.SharedPref;
import br.com.pererao.SnackBarCustom;
import br.com.pererao.model.User;
import br.com.pererao.ui.configuration.ConfigurationFragment;

public class NewPasswordActivity extends AppCompatActivity {

    TextInputEditText et_new_password, et_old_password;
    TextInputLayout ti_et_new_password, ti_et_old_password;
    ImageButton imb_NewPassCode;
    MaterialButton btn_goto;
    RelativeLayout relativeLayout;
    private static final String TAG = "ResetPassActivityTAG";
    LoadingDialog loadingDialog = new LoadingDialog(NewPasswordActivity.this);
    Toolbar toolbar;
    SharedPref sharedPref;
    Intent intent;
    String passIntent;
    TextView tv_newPassAct;
    private static final String USUARIO = "Usuario";
    DatabaseReference mDatabaseReference;
    String oldPassDatabase;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

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
        setContentView(R.layout.activity_new_password);

        //TODO: Declarações
        et_new_password = findViewById(R.id.et_new_pass);
        et_old_password = findViewById(R.id.et_old_pass);

        ti_et_new_password = findViewById(R.id.ti_new_pass);
        ti_et_old_password = findViewById(R.id.ti_old_pass);

        btn_goto = findViewById(R.id.btn_goto);
        imb_NewPassCode = findViewById(R.id.btn_newPass);
        relativeLayout = findViewById(R.id.rl_newPass);
        tv_newPassAct = findViewById(R.id.tv_email_or_password);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        toolbar = findViewById(R.id.toolbar);

        intent = getIntent();
        passIntent = intent.getStringExtra("passIntentString");

        assert passIntent != null;
        if (passIntent.equals("RestaurarSenha")) {
            toolbar.setTitle(R.string.reset_password);
            tv_newPassAct.setText(R.string.email_forgot_pass);
            String hint = getString(R.string.hint_email);
            ti_et_old_password.setHint(hint);
            ti_et_old_password.setStartIconDrawable(R.drawable.ic_email_outline_black_24dp);
            ti_et_old_password.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
            et_old_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        } else {
            if (mFirebaseUser != null) {
                toolbar.setTitle("Nova Senha");
                tv_newPassAct.setText(R.string.new_password);
                //String hint = getString();
                ti_et_new_password.setVisibility(View.VISIBLE);
                ti_et_new_password.setHint("Nova Senha");
                ti_et_new_password.setStartIconDrawable(R.drawable.ic_lock_outline_black_24dp);
                ti_et_new_password.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                et_new_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                ti_et_old_password.setHint("Senha Atual");
                ti_et_old_password.setStartIconDrawable(R.drawable.ic_lock_outline_black_24dp);
                ti_et_old_password.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                et_old_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                gotoActivity();
            }
        }

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
        assert passIntent != null;
        if (passIntent.equals("RestaurarSenha")) {
            if (id == android.R.id.home) {
                Intent intent = new Intent(NewPasswordActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        } else {
            if (id == android.R.id.home) {
                Intent intent = new Intent(NewPasswordActivity.this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
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
        String old_pass = ti_et_old_password.getEditText().getText().toString();

        assert passIntent != null;
        if (passIntent.equals("RestaurarSenha")) {
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

        } else {
            if (!validateNewPassword() | !validateOldPassword()) {
                return;
            } else if (!validateEqualsPass()) {
                return;
            }
            try {
                loadingDialog.startLoadingDialog();
                String UserPassword = ti_et_old_password.getEditText().getText().toString();
                UserReauthentication(UserPassword);
            } catch (Exception e) {
                Log.e(TAG, "Error New Pass: " + e);
            }

        }

    }

    private boolean validateEmailResetPass() {
        String val = ti_et_old_password.getEditText().getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (TextUtils.isEmpty(val)) {
            ti_et_old_password.setError("E-mail Obrigatório");
            ti_et_old_password.requestFocus();
            return false;
        } else if (!val.matches(checkEmail)) {
            ti_et_old_password.setError("E-mail Inválido");
            ti_et_old_password.requestFocus();
            return false;
        } else {
            ti_et_old_password.setError(null);
            ti_et_old_password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateNewPassword() {
        final String val = ti_et_new_password.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(val)) {
            ti_et_new_password.setError("Nova Senha Obrigatório");
            ti_et_new_password.requestFocus();
            return false;
        } else if (et_new_password.length() < 8) {
            ti_et_new_password.setError("A Nova Senha Deve Conter No Mínimo 8 Caracteres");
            ti_et_new_password.requestFocus();
            return false;
        } else {
            ti_et_new_password.setError(null);
            ti_et_new_password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateOldPassword() {
        final String val = ti_et_old_password.getEditText().getText().toString().trim();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(USUARIO).child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                oldPassDatabase = user.getSenhaUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (TextUtils.isEmpty(val)) {
            ti_et_old_password.setError("Senha Atual Obrigatório");
            ti_et_old_password.requestFocus();
            return false;
        } else if (!(val.equals(oldPassDatabase))) {
            ti_et_old_password.setError("Senha Incorreta");
            ti_et_old_password.requestFocus();
            return false;
        } else {
            ti_et_old_password.setError(null);
            ti_et_old_password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEqualsPass() {
        String val = ti_et_new_password.getEditText().getText().toString().trim();
        String val2 = ti_et_old_password.getEditText().getText().toString().trim();

        if (val.equals(val2)) {
            ti_et_new_password.setError("Sua Nova Senha Não Pode Ser Idêntica A Atual!");
            ti_et_new_password.requestFocus();
            return false;
        } else {
            ti_et_new_password.setError(null);
            ti_et_new_password.setErrorEnabled(false);
            return true;
        }
    }

    private void UserReauthentication(String UserPassword) {
        String userEmail = mFirebaseUser.getEmail();
        final String newPass = ti_et_new_password.getEditText().getText().toString();
        assert userEmail != null;
        AuthCredential credential = EmailAuthProvider
                .getCredential(userEmail, UserPassword);

        mFirebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Re-autenticação Feita Com Sucesso.", Toast.LENGTH_SHORT).show();
                    ChangePassword(newPass);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Re-autenticação Infelizmente Falhou.", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissDialog();
                }
            }
        });
    }

    private void ChangePassword(final String newPass) {
        mFirebaseUser.updatePassword(newPass)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mDatabaseReference = FirebaseDatabase.getInstance().getReference(USUARIO + "/" + mFirebaseUser.getUid());
                            Map<String, Object> map = new HashMap<>();
                            map.put("senhaUser", newPass);
                            mDatabaseReference.updateChildren(map);

                            Toast.makeText(getApplicationContext(), "Sua Senha Foi Alterado Com Êxito", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissDialog();
                            mFirebaseAuth.signOut();
                            Intent intent = new Intent(NewPasswordActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    private void gotoActivity() {
        Intent intentAc;
        if (passIntent.equals("RestaurarSenha")) {
            intentAc = new Intent(NewPasswordActivity.this, LoginActivity.class);
        } else {
            intentAc = new Intent(NewPasswordActivity.this, ProfileActivity.class);
        }
        intentAc.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentAc);
        NewPasswordActivity.this.finish();
    }
}
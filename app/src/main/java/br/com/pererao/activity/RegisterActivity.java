package br.com.pererao.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import br.com.pererao.Network;
import br.com.pererao.R;
import br.com.pererao.SharedPref;
import br.com.pererao.SnackBarCustom;
import br.com.pererao.model.User;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText et_fullname, et_email, et_confirm_email, et_password, et_confirm_password;
    TextInputLayout ti_et_name, ti_et_email, ti_et_confirm_email, ti_et_password, ti_et_confirm_password;
    ImageButton imb_register;
    MaterialButton btn_goto_login;
    String UserID;
    static final String USUARIO = "Usuario";
    RelativeLayout relativeLayout;
    private static final String TAG = "RegisterActivityTAG";
    LoadingDialog loadingDialog = new LoadingDialog(RegisterActivity.this);
    SharedPref sharedPref;
    Toolbar toolbar;
    boolean doubleBackToExitPressedOnce = false;
    FloatingActionButton fab;
    private float CLICK_DRAG_TOLERANCE = 15, downRawX, downRawY, dX, dY;
    User user;
    //Firebase
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Revoke PrintScreen**/
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
        //      WindowManager.LayoutParams.FLAG_SECURE);
        /*Tema Escuro**/
        sharedPref = new SharedPref(this);
        if (sharedPref.CarregamentoTemaEscuro()) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_register);

        //TODO: Declarações
        fab = findViewById(R.id.fab);

        ti_et_name = findViewById(R.id.ti_username_register);
        ti_et_email = findViewById(R.id.ti_email_register);
        ti_et_confirm_email = findViewById(R.id.ti_confirm_email_register);
        ti_et_password = findViewById(R.id.ti_password_register);
        ti_et_confirm_password = findViewById(R.id.ti_confirm_password_register);

        et_fullname = findViewById(R.id.et_username_register);
        et_email = findViewById(R.id.et_email_register);
        et_confirm_email = findViewById(R.id.et_confirm_email_register);
        et_password = findViewById(R.id.et_password_register);
        et_confirm_password = findViewById(R.id.et_confirm_password_register);

        btn_goto_login = findViewById(R.id.btn_goto_login);
        imb_register = findViewById(R.id.imb_register);

        relativeLayout = findViewById(R.id.rl_register);
        //Firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference(USUARIO);
        mFirebaseAuth = FirebaseAuth.getInstance();

        //ToolBar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.tv_register);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        //Setando UID aleatorio pelo firebase
        FirebaseUser usernull = FirebaseAuth.getInstance().getCurrentUser();
        if (usernull != null) {
            UserID = usernull.getUid();
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onStart() {
        super.onStart();

        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {

                    downRawX = motionEvent.getRawX();
                    downRawY = motionEvent.getRawY();
                    dX = view.getX() - downRawX;
                    dY = view.getY() - downRawY;

                    return true; // Consumed

                } else if (action == MotionEvent.ACTION_MOVE) {

                    int viewWidth = view.getWidth();
                    int viewHeight = view.getHeight();

                    View viewParent = (View) view.getParent();
                    int parentWidth = viewParent.getWidth();
                    int parentHeight = viewParent.getHeight();

                    float newX = motionEvent.getRawX() + dX;
                    newX = Math.max(layoutParams.leftMargin, newX); // Don't allow the FAB past the left hand side of the parent
                    newX = Math.min(parentWidth - viewWidth - layoutParams.rightMargin, newX); // Don't allow the FAB past the right hand side of the parent

                    float newY = motionEvent.getRawY() + dY;
                    newY = Math.max(layoutParams.topMargin, newY); // Don't allow the FAB past the top of the parent
                    newY = Math.min(parentHeight - viewHeight - layoutParams.bottomMargin, newY); // Don't allow the FAB past the bottom of the parent

                    view.animate()
                            .x(newX)
                            .y(newY)
                            .setDuration(0)
                            .start();

                    return true; // Consumed

                } else if (action == MotionEvent.ACTION_UP) {

                    float upRawX = motionEvent.getRawX();
                    float upRawY = motionEvent.getRawY();

                    float upDX = upRawX - downRawX;
                    float upDY = upRawY - downRawY;

                    if (Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE) { // A click
                        goto_login();

                    } else { // A drag
                        return true; // Consumed
                    }

                } else {
                    goto_login();
                }

                return true;
            }
        });

        /*Botão do cadastro*/
        imb_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyNet();
            }
        });

        /*Voltar para a activity de login*/
        btn_goto_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goto_login();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            goto_login();
        }

        return super.onOptionsItemSelected(item);
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
            SnackBarCustom.Snack(getApplication(), relativeLayout, "Sem Internet No Momento", Snackbar.LENGTH_LONG);
        }
    }

    /**
     * Confirma se os campos se coincídem
     */
    private void ConfirmaCampos() {
        final String name = ti_et_name.getEditText().getText().toString();
        final String email = ti_et_email.getEditText().getText().toString().trim();
        String password = ti_et_password.getEditText().getText().toString().trim();

        if (!validateUsernameRegister() | !validateEmailRegister() | !validatePasswordRegister() | !validateConfirmEmailRegister() | !validateConfirmPasswordRegister()) {
            return;
        } else if (!validateEqualsEmail() | !validateEqualsPass()) {
            return;
        }
        loadingDialog.startLoadingDialog();
        ConfirmRegister(name, email, password);

    }

    private void ConfirmRegister(String name, String email, String password) {
        try {
            CreateUser(name, email, password);
        } catch (Exception e) {
            Log.i("Error", "Error: " + e.toString());
        }
    }

    private boolean validateUsernameRegister() {
        String val = ti_et_name.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(val)) {
            ti_et_name.setError("Nome Obrigatório");
            ti_et_name.requestFocus();
            return false;
        } else {
            ti_et_name.setError(null);
            ti_et_name.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmailRegister() {
        String val = ti_et_email.getEditText().getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(val)) {
            ti_et_email.setError("E-mail Obrigatório");
            ti_et_email.requestFocus();
            return false;
        } else if (!val.matches(checkEmail)) {
            ti_et_email.setError("E-mail Inválido");
            ti_et_email.requestFocus();
            return false;
        } else {
            ti_et_email.setError(null);
            ti_et_email.setErrorEnabled(false);
            return true;
        }
    }

    public boolean validateConfirmEmailRegister() {
        String val = ti_et_confirm_email.getEditText().getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(val)) {
            ti_et_confirm_email.setError("Confirmação De E-mail Obrigatório");
            ti_et_confirm_email.requestFocus();
            return false;
        } else if (!val.matches(checkEmail)) {
            ti_et_confirm_email.setError("E-mail Inválido");
            ti_et_confirm_email.requestFocus();
            return false;
        } else {
            ti_et_confirm_email.setError(null);
            ti_et_confirm_email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePasswordRegister() {
        String val = ti_et_password.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(val)) {
            ti_et_password.setError("Senha Obrigatório");
            ti_et_password.requestFocus();
            return false;
        } else if (et_password.length() < 8) {
            ti_et_password.setError("A Senha Deve Conter No Mínimo 8 Caracteres");
            ti_et_password.requestFocus();
            return false;
        } else {
            ti_et_password.setError(null);
            ti_et_password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateConfirmPasswordRegister() {
        String val = ti_et_confirm_password.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(val)) {
            ti_et_confirm_password.setError("Confirmação De Senha Obrigatório");
            ti_et_confirm_password.requestFocus();
            return false;
        } else if (et_confirm_password.length() < 8) {
            ti_et_confirm_password.setError("A Confirmação De Senha Deve Conter No Mínimo 8 Caracteres");
            ti_et_confirm_password.requestFocus();
            return false;
        } else {
            ti_et_confirm_password.setError(null);
            ti_et_confirm_password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEqualsEmail() {
        String val = ti_et_email.getEditText().getText().toString().trim();
        String val2 = ti_et_confirm_email.getEditText().getText().toString().trim();

        if (!(val.equals(val2))) {
            ti_et_confirm_email.setError("E-mail Não Coincíde");
            ti_et_confirm_email.requestFocus();
            return false;
        } else {
            ti_et_confirm_email.setError(null);
            ti_et_confirm_email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEqualsPass() {
        String val = ti_et_password.getEditText().getText().toString().trim();
        String val2 = ti_et_confirm_password.getEditText().getText().toString().trim();

        if (!(val.equals(val2))) {
            ti_et_confirm_password.setError("Senha Não Coincíde");
            ti_et_confirm_password.requestFocus();
            return false;
        } else {
            ti_et_confirm_password.setError(null);
            ti_et_confirm_password.setErrorEnabled(false);
            return true;
        }
    }

    /**
     * Cadastro no Firebase
     */
    public void CreateUser(final String name, final String email, final String password) {
        try {
            mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Envia informações para a updateUserInfo
                        loadingDialog.dismissDialog();

                        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        assert mFirebaseUser != null;
                        String id = mFirebaseUser.getUid();

                        if (mFirebaseUser != null) {
                            boolean emailVerified = mFirebaseUser.isEmailVerified();
                            if (!emailVerified) {
                                Toast.makeText(getApplicationContext(), "E-mail não verificado", Toast.LENGTH_LONG).show();
                                Log.i(TAG, "E-mail Não Verificado");
                            } else if (emailVerified){
                                Toast.makeText(getApplicationContext(), "E-mail já verificado", Toast.LENGTH_LONG).show();
                                Log.i(TAG, "E-mail Já Verificado");
                            }
                            mFirebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "E-mail De Verificação Enviado, Clique Sobre Ele Para Confirmar", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Sucesso: E-mail Foi Enviado Com Sucesso");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Falha: E-mail não foi enviado " + e.getMessage());
                                }
                            });
                        }

                        String userUrl = "default";
                        String status = "offline";
                        String search = name.toLowerCase();
                        float rating = 0;
                        user = new User(id, name, email,password, userUrl, status, search, rating);
                       /* HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", id);
                        hashMap.put("nomeUser", name);
                        hashMap.put("emailUser", email);
                        hashMap.put("userUrl", "default");
                        hashMap.put("status", "offline");
                        hashMap.put("search", name.toLowerCase());*/

                        /*hashMap*/
                        mDatabaseReference.child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                LimparCampos();
                                updateUI();
                            }
                        });

                    } else {
                        loadingDialog.dismissDialog();
                        Toast.makeText(getApplicationContext(), "Erro! Este Endereço De E-mail Já Está Sendo Usado Por Outra Conta", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error: " + e);
        }
    }

    /**
     * Abre a proxima tela se for chamado
     */
    public void updateUI() {
        Intent intent = new Intent(RegisterActivity.this, VerifyAccount.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goto_login() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        RegisterActivity.this.finish();
    }

    /**
     * limpa os campos
     */
    public void LimparCampos() {
        et_fullname.setText("");
        et_email.setText("");
        et_password.setText("");
        et_confirm_email.setText("");
        et_confirm_password.setText("");
    }

}
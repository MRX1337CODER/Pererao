package br.com.pererao.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import br.com.pererao.Network;
import br.com.pererao.R;
import br.com.pererao.SharedPref;
import br.com.pererao.SnackBarCustom;
import br.com.pererao.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText et_fullname, et_email, et_confirm_email, et_password, et_confirm_password;
    TextInputLayout ti_et_name, ti_et_email, ti_et_confirm_email, ti_et_password, ti_et_confirm_password;
    ImageButton imb_register;
    CircleImageView img_user;
    ImageView img_more;
    private Uri imageUri;
    MaterialButton btn_gotoLoginActivity;
    String UserID;
    static final String USUARIO = "Usuario";
    RelativeLayout relativeLayout;
    private static final String TAG = "RegisterActivityTAG";
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    LoadingDialog loadingDialog = new LoadingDialog(RegisterActivity.this);
    SharedPref sharedPref;
    Toolbar toolbar;
    boolean doubleBackToExitPressedOnce = false;
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

        btn_gotoLoginActivity = findViewById(R.id.btn_goto_login);
        imb_register = findViewById(R.id.imb_register);
        img_user = findViewById(R.id.user_image);
        img_more = findViewById(R.id.img_more);

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
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLoginActivity();
            }
        });

        //Setando UID aleatorio pelo firebase
        FirebaseUser usernull = FirebaseAuth.getInstance().getCurrentUser();
        if (usernull != null) {
            UserID = usernull.getUid();
        }

        //TODO:Ações
        /*Botão do cadastro*/
        imb_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyNet();
            }
        });

        /*Voltar para a activity de login*/
        btn_gotoLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLoginActivity();
            }
        });

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureIntent();
            }
        });
    }

    private void takePictureIntent() {
        Intent photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (photo.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(photo, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            uploadImage(imageBitmap);
        }
    }

    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final StorageReference mStorageReference = FirebaseStorage.getInstance().getReference().child("user_photo/" + mFirebaseUser.getUid());
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageUp = baos.toByteArray();
        UploadTask upload = mStorageReference.putBytes(imageUp);
        loadingDialog.startLoadingDialog();
        upload.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                loadingDialog.dismissDialog();
                if (task.isSuccessful()){
                    mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getApplicationContext())
                                    .load(uri)
                                    .into(img_user);
                            Toast.makeText(getApplicationContext(), "Imagem Carregada Com Sucesso", Toast.LENGTH_SHORT).show();
                            //img_user.setImageBitmap(imageBitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Erro: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), "Falha Ao Carregar Imagem", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            gotoLoginActivity();
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
        //String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(val)) {
            ti_et_email.setError("E-mail Obrigatório");
            ti_et_email.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(val).matches()) {//(!val.matches(checkEmail)) {
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
        //String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(val)) {
            ti_et_confirm_email.setError("Confirmação De E-mail Obrigatório");
            ti_et_confirm_email.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(val).matches()) {//!val.matches(checkEmail)) {
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
            imb_register.setEnabled(false);
            //Cria usuário
            mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mFirebaseUser = mFirebaseAuth.getCurrentUser();
                        assert mFirebaseUser != null;
                        final String id = mFirebaseUser.getUid();

                        //Envia email de verificação
                        mFirebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "E-mail De Verificação Enviado, Clique Sobre Ele Para Confirmar", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Falha Ao Enviar E-mail de Verificação.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        String userUrl = "default";
                        String status = "offline";
                        String search = name.toLowerCase();
                        float rating = 0;
                        user = new User(id, name, email, password, userUrl, status, search, rating);
                        loadingDialog.dismissDialog();
                        //Adiciona dados do usuário no FB
                        mDatabaseReference.child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                LimparCampos();
                                gotoVerifyAccount();
                            }
                        });

                    } else {
                        imb_register.setEnabled(true);
                        loadingDialog.dismissDialog();
                        Toast.makeText(getApplicationContext(), "Erro! Este Endereço De E-mail Já Está Sendo Usado Por Outra Conta.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error: " + e);
        }
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
        imb_register.setEnabled(true);
    }

    //Activity's
    public void gotoVerifyAccount() {
        Intent intent = new Intent(RegisterActivity.this, VerifyAccount.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void gotoLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        RegisterActivity.this.finish();
    }
}
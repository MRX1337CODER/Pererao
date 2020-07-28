package br.com.pererao.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import br.com.pererao.Network;
import br.com.pererao.R;
import br.com.pererao.SharedPref;
import br.com.pererao.SnackBarCustom;
import br.com.pererao.model.User;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText et_fullname, et_email, et_confirm_email, et_password, et_confirm_password;
    TextInputLayout ti_et_name, ti_et_email, ti_et_confirm_email, ti_et_password, ti_et_confirm_password;
    ImageButton imb_register;
    ImageView img_user, img_more;
    MaterialButton btn_gotoLoginActivity;
    String UserID, userUrl;
    RelativeLayout relativeLayout;
    RadioGroup rg_prestador;
    boolean isPrestador = false;
    static final String USUARIO = "Usuario";
    private static final String TAG = "RegisterActivityTAG";

    public final int REQ_CD_CAM = 101;
    private Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    private File _file_cam;

    private String imagePath = "";
    private String imageName = "";

    LoadingDialog loadingDialog = new LoadingDialog(RegisterActivity.this);
    SharedPref sharedPref;
    Toolbar toolbar;
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
        sharedPref.CarregamentoTemaEscuro();
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

        rg_prestador = findViewById(R.id.radio_prestador);
        btn_gotoLoginActivity = findViewById(R.id.btn_goto_login);
        imb_register = findViewById(R.id.imb_register);
        img_user = findViewById(R.id.user_image);
        img_more = findViewById(R.id.img_more);

        relativeLayout = findViewById(R.id.rl_register);
        //Firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference(USUARIO);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }

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

        _file_cam = FileUtil.createNewPictureFile(getApplicationContext());
        Uri _uri_cam = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            _uri_cam = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", _file_cam);
        } else {
            _uri_cam = Uri.fromFile(_file_cam);
        }
        cam.putExtra(MediaStore.EXTRA_OUTPUT, _uri_cam);
        cam.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(cam, REQ_CD_CAM);
            }
        });

        img_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(cam, REQ_CD_CAM);
            }
        });

        rg_prestador.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_Cliente:
                        isPrestador = false;
                        break;
                    case R.id.rb_Prestador:
                        isPrestador = true;
                        break;
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            Log.w("K", "k");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CD_CAM:
                if (resultCode == RESULT_OK) {
                    //imageUri = data.getData();
                    String _filePath = _file_cam.getAbsolutePath();

                    imagePath = _filePath;
                    Bitmap bitmap = null;
                    try {
                        bitmap = CorrigeFoto.carrega(imagePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //img_user.setImageBitmap(bitmap);
                    Glide.with(getApplicationContext())
                            .load(bitmap)
                            .into(img_user);

                    imageName = Uri.parse(_filePath).getLastPathSegment();
                    Log.w("SLA", "Path:" + imagePath + "\nName:" + imageName);

                }
                break;
            default:
                break;
        }
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
        gotoLoginActivity();
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
        CreateUser(name, email, password);

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

                        updateUserInfo(name, email, Uri.fromFile(new File(imagePath)), id);

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

    public void updateUserInfo(final String name, final String email, final Uri imageUri, final String id) {

        if (imageUri == null) {
            userUrl = "default";
            saveUser(id, name, email, userUrl);
            imb_register.setEnabled(false);
        } else {
            StorageReference mStorageReference = FirebaseStorage.getInstance().getReference().child("user_photo");
            final StorageReference imageFilePath = mStorageReference.child(id).child("profile").child("profile_image_user");//imageUri.getLastPathSegment());
            imageFilePath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    userUrl = uri.toString();
                                    saveUser(id, name, email, userUrl);
                                    imb_register.setEnabled(false);
                                }
                            });
                        }
                    });
        }

    }

    public void saveUser(String id, String name, String email, String url) {
        String status = "Off-line";
        String search = name.toLowerCase();
        float rating = 0;
        user = new User();
        user.setId(id);
        user.setNomeUser(name);
        user.setEmailUser(email);
        user.setUserUrl(url);
        user.setStatus(status);
        user.setSearch(search);
        user.setRating(rating);
        user.setPrestador(isPrestador);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", user.getId());
        hashMap.put("nomeUser", user.getNomeUser());
        hashMap.put("emailUser", user.getEmailUser());
        hashMap.put("userUrl", user.getUserUrl());
        hashMap.put("status", user.getStatus());
        hashMap.put("search", user.getSearch());
        hashMap.put("rating", user.getRating());
        hashMap.put("prestador", user.isPrestador());
        loadingDialog.dismissDialog();
        //Adiciona dados do usuário no FB
        mDatabaseReference.child(id).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                LimparCampos();
                if (isPrestador) {
                    gotoQualificationActivity();
                } else {
                    gotoVerifyAccount();
                }
            }
        });
    }


    /*Verificar campos*/
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(RegisterActivity.this, intent, activityOptionsCompat.toBundle());
        finish();
    }

    public void gotoQualificationActivity() {
        Intent intent = new Intent(RegisterActivity.this, QualificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(RegisterActivity.this, intent, activityOptionsCompat.toBundle());
        finish();
    }

    public void gotoLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(RegisterActivity.this, intent, activityOptionsCompat.toBundle());
        finish();
    }
}
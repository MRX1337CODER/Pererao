package br.com.pererao.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import br.com.pererao.R;
import br.com.pererao.SharedPref;
import br.com.pererao.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity {

    private static final String TAG = "UpdateProfileActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final String USUARIO = "Usuario";
    TextInputEditText et_username, et_email, et_password;
    TextInputLayout ti_et_username, ti_et_email, ti_et_password;
    CircleImageView img_user;
    ImageView iv_more;
    Uri imageUri;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    FirebaseAuth mFirebaseAuth;
    Toolbar toolbar;
    LoadingDialog loadingDialog = new LoadingDialog(UpdateProfileActivity.this);
    SharedPref sharedPref;
    String _ID, _USERNAME, _EMAIL, _USERURL, _SEARCH, userUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Revoke PrintScreen**/
        //     getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
        //           WindowManager.LayoutParams.FLAG_SECURE);
        /*Tema Escuro**/
        sharedPref = new SharedPref(this);
        sharedPref.CarregamentoTemaEscuro();
        setContentView(R.layout.activity_update_profile);

        //ToolBar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.update_profile);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity();
            }
        });

        //TODO:Declarações
        et_username = findViewById(R.id.et_update_username);
        et_email = findViewById(R.id.et_update_email);
        et_password = findViewById(R.id.et_update_password);

        ti_et_username = findViewById(R.id.ti_update_username);
        ti_et_email = findViewById(R.id.ti_update_email);
        ti_et_password = findViewById(R.id.ti_update_password);

        img_user = findViewById(R.id.user_image);
        iv_more = findViewById(R.id.img_more);

        loadingDialog.startLoadingDialog();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(USUARIO).child(mFirebaseUser.getUid());

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    _ID = user.getId();
                    _USERNAME = user.getNomeUser();
                    _EMAIL = user.getEmailUser();
                    _USERURL = user.getUserUrl();
                    _SEARCH = user.getSearch();
                    loadingDialog.dismissDialog();
                    initFields();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhotoProfile();
            }
        });
        iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhotoProfile();
            }
        });

    }

    public void initFields() {
        if (_USERURL.equals("default")) {
            img_user.setImageResource(R.drawable.ic_user_icon);
        } else {
            Glide.with(getApplicationContext())
                    .load(_USERURL)
                    .into(img_user);
        }

        et_username.setText(_USERNAME);
        et_email.setText(_EMAIL);
    }

    public void updatePhotoProfile() {
        Intent photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (photo.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(photo, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Glide.with(getApplicationContext())
                    .load(imageUri)
                    .into(img_user);
        }
    }

    private void status(String status) {
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
    }

    public void update(View view) {
        if (isNameChanged()) {
            Log.i(TAG, "Nome Atualizado");
        }
        if (isPasswordChanged() || isEmailChanged()) {
            Log.i(TAG, "Dado(s) Atualizado(s)");
        }

        if (imageUri != null) {
            loadingDialog.startLoadingDialog();
            StorageReference mStorageReference = FirebaseStorage.getInstance().getReference().child("user_photo");
            final StorageReference imageFilePath = mStorageReference.child(mFirebaseUser.getUid()).child("profile").child("profile_image_user");//imageUri.getLastPathSegment());
            imageFilePath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    userUrl = uri.toString();
                                    updatePhoto(userUrl);
                                    loadingDialog.dismissDialog();
                                    imageUri = null;
                                }
                            });
                        }
                    });
        }
    }

    public void updatePhoto(String url) {
        Map<String, Object> map = new HashMap<>();
        map.put("userUrl", url);
        mDatabaseReference.updateChildren(map);
    }

    public boolean isNameChanged() {
        final String val = ti_et_username.getEditText().getText().toString();
        if (!_USERNAME.equals(val)) {
            Map<String, Object> map = new HashMap<>();
            map.put("nomeUser", val);
            mDatabaseReference.updateChildren(map);
            _USERNAME = val;
            Toast.makeText(getApplicationContext(), "Dado Atualizado Com Sucesso", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    public boolean isPasswordChanged() {
        final String val = ti_et_password.getEditText().getText().toString();
        if (!TextUtils.isEmpty(val) && val.length() < 8) {
            ti_et_password.setError("A nova senha deve conter no mínimo 8 caracteres");
            return false;
        } else if (TextUtils.isEmpty(val)) {
            ti_et_password.setError(null);
            return false;
        } else {
            openAlertDialog(val, 0, "Alterar Senha", "Ao Alterar Sua Senha Você Será Redirecionado A Tela Entrada.");
            return true;
        }
    }

    public boolean isEmailChanged() {
        final String val = ti_et_email.getEditText().getText().toString();
        if (!TextUtils.isEmpty(val) && !Patterns.EMAIL_ADDRESS.matcher(val).matches()) {
            ti_et_email.setError("E-mail Inválido");
            return false;
        } else if (TextUtils.isEmpty(val)) {
            ti_et_email.setError(null);
            return false;
        } else if (!_EMAIL.equals(val)) {
            openAlertDialog(val, 1, "Alterar E-mail", "Ao alterar seu e-mail você será redirecionado a tela entrada e terá de validá-lo.");
            return true;
        } else {
            ti_et_email.setError(null);
            return false;
        }
    }

    public void openAlertDialog(final String type, final int i, String title, String message) {
        final EditText input = new EditText(UpdateProfileActivity.this);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateProfileActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint("Senha Atual");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock_outline_black_24dp, 0, 0, 0);
        alertDialog.setView(input);

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadingDialog.startLoadingDialog();
                String userpass = input.getText().toString();
                UserReauthentication(userpass, type, i);
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void UserReauthentication(String UserPassword, final String type, final int i) {
        String userEmail = mFirebaseUser.getEmail();
        assert userEmail != null;
        AuthCredential credential = EmailAuthProvider
                .getCredential(userEmail, UserPassword);

        mFirebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Toast.makeText(getApplicationContext(), "Re-autenticação Feita Com Sucesso.", Toast.LENGTH_SHORT).show();
                    ChangeEmailOrPassword(type, i);
                } else {
                    Toast.makeText(getApplicationContext(), "Re-autenticação Infelizmente Falhou.", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissDialog();
                }
            }
        });
    }

    private void ChangeEmailOrPassword(final String type, final int i) {
        if (i == 0) {
            mFirebaseUser.updatePassword(type)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Sua Senha Foi Alterado Com Êxito", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismissDialog();
                            }
                        }
                    });
        } else {
            mFirebaseUser.updateEmail(type)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("emailUser", type);
                                mDatabaseReference.updateChildren(map);

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

                                Toast.makeText(getApplicationContext(), "Seu E-mail Foi Alterado Com Êxito", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismissDialog();
                            }
                        }
                    });
        }
        mFirebaseAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void ChangePassword(final String newPass) {
        mFirebaseUser.updatePassword(newPass)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Sua Senha Foi Alterado Com Êxito", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissDialog();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        loadingDialog.dismissDialog();
        gotoActivity();
    }

    private void gotoActivity() {
        Intent intent = new Intent(UpdateProfileActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(UpdateProfileActivity.this, intent, activityOptionsCompat.toBundle());
        finish();
    }

}
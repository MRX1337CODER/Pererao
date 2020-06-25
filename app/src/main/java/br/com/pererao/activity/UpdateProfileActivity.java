package br.com.pererao.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import br.com.pererao.activity.ui.configuration.Configuration;
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
    String _ID, _USERNAME, _EMAIL, _PASSWORD, _USERURL, _SEARCH, userUrl;

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
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

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
                    _PASSWORD = user.getSenhaUser();
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
        et_password.setText(_PASSWORD);
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

    public void update(View view) {
        if (isNameChanged()) {
            Log.i(TAG, "Nome Atualizado");
        }
        if (isPasswordChanged()) {
            Log.i(TAG, "Senha Atualizada");
        }

        if (imageUri != null){
            StorageReference mStorageReference = FirebaseStorage.getInstance().getReference().child("user_photo");
            final StorageReference imageFilePath = mStorageReference.child(mFirebaseUser.getUid()).child("profile").child(imageUri.getLastPathSegment());
            imageFilePath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    userUrl = uri.toString();
                                    updatePhoto(userUrl);
                                }
                            });
                        }
                    });
        }
    }

    public void updatePhoto(String url){
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
        if (!_PASSWORD.equals(val)) {
            Map<String, Object> map = new HashMap<>();
            map.put("senhaUser", val);
            mDatabaseReference.updateChildren(map);
            _PASSWORD = val;
            Toast.makeText(getApplicationContext(), "Dado Atualizado Com Sucesso", Toast.LENGTH_SHORT).show();
            openAlertDialog(val);
            return true;
        } else {
            return false;
        }
    }

    public void openAlertDialog(final String pass){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateProfileActivity.this);
        alertDialog.setTitle("Alterar Senha");
        alertDialog.setMessage("Ao Alterar Sua Senha Você Será Redirecionado A Tela Entrada.");//Ao Alterar Seu E-mail Você Terá De Confirma-lo Novamente (ou algo assim)
        alertDialog.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ChangePassword(pass);
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
                            Intent intent = new Intent(UpdateProfileActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    /*public boolean isEmailChanged() {
        final String val = ti_et_email.getEditText().getText().toString();
        if (!_PASSWORD.equals(val)) {
            Map<String, Object> map = new HashMap<>();
            map.put("senhaUser", val);
            mDatabaseReference.updateChildren(map);
            return true;
        } else {
            return false;
        }
    }*/

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
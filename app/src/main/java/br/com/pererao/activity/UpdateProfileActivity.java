package br.com.pererao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

import br.com.pererao.R;
import br.com.pererao.SharedPref;
import br.com.pererao.activity.ui.configuration.Configuration;
import br.com.pererao.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity {

    private static final String TAG = "UpdateProfileActivity";
    TextInputEditText et_username, et_email, et_password;
    TextInputLayout ti_et_username, ti_et_email, ti_et_password;
    CircleImageView img_user;
    ImageView iv_more;
    private static final String USUARIO = "Usuario";
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    FirebaseAuth mFirebaseAuth;
    Toolbar toolbar;
    LoadingDialog loadingDialog = new LoadingDialog(UpdateProfileActivity.this);
    SharedPref sharedPref;
    String _USERNAME, _EMAIL, _PASSWORD, _USERURL, _SEARCH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void initFields(){
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

    public void updatePhotoProfile(){
        Toast.makeText(getApplicationContext(), "Atualizar Foto...", Toast.LENGTH_SHORT).show();
    }

    public void update(View view) {
        if (isNameChanged()) {
            Log.i(TAG, "Nome Atualizado");
        }
        if (isPasswordChanged()){
            Log.i(TAG,"Senha Atualizada");
        }
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
            Toast.makeText(getApplicationContext(), "Dado Igual Anterior E Não Pode Ser Atualizado", Toast.LENGTH_SHORT).show();
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
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "Dado Igual Anterior E Não Pode Ser Atualizado", Toast.LENGTH_SHORT).show();
            return false;
        }
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
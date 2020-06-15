package br.com.pererao.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import br.com.pererao.R;
import br.com.pererao.SharedPref;

public class UpdateProfileActivity extends AppCompatActivity {

    TextInputEditText et_username, et_email, et_password;
    TextInputLayout ti_et_username, ti_et_email, ti_et_password;
    ImageView img_user;
    private static final String USUARIO = "Usuario";
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    FirebaseAuth mFirebaseAuth;
    Toolbar toolbar;
    LoadingDialog loadingDialog = new LoadingDialog(UpdateProfileActivity.this);
    SharedPref sharedPref;
    boolean doubleBackToExitPressedOnce = false;
    String _USERNAME, _EMAIL, _PASSWORD, _USERURL, _SEARCH;
    Intent intentGet;

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

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(USUARIO).child(mFirebaseUser.getUid());

        intentGet = getIntent();
        _USERNAME = intentGet.getStringExtra("username");
        _EMAIL = intentGet.getStringExtra("email");
        _PASSWORD = intentGet.getStringExtra("password");
        _USERURL = intentGet.getStringExtra("userUrl");
        _SEARCH = intentGet.getStringExtra("search");

        if (_USERURL.equals("default")) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.ic_user_icon)//"https://firebasestorage.googleapis.com/v0/b/pererao2k20.appspot.com/o/user_photo%2Fman_user.png?alt=media")
                    .transform(new CircleCrop())
                    .into(img_user);
        } else {
            Glide.with(getApplicationContext())
                    .load(_USERURL)
                    .transform(new CircleCrop())
                    .into(img_user);
        }

        et_username.setText(_USERNAME);
        et_email.setText(_EMAIL);
        et_password.setText(_PASSWORD);

    }

    public void update(View view) {
        if (isNameChanged()) {
            Toast.makeText(getApplicationContext(), "Dado Atualizado.", Toast.LENGTH_SHORT).show();
        }
        if (isPasswordChanged()){
            Toast.makeText(getApplicationContext(), "Dado Atualizado.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Os Dados São Iguais E Não Podem Ser Atualizados.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isNameChanged() {
        final String val = ti_et_username.getEditText().getText().toString();
        if (!_USERNAME.equals(val)) {
            Map<String, Object> map = new HashMap<>();
            map.put("nomeUser", val);
            mDatabaseReference.updateChildren(map);
            _USERNAME = val;
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
            return true;
        } else {
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
        gotoActivity();
    }

    private void gotoActivity() {
        Intent intent = new Intent(UpdateProfileActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
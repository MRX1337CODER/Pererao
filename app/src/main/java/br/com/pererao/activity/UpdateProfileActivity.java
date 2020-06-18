package br.com.pererao.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.button.MaterialButton;
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
import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText et_username, et_email, et_password;
    TextInputLayout ti_et_username, ti_et_email, ti_et_password;
    ImageButton btn_edit_email, btn_edit_password;
    CircleImageView img_user;
    private static final String USUARIO = "Usuario";
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    FirebaseAuth mFirebaseAuth;
    Toolbar toolbar;
    LoadingDialog loadingDialog = new LoadingDialog(UpdateProfileActivity.this);
    SharedPref sharedPref;
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

        btn_edit_email = findViewById(R.id.btn_edit_email);
        btn_edit_password = findViewById(R.id.btn_edit_password);

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
            img_user.setImageResource(R.drawable.ic_user_icon);
        } else {
            Glide.with(getApplicationContext())
                    .load(_USERURL)
                    .into(img_user);
        }

        et_username.setText(_USERNAME);
        et_email.setText(_EMAIL);
        et_password.setText(_PASSWORD);

        btn_edit_email.setOnClickListener(this);
        btn_edit_password.setOnClickListener(this);
    }

    public void update(View view) {
        if (isPasswordChanged()) {
            String title = getString(R.string.old_password);
            dialogchange(title, "passwordChange");
        }
        if (isNameChanged()) {
            Toast.makeText(getApplicationContext(), "Dado Atualizado.", Toast.LENGTH_SHORT).show();
        } else {
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

    public void dialogchange(String title, final String type) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dView = inflater.inflate(R.layout.change_dialog, null);
        builder.setView(dView);

        final TextInputLayout ti_et_change = dView.findViewById(R.id.ti_pass);
        final EditText et_change = dView.findViewById(R.id.et_change);
        //String val = ti_et_change.getEditText().getText().toString();
        builder.setTitle("Trocar Senha");
        //builder.setMessage(title);
        builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (type == "emailChange") {
                } else {
                    et_change.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    et_change.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email_outline_black_24dp, 0, 0, 0);
                    et_change.setHint(R.string.old_password);
                    final String val = et_change.getText().toString().trim();

                    if (TextUtils.isEmpty(val)) {
                        et_change.setError("Nova Senha Obrigatório");
                        et_change.requestFocus();
                    } else if (et_change.length() < 8) {
                        et_change.setError("A Nova Senha Deve Conter No Mínimo 8 Caracteres");
                        et_change.requestFocus();
                    } else if (val != _PASSWORD) {
                        et_change.setError("Senha Errada!");
                    } else {
                        et_change.setError(null);
                        isPasswordChanged();
                    }
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        if (TextUtils.isEmpty(et_change.getText().toString())) {
            //Toast.makeText(UpdateProfileActivity.this, "", Toast.LENGTH_SHORT).show();
            et_change.setError("Nova Senha Obrigatório");
            et_change.requestFocus();
        } else if (et_change.length() < 8) {
            et_change.setError("A Nova Senha Deve Conter No Mínimo 8 Caracteres");
            et_change.requestFocus();
        } else {
            et_change.setError(null);
            //et_change.setErrorEnabled(false);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_edit_email:
                break;
            case R.id.btn_edit_password:
                et_password.setEnabled(true);
                ti_et_password.setFocusable(true);
                break;
        }
    }
}
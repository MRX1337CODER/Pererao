package br.com.pererao.activity.ui.configuration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.pererao.R;
import br.com.pererao.SharedPref;
import br.com.pererao.activity.DashboardActivity;
import br.com.pererao.activity.UpdateProfileActivity;

public class Configuration extends AppCompatActivity {

    SharedPref sharedPref;
    Switch DarkMode;
    MaterialButton btn_edit_account;
    FirebaseUser mFirebaseUser;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.CarregamentoTemaEscuro()) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        btn_edit_account = findViewById(R.id.btn_edit_account);
        DarkMode = findViewById(R.id.DarkMode);

        //Tema Escuro Botão
        if (sharedPref.CarregamentoTemaEscuro()) {
            DarkMode.setChecked(true);
        }
        DarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton btnView, boolean isChecked) {
                if (isChecked) {
                    sharedPref.setEstadoTemaEscuro(true);
                    restartApp();
                } else {
                    sharedPref.setEstadoTemaEscuro(false);
                    restartApp();
                }
            }
        });

        btn_edit_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    public void onBackPressed() {
        gotoDashboardActivity();
    }

    private void gotoDashboardActivity(){
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void restartApp() {
        Intent intent = new Intent(getApplicationContext(), Configuration.class);
        startActivity(intent);
        finish();
    }
}
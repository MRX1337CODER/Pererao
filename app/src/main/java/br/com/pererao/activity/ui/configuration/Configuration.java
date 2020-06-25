package br.com.pererao.activity.ui.configuration;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    MaterialButton btn_edit_account, DarkMode;
    int checkedItem = 0;
    FirebaseUser mFirebaseUser;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        sharedPref.CarregamentoTemaEscuro();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        btn_edit_account = findViewById(R.id.btn_edit_account);
        DarkMode = findViewById(R.id.DarkMode);

        //Tema Escuro Botão
        DarkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogDarkMode();
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

    private void showAlertDialogDarkMode() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Configuration.this);
        alertDialog.setTitle("Escolha um tema");
        final String[] items = {"Claro", "Escuro"};
        switch (sharedPref.CarregamentoTemaEscuro()) {
            case 0:
                checkedItem = 0;
                break;
            case 1:
                checkedItem = 1;
                break;
        }

        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Toast.makeText(Configuration.this, "Selecionou: " + items[which], Toast.LENGTH_LONG).show();
                        sharedPref.setTheme(0);
                        restartActivity();
                        dialog.dismiss();
                        break;
                    case 1:
                        Toast.makeText(Configuration.this, "Selecionou: " + items[which], Toast.LENGTH_LONG).show();
                        sharedPref.setTheme(1);
                        restartActivity();
                        dialog.dismiss();
                        break;
                }
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void restartActivity() {
        Intent intent = new Intent(getApplicationContext(), Configuration.class);
        startActivity(intent);
        finish();
    }
}
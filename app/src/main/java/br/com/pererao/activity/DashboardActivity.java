package br.com.pererao.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.pererao.R;
import br.com.pererao.SharedPref;
import br.com.pererao.SnackBarCustom;
import br.com.pererao.activity.ui.configuration.Configuration;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPref sharedPref;
    Toolbar toolbar;
    boolean doubleBackToExitPressedOnce = false;
    LinearLayout linearLayout;
    String UserID;
    Button btn_profile, btn_maps, btn_chat, btn_configuration;

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Tema Escuro**/
        sharedPref = new SharedPref(this);
        if (sharedPref.CarregamentoTemaEscuro()) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        VerifyAuthentication();
        //TODO: Declarações
        toolbar = findViewById(R.id.toolbar);
        linearLayout = findViewById(R.id.linearLayout);

        btn_profile = findViewById(R.id.btn_profile);
        btn_maps = findViewById(R.id.btn_maps);
        btn_chat = findViewById(R.id.btn_chat);
        btn_configuration = findViewById(R.id.btn_configuration);
        //Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();


        //ToolBar
        setSupportActionBar(toolbar);
        toolbar.setTitle("Painel");

        if (mFirebaseUser != null && mFirebaseUser.isEmailVerified()) {
            UserID = mFirebaseUser.getUid();
        } else {
            VerifyAuthentication();
        }

        btn_configuration.setOnClickListener(this);

    }

    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        SnackBarCustom.Snack(getApplication(), linearLayout, "Pressione Voltar Novamente Para Sair", Snackbar.LENGTH_SHORT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Toast.makeText(getApplicationContext(), "Ajuda :)", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout_app:

                AlertDialog.Builder alertExit = new AlertDialog.Builder(this);
                alertExit.setTitle("Sair");
                alertExit.setMessage("Deseja Realmente Sair?");

                alertExit.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signOut();
                    }
                });
                alertExit.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = alertExit.create();
                alert.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        mFirebaseAuth.signOut();
        VerifyAuthentication();
    }

    private void VerifyAuthentication() {
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        int id_btn = v.getId();
        Intent intent;
        switch (id_btn){
            case R.id.btn_configuration:
                intent = new Intent(getApplicationContext(), Configuration.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_profile:
                intent = new Intent(getApplicationContext(), UpdateProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
        }
    }
}
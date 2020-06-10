package br.com.pererao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import br.com.pererao.Network;
import br.com.pererao.R;
import br.com.pererao.SharedPref;
import br.com.pererao.SnackBarCustom;

public class NetworkActivity extends AppCompatActivity {
    Button btn_connect;
    RelativeLayout relativeLayout;
    boolean doubleBackToExitPressedOnce = false;
    Toolbar toolbar;
    SharedPref sharedPref;

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
        setContentView(R.layout.activity_network);

        relativeLayout = findViewById(R.id.rl_network);
        btn_connect = findViewById(R.id.btn_connect);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Sem Rede");
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onStart() {
        super.onStart();
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Net();
            }
        });
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

    private void Net() {
        if (Network.isConnected(getApplication())) {

            Intent intent = new Intent(NetworkActivity.this, ProfileActivity.class);//OnboardingActivity
            startActivity(intent);
            finish();
        }
    }
}
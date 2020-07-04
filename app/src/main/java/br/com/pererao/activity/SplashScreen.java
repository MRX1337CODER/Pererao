package br.com.pererao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import br.com.pererao.Network;
import br.com.pererao.R;
import br.com.pererao.SharedPref;

public class SplashScreen extends AppCompatActivity {
    SharedPref sharedPref;
    int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        /**Revoke PrintScreen**/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        /**Tema Escuro**/
        sharedPref = new SharedPref(this);
        sharedPref.CarregamentoTemaEscuro();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (Network.isConnected(getApplication())) {
            /**
             * Novo Handler para iniciar a proxima Activity e fechar esta após alguns segundos.
             **/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    /**
                     * Cria uma Intent que vai iniciar a OnbardingActivity (neste caso).
                     **/
                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);//verify//OnboardingActivity
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, NetworkActivity.class);//NetwordActivitty
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }

    }

}
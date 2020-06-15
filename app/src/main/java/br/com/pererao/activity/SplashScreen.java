package br.com.pererao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import br.com.pererao.R;
import br.com.pererao.SharedPref;

import br.com.pererao.Network;

public class SplashScreen extends Activity {
    TextView tv1, tv2;
    Animation anim_txt;//, anim_logo;
    SharedPref sharedPref;
    int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    public void onCreate(Bundle icicle) {
        /**Revoke PrintScreen**/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        /**Tema Escuro**/
        sharedPref = new SharedPref(this);
        if (sharedPref.CarregamentoTemaEscuro()) {
            setTheme(R.style.DarkTheme_FullScreen);
        } else {
            setTheme(R.style.AppTheme_FullScreen);
        }

        super.onCreate(icicle);
        setContentView(R.layout.activity_splash_screen);
        tv1 = findViewById(R.id.splash_name);
        tv2 = findViewById(R.id.splash_developer);
        //tv = (TextView) findViewById(R.id.splashscreen);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Glide.with(this).asGif().load(R.drawable.fundo).into(iv2);
        // Animation animacao_logo = AnimationUtils.loadAnimation(this,R.anim.zoom_in);
        anim_txt = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        //Animation animacao_texto2 = AnimationUtils.loadAnimation(this, R.anim.fade_in_atrasado);
        // iv.startAnimation(animacao_logo);
        //tv1.startAnimation(animacao_texto1);
        tv1.setAnimation(anim_txt);
        tv2.startAnimation(anim_txt);

        if (Network.isConnected(getApplication())) {
            /**
             * Novo Handler para iniciar a proxima Activity e fechar esta após alguns segundos.
             **/
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        SplashScreen.this.runOnUiThread(new Runnable() {
                            public void run() {
                                SplashScreen.this.doFinish();
                            }
                        });
                    }
                }
            }).start();


            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    /**
                     * Cria uma Intent que vai iniciar a OnbardingActivity (neste caso).
                     **/
              /*      Intent intent = new Intent(SplashScreen.this, LoginActivity.class);//verify//OnboardingActivity
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_DISPLAY_LENGTH);*/
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
    private synchronized void doFinish() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
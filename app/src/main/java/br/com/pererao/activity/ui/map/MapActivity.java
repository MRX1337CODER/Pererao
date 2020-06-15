package br.com.pererao.activity.ui.map;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import br.com.pererao.R;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Tema Escuro**/
        /*sharedPref = new SharedPref(this);
        if (sharedPref.CarregamentoTemaEscuro()) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

    }
}

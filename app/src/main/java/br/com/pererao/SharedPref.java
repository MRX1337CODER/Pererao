package br.com.pererao;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class SharedPref {
    SharedPreferences MinhaSharedPref;

    public SharedPref(Context context) {
        MinhaSharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    // Aqui vai salvar o estado do tema escuro em uma boolean
    public void setTheme(int theme) {
        SharedPreferences.Editor editor = MinhaSharedPref.edit();
        editor.putInt("TemaEscuro", theme);
        editor.commit();
    }

    // Aqui carrega o estado do tema escuro
    public int CarregamentoTemaEscuro() {
        int theme = MinhaSharedPref.getInt("TemaEscuro", 0);
        switch (theme) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }
        return theme;
    }

}
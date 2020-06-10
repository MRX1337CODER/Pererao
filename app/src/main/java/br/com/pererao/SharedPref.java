package br.com.pererao;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    SharedPreferences MinhaSharedPref;

    public SharedPref(Context context) {
        MinhaSharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    // Aqui vai salvar o estado do tema escuro em uma boolean
    public void setEstadoTemaEscuro(Boolean estado) {
        SharedPreferences.Editor editor = MinhaSharedPref.edit();
        editor.putBoolean("TemaEscuro", estado);
        editor.commit();
    }

    // Aqui carrega o estado do tema escuro
    public Boolean CarregamentoTemaEscuro() {
        Boolean estado = MinhaSharedPref.getBoolean("TemaEscuro", false);
        return estado;
    }

}

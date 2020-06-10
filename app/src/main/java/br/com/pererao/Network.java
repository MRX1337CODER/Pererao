package br.com.pererao;

import android.content.Context;
import android.net.ConnectivityManager;

import java.util.Objects;

public final class Network {

    public static boolean isConnected(Context cont) {
        ConnectivityManager conmag = (ConnectivityManager) cont.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conmag != null) {
            conmag.getActiveNetworkInfo();

            //Verifica internet pela WIFI
            if (Objects.requireNonNull(conmag.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).isConnected()) {
                return true;
            }

            //Verifica se tem internet móvel
            if (Objects.requireNonNull(conmag.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).isConnected()) {
                return true;
            }
        }

        return false;
    }

}
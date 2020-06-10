package br.com.pererao;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public final class SnackBarCustom {

    public static void Snack(Context cont, View view, String s, int time) {
        final Snackbar snackbar = Snackbar.make(view, s, time);
        View v = snackbar.getView();
        TextView tv_snack = v.findViewById(com.google.android.material.R.id.snackbar_text);
        tv_snack.setGravity(Gravity.CENTER);
        tv_snack.setTextColor(cont.getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    public static void SnackSetAction(Context cont, View view, int bgc, boolean bg, String s, String action, int time, final View.OnClickListener listener) {
        final Snackbar snackbar = Snackbar.make(view, s, time);
        snackbar.setAction(action, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                snackbar.dismiss();
            }
        });
        View v = snackbar.getView();
        //v.setBackgroundColor(c);
        if (bg){
        v.setBackgroundColor(cont.getResources().getColor(bgc));
        }
        TextView tv_snack = v.findViewById(com.google.android.material.R.id.snackbar_text);
        tv_snack.setGravity(Gravity.CENTER);
        tv_snack.setTextColor(cont.getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

}

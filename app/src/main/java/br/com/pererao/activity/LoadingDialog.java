package br.com.pererao.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import br.com.pererao.R;

public class LoadingDialog {
    public Activity activity;
    private AlertDialog Alert_Dialog;

    public LoadingDialog(Activity activity2) {
        activity = activity2;
    }

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog, null));
        builder.setCancelable(false);

        Alert_Dialog = builder.create();
        Alert_Dialog.show();
    }

    public void dismissDialog() {
        Alert_Dialog.dismiss();
    }
}
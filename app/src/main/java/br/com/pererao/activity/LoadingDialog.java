package br.com.pererao.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import br.com.pererao.R;

class LoadingDialog {
    private Activity activity;
    private AlertDialog Alert_Dialog;

    LoadingDialog(Activity activity2) {
        activity = activity2;
    }

    void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog, null));
        builder.setCancelable(false);

        Alert_Dialog = builder.create();
        Alert_Dialog.show();
    }

    void dismissDialog() {
        Alert_Dialog.dismiss();
    }
}
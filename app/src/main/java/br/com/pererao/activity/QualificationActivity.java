package br.com.pererao.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import br.com.pererao.R;

public class QualificationActivity extends AppCompatActivity {

    MaterialButton btn_submit_qualifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qualification);

        //TODO:Declarações
        btn_submit_qualifications = findViewById(R.id.btn_submit_qualifications);

        btn_submit_qualifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoVerifyAccount();
            }
        });
    }

    public void gotoVerifyAccount() {
        Intent intent = new Intent(getApplicationContext(), VerifyAccount.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(getApplicationContext(), intent, activityOptionsCompat.toBundle());
        finish();
    }

}

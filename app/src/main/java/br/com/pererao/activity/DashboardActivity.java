package br.com.pererao.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

import br.com.pererao.R;
import br.com.pererao.SharedPref;
import br.com.pererao.SnackBarCustom;
import br.com.pererao.activity.firebase.FirebaseHelper;
import br.com.pererao.activity.ui.chat.ChatActivity;
import br.com.pererao.activity.ui.configuration.Configuration;
import br.com.pererao.activity.ui.home.HomeActivity;
import br.com.pererao.activity.ui.map.MapActivity;
import br.com.pererao.model.Chat;
import br.com.pererao.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPref sharedPref;
    Toolbar toolbar;
    boolean doubleBackToExitPressedOnce = false;
    LinearLayout linearLayout;
    TextView tv_username, tv_email;
    CircleImageView user_image, new_message;
    Button btn_profile, btn_maps, btn_chat, btn_configuration;
    private static final String TAG = "DashboardActivity";
    LoadingDialog loadingDialog = new LoadingDialog(DashboardActivity.this);
    private static final String USUARIO = "Usuario";
    private PackageInfo pInfo;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Tema Escuro**/
        sharedPref = new SharedPref(this);
        sharedPref.CarregamentoTemaEscuro();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //TODO: Declarações
        toolbar = findViewById(R.id.toolbar);
        linearLayout = findViewById(R.id.linearLayout);
        tv_username = findViewById(R.id.user_name);
        tv_email = findViewById(R.id.user_email);
        user_image = findViewById(R.id.user_image);
        new_message = findViewById(R.id.img_new_message);

        btn_profile = findViewById(R.id.btn_profile);
        btn_maps = findViewById(R.id.btn_maps);
        btn_chat = findViewById(R.id.btn_chat);
        btn_configuration = findViewById(R.id.btn_configuration);
        //Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        loadingDialog.startLoadingDialog();
        checkUpdateApp();

        //ToolBar
        this.toolbar.setTitle("Painel");
        setSupportActionBar(toolbar);
        mFirebaseUser.reload();
        if (mFirebaseUser == null) {
            gotoLoginActivity();
        } else if (!mFirebaseUser.isEmailVerified()) {
            gotoVerifyAccount();
        } else {

            mDatabaseReference.child("Chat").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int unread = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Chat chat = snapshot.getValue(Chat.class);
                            if (chat.getReceiver().equals(mFirebaseUser.getUid()) && !chat.isIsseen()) {
                                unread++;
                            }
                        }
                        if (unread == 0) {
                            new_message.setVisibility(View.GONE);
                        } else {
                            new_message.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            mDatabaseReference.child(USUARIO).child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null || user.getNomeUser() == null) {
                            gotoLoginActivity();
                        }
                        assert user != null;
                        tv_username.setText(user.getNomeUser());
                        tv_email.setText(user.getEmailUser());

                        if (user.getUserUrl().equals("default")) {
                            user_image.setImageResource(R.drawable.ic_user_icon);
                        } else {
                            Glide.with(getApplicationContext())
                                    .load(user.getUserUrl())
                                    .into(user_image);
                        }
                        loadingDialog.dismissDialog();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        btn_profile.setOnClickListener(this);
        btn_maps.setOnClickListener(this);
        btn_chat.setOnClickListener(this);
        btn_configuration.setOnClickListener(this);
    }

    private void checkUpdateApp() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("new_version_code", String.valueOf(getVersionCode()));

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10) // mudar para 3600 para publicar o app
                .build();

        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(hashMap);

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    final String new_version_code = mFirebaseRemoteConfig.getString("new_version_code");
                    if (Integer.parseInt(new_version_code) > getVersionCode()) {
                        showUpdateAppDialog("seu pacote aqui", new_version_code);
                    }
                }
            }
        });
    }

    //Alert Dialog Atualização
    private void showUpdateAppDialog(final String appPackageName, String versionFromRemoteConfig) {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("Atualização");
        ad.setMessage("Essa versão está obsoleta, por favor atualize para a versão: " + versionFromRemoteConfig);
        ad.setPositiveButton("Atualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                dialog.dismiss();
            }
        });

        ad.setCancelable(false);
        AlertDialog alert = ad.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public int getVersionCode() {
        pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            //Log.i("MYLOG", "NameNotFoundException: "+e.getMessage());
        }
        return pInfo.versionCode;
    }

    private void status(String status) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(USUARIO).child(mFirebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        mDatabaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("On-line");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("Off-line");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //FirebaseUser Fuser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            mFirebaseUser.reload();
            if (!mFirebaseUser.isEmailVerified()) {
                gotoVerifyAccount();
            } else {
                Log.i(TAG, "E-mail Verificado e Com Usuário");
            }
        } else {
            gotoLoginActivity();
        }

    }

    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        SnackBarCustom.Snack(getApplication(), linearLayout, "Pressione Voltar Novamente Para Sair", Snackbar.LENGTH_SHORT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Toast.makeText(getApplicationContext(), "Ajuda :)", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout_app:

                AlertDialog.Builder alertExit = new AlertDialog.Builder(this);
                alertExit.setTitle("Sair");
                alertExit.setMessage("Deseja Realmente Sair?");

                alertExit.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signOut();
                    }
                });
                alertExit.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = alertExit.create();
                alert.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        FirebaseHelper.logOut();
        gotoLoginActivity();
    }

    @Override
    public void onClick(View v) {
        int id_btn = v.getId();
        switch (id_btn) {
            case R.id.btn_profile:
                gotoProfileActivity();
                break;
            case R.id.btn_maps:
                gotoMapActivity();
                break;
            case R.id.btn_chat:
                gotoChatActivity();
                break;
            case R.id.btn_configuration:
                gotoConfigurationActivity();
                break;
        }
    }

    //Activity's
    private void gotoLoginActivity() {
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(DashboardActivity.this, intent, activityOptionsCompat.toBundle());
        finish();
    }

    private void gotoVerifyAccount() {
        Intent intent = new Intent(DashboardActivity.this, VerifyAccount.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(DashboardActivity.this, intent, activityOptionsCompat.toBundle());
        finish();
    }

    private void gotoProfileActivity() {
        Intent intent = new Intent(DashboardActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(DashboardActivity.this, intent, activityOptionsCompat.toBundle());
        finish();
    }

    private void gotoMapActivity() {
        Intent intent = new Intent(DashboardActivity.this, MapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(DashboardActivity.this, intent, activityOptionsCompat.toBundle());
        finish();
    }

    private void gotoChatActivity() {
        Intent intent = new Intent(DashboardActivity.this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(DashboardActivity.this, intent, activityOptionsCompat.toBundle());
        finish();
    }

    private void gotoConfigurationActivity() {
        Intent intent = new Intent(DashboardActivity.this, Configuration.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.startActivity(DashboardActivity.this, intent, activityOptionsCompat.toBundle());
        finish();
    }
}
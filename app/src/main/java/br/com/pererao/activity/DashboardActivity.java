package br.com.pererao.activity;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.pererao.R;
import br.com.pererao.SharedPref;
import br.com.pererao.SnackBarCustom;
import br.com.pererao.activity.ui.chat.ChatActivity;
import br.com.pererao.activity.ui.configuration.Configuration;
import br.com.pererao.activity.ui.home.HomeActivity;
import br.com.pererao.activity.ui.map.MapActivity;
import br.com.pererao.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPref sharedPref;
    Toolbar toolbar;
    boolean doubleBackToExitPressedOnce = false;
    LinearLayout linearLayout;
    TextView tv_username, tv_email;
    CircleImageView user_image;
    Button btn_profile, btn_maps, btn_chat, btn_configuration;
    private static final String TAG = "DashboardActivity";
    private static final String USUARIO = "Usuario";

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Tema Escuro**/
        sharedPref = new SharedPref(this);
        if (sharedPref.CarregamentoTemaEscuro()) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //TODO: Declarações
        toolbar = findViewById(R.id.toolbar);
        linearLayout = findViewById(R.id.linearLayout);
        tv_username = findViewById(R.id.user_name);
        tv_email = findViewById(R.id.user_email);
        user_image = findViewById(R.id.user_image);

        btn_profile = findViewById(R.id.btn_profile);
        btn_maps = findViewById(R.id.btn_maps);
        btn_chat = findViewById(R.id.btn_chat);
        btn_configuration = findViewById(R.id.btn_configuration);
        //Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(USUARIO);

        onStart();
        //ToolBar
        this.toolbar.setTitle("Painel");
        setSupportActionBar(toolbar);

        if (mFirebaseUser == null) {
            gotoLoginActivity();
        } else {
            mDatabaseReference.child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user == null) {
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser Fuser = mFirebaseAuth.getCurrentUser();
        if (Fuser != null) {
            Fuser.reload();
            if (!Fuser.isEmailVerified()) {
                gotoVerifyAccount();
            } else {
                Log.i(TAG, "Usuário: " + Fuser.getUid() + "\nE-mail Verified:" + Fuser.isEmailVerified());
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
        mFirebaseAuth.signOut();
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
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void gotoVerifyAccount() {
        Intent intent = new Intent(DashboardActivity.this, VerifyAccount.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void gotoProfileActivity() {
        Intent intent = new Intent(DashboardActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void gotoMapActivity() {
        Intent intent = new Intent(DashboardActivity.this, MapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void gotoChatActivity() {
        Intent intent = new Intent(DashboardActivity.this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void gotoConfigurationActivity() {
        Intent intent = new Intent(DashboardActivity.this, Configuration.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
package br.com.pererao.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.List;
import java.util.Objects;

import br.com.pererao.R;
import br.com.pererao.SharedPref;
import br.com.pererao.SnackBarCustom;
import br.com.pererao.model.User;
import br.com.pererao.ui.chat.ChatFragment;

public class ProfileActivity extends AppCompatActivity {

    SharedPref sharedPref;
    AppBarConfiguration appBarConfiguration;
    NavController navController;
    BottomNavigationView navView;
    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    FirebaseAuth mFirebaseAuth;
    boolean doubleBackToExitPressedOnce = false;
    RelativeLayout relativeLayout;
    DatabaseReference mDatabaseReference;
    private static final String USUARIO = "Usuario";
    ImageView img_user;
    String UserID;

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
        setContentView(R.layout.activity_main);
        mFirebaseAuth = FirebaseAuth.getInstance();
        drawer = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        navigationView = findViewById(R.id.nav_viewDrawable);
        toolbar = findViewById(R.id.toolbar);
        relativeLayout = findViewById(R.id.rl_profile);

        img_user = findViewById(R.id.img_user);

        VerifyAuthentication();

        //ToolBar
        //toolbar.setTitle(R.string.reset_password);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        FirebaseUser usernull = mFirebaseAuth.getCurrentUser();
        if (usernull != null) {
            UserID = usernull.getUid();
        }
        else {
            VerifyAuthentication();
        }
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(USUARIO);

        mDatabaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            assert user != null;
                            if (user.getNomeUser() == null || user.getEmailUser() == null) {
                                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                            if (user.getUserUrl().equals("default")) {
                                Glide.with(getApplicationContext())
                                        .load("https://firebasestorage.googleapis.com/v0/b/pererao2k20.appspot.com/o/user_photo%2Fman_user.png?alt=media")
                                        .transform(new CircleCrop())
                                        .into(img_user);
                            }
                            else{
                                Glide.with(getApplicationContext())
                                        .load(user.getUserUrl())
                                        .transform(new CircleCrop())
                                        .into(img_user);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("HomeFragment", "Erro ao ler o valor: " + databaseError.toException());
                    }
                }
        );


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_map, R.id.navigation_chat, R.id.navigation_configuration)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
            //super.onBackPressed();
            super.finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        SnackBarCustom.Snack(getApplication(), relativeLayout, "Pressione Voltar Novamente Para Sair", Snackbar.LENGTH_SHORT);
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
        VerifyAuthentication();
    }

    private void VerifyAuthentication() {

        if (FirebaseAuth.getInstance().getUid() == null) {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

}
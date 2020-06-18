package br.com.pererao.activity.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.pererao.R;
import br.com.pererao.activity.DashboardActivity;
import br.com.pererao.activity.LoginActivity;
import br.com.pererao.activity.VerifyAccount;
import br.com.pererao.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    TextView nameUser, emailUser;
    CircleImageView user_image;
    DatabaseReference mDatabaseReference;
    private static final String USUARIO = "Usuario";
    String UserID;
    RatingBar ratingBar;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        user_image = findViewById(R.id.user_image);
        nameUser = findViewById(R.id.tv_name_user);
        emailUser = findViewById(R.id.tv_email_user);
        ratingBar = findViewById(R.id.ratingBarUser);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        FirebaseUser usernull = mFirebaseAuth.getCurrentUser();
        if (usernull != null) {
            UserID = usernull.getUid();
        }

        if (mFirebaseUser != null) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference(USUARIO).child(mFirebaseUser.getUid());
            mDatabaseReference.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            assert user != null;
                            nameUser.setText(user.getNomeUser());
                            emailUser.setText(user.getEmailUser());
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
                            Log.e("HomeActivity", "Erro ao ler o valor: " + databaseError.toException());
                        }
                    }
            );
        } else {
            gotoLoginActivity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mFirebaseUser != null) {
            if (!mFirebaseUser.isEmailVerified()) {
                gotoVerifyAccount();
            }
        } else {
            gotoLoginActivity();
        }

    }

    public void onBackPressed() {
        gotoDashboardActivity();
    }

    private void gotoDashboardActivity() {
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void gotoLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void gotoVerifyAccount() {
        Intent intent = new Intent(getApplicationContext(), VerifyAccount.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
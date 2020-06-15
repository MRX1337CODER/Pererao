package br.com.pererao.activity.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.pererao.R;
import br.com.pererao.activity.LoginActivity;
import br.com.pererao.model.User;

public class Home extends Fragment {

    private TextView nameUser, emailUser;
    private ImageView img_user;
    private DatabaseReference mDatabaseReference;
    private static final String USUARIO = "Usuario";
    String UserID;
    RatingBar ratingBar;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.profile_activity, container, false);
        VerifyAuthentication();
        img_user = root.findViewById(R.id.img_user);
        nameUser = root.findViewById(R.id.tv_name_user);
        emailUser = root.findViewById(R.id.tv_email_user);
        ratingBar = root.findViewById(R.id.ratingBarUser);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        FirebaseUser usernull = mFirebaseAuth.getCurrentUser();
        if (usernull != null) {
            UserID = usernull.getUid();
        }

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(USUARIO).child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        assert user != null;
                        if (!(user.getNomeUser() == null) || !(user.getEmailUser() == null)) {
                            nameUser.setText(user.getNomeUser());
                            emailUser.setText(user.getEmailUser());
                            if (user.getUserUrl().equals("default")) {
                                Glide.with(getContext())
                                        .load("https://firebasestorage.googleapis.com/v0/b/pererao2k20.appspot.com/o/user_photo%2Fman_user.png?alt=media")
                                        .transform(new CircleCrop())
                                        .into(img_user);
                            } else {
                                Glide.with(getContext())
                                        .load(user.getUserUrl())
                                        .transform(new CircleCrop())
                                        .into(img_user);
                            }
                        } else if (user.getNomeUser() == null || user.getEmailUser() == null || mFirebaseUser.getUid() == null) {
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            getActivity().getFragmentManager().popBackStack();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("HomeFragment", "Erro ao ler o valor: " + databaseError.toException());
                    }
                }
        );

        return root;

    }

    private void VerifyAuthentication() {

        if (FirebaseAuth.getInstance().getUid() == null) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().getFragmentManager().popBackStack();
        }
    }
}
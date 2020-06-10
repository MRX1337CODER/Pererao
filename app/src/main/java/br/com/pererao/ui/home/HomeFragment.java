package br.com.pererao.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.pererao.R;
import br.com.pererao.activity.LoginActivity;
import br.com.pererao.activity.ProfileActivity;
import br.com.pererao.model.User;

public class HomeFragment extends Fragment {

    private TextView nameUser, emailUser;
    private ImageView photoUser;
    private DatabaseReference mDatabaseReference;
    private static final String USUARIO = "Usuario";
    String UserID, id;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        VerifyAuthentication();
        nameUser = root.findViewById(R.id.tv_name_user);
        emailUser = root.findViewById(R.id.tv_email_user);

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
                        //for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);//snapshot.getValue(User.class);
                        assert user != null;
                        //Log.i("HomeFrag", "User:" + user + "\n\n\nUserName:" + user.getNomeUser() + "\n\nUserEmail:" + user.getEmailUser());
                        try {
                            if (!(user.getNomeUser() == null) || !(user.getEmailUser() == null)) {
                                nameUser.setText(user.getNomeUser());
                                emailUser.setText(user.getEmailUser());
                            } else {
                                user.setNomeUser("");
                                user.setEmailUser("");
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                getActivity().getFragmentManager().popBackStack();
                            }
                        }catch (Exception e){
                            Log.e("HomeFragment", "Error User:" + e.getMessage());
                        }

                        //}
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
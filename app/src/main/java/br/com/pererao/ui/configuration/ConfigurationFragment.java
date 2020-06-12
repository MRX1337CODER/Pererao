package br.com.pererao.ui.configuration;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;

import br.com.pererao.R;
import br.com.pererao.activity.NewPasswordActivity;
import br.com.pererao.model.User;

public class ConfigurationFragment extends Fragment {

    MaterialButton btn_new_password;
    TextView tv_pass;
    DatabaseReference mDocumentReference;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    private static final String USUARIO = "Usuario";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_configuration, container, false);


        tv_pass = root.findViewById(R.id.password);
        btn_new_password = root.findViewById(R.id.btn_pass);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mDocumentReference = FirebaseDatabase.getInstance().getReference(USUARIO).child(mFirebaseUser.getUid());
        mDocumentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                tv_pass.setText(user.getSenhaUser());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_new_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewPasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("passIntentString", "NovaSenha");
                startActivity(intent);
            }
        });

        return root;
    }

}

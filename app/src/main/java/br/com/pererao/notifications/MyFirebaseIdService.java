package br.com.pererao.notifications;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseIdService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser != null){
            updateToken(s);
        }
    }

    private void updateToken(String refreshToken) {
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(refreshToken);
        mDatabaseReference.child(mFirebaseUser.getUid()).setValue(token);
    }
}

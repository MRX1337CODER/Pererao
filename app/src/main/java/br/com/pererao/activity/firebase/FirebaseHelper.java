package br.com.pererao.activity.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class FirebaseHelper {

    private static FirebaseUser mFirebaseUser;
    private static FirebaseAuth mFirebaseAuth;
    private static FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseHelper() {
    }

    public static FirebaseAuth getmFirebaseAuth(){
        if (mFirebaseAuth == null){
            inicializarFirebase();
        }
        return mFirebaseAuth;
    }

    private static void inicializarFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFBUser = mFirebaseAuth.getCurrentUser();
                if (mFBUser != null){
                    mFirebaseUser = mFBUser;
                }
            }
        };
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public static FirebaseUser getmFirebaseUser(){
        //mFirebaseUser = getmFirebaseAuth().getCurrentUser();
        return mFirebaseUser;
    }

    public static void logOut(){
        getmFirebaseAuth().signOut();
    }

}

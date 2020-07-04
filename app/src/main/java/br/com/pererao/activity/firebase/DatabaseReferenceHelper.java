package br.com.pererao.activity.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseReferenceHelper {

    private static DatabaseReference mDatabaseReference;

    private DatabaseReferenceHelper() {
    }

    public static DatabaseReference getmDatabaseReference(){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        return mDatabaseReference;
    }

}

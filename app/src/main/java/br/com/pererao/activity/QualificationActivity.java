package br.com.pererao.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import br.com.pererao.R;
import br.com.pererao.adapter.QualificationsAdapter;
import br.com.pererao.model.Qualifications;
import br.com.pererao.model.User;

public class QualificationActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {


    private List<Qualifications> mQualifications, filteredQualifications;
    private QualificationsAdapter mAdapter;
    LoadingDialog loadingDialog = new LoadingDialog(QualificationActivity.this);
    MaterialButton btn_submit_qualifications;
    private static final String TAG = "QualificationActivity";
    private static final String USUARIO = "Usuario";
    RecyclerView recyclerView;
    StringBuilder sb = null;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    FirebaseAuth mFirebaseAuth;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qualification);

        //TODO:Declarações
        btn_submit_qualifications = findViewById(R.id.btn_submit_qualifications);
        recyclerView = findViewById(R.id.recycler_view);
        toolbar = findViewById(R.id.toolbar);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(USUARIO).child(mFirebaseUser.getUid());

        //TODO: Ações
        toolbar.setTitle(R.string.qualifications);
        setSupportActionBar(toolbar);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        btn_submit_qualifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoVerifyAccount();
            }
        });


        addItensFromJson();
    }

    private void addItensFromJson() {
        try {
            String jsonDataString = readJsonDataFromFile();
            JSONArray jsonArray = new JSONArray(jsonDataString);
            mQualifications = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject itemObj = jsonArray.getJSONObject(i);
                String desc = itemObj.getString("desc");

                Qualifications qualifications = new Qualifications(desc);
                mQualifications.add(qualifications);

            }
            mAdapter = new QualificationsAdapter(this, mQualifications);
            recyclerView.setAdapter(mAdapter);

        } catch (JSONException | IOException e) {
            //e.printStackTrace();
            Log.i(TAG, "addItemsFromJSON: ", e);
        }
    }

    private String readJsonDataFromFile() throws IOException {
        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();

        try {
            String jsonString = null;
            inputStream = getResources().openRawResource(R.raw.occupations);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            while ((jsonString = bufferedReader.readLine()) != null) {
                builder.append(jsonString);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return new String(builder);
    }

    public void gotoVerifyAccount() {
        sb = new StringBuilder();
        //sbCode = new StringBuilder();
        int i = 0;
        do {
            if (mAdapter.checkedQualifications.size() <= 0) {
                Toast.makeText(getApplicationContext(), "Deve ser selecionada ao menos uma qualificação!", Toast.LENGTH_LONG).show();
            } else {
                Qualifications qualifications = mAdapter.checkedQualifications.get(i);
                sb.append(qualifications.getDesc());
                //sbCode.append(qualifications.getCod());
                if (i != mAdapter.checkedQualifications.size() - 1) {
                    sb.append("\n");
            //        sbCode.append("\n");
                }

                i++;
            }
        } while (i < mAdapter.checkedQualifications.size());

        if (mAdapter.checkedQualifications.size() > 0) {
            AlertDialog.Builder alertQuali = new AlertDialog.Builder(this);
            alertQuali.setTitle("Confirmar Qualificações");
            final String[] linha = sb.toString().split("\\n");
            //final String[] linhaCode = sbCode.toString().split("\\n");

            alertQuali.setMessage(sb.toString());
            alertQuali.setPositiveButton("Tudo certo, Enviar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loadingDialog.startLoadingDialog();
                    mDatabaseReference.child("qualifications").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                for (int j = 0; j < mAdapter.checkedQualifications.size(); j++) {
                                    User user = new User();
                                    List<String> t = new ArrayList<>();
                                    t.addAll(Arrays.asList(linha).subList(0, j + 1));
                                    user.setList(t);
                                    user.setSize(mAdapter.checkedQualifications.size());
                                    Log.i("TESTE", "LIST:" + user.getList());
                                    hashMap.put("size", user.getSize());
                                    hashMap.put("list", user.getList());

                                }
                                loadingDialog.dismissDialog();
                                mDatabaseReference.child("qualifications").setValue(hashMap);
                                Intent intent = new Intent(getApplicationContext(), VerifyAccount.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out);
                                ActivityCompat.startActivity(getApplicationContext(), intent, activityOptionsCompat.toBundle());
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            alertQuali.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertQuali.create();
            alertDialog.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popup_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("Buscar...");
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filteredQualifications = filter(mQualifications, newText);
        mAdapter.setFilter(filteredQualifications);
        return true;
    }

    private List<Qualifications> filter(List<Qualifications> mQualifications, String newText) {
        newText = newText.toLowerCase();
        String text;
        filteredQualifications = new ArrayList<>();
        for (Qualifications qualifi : mQualifications) {
            text = qualifi.getDesc().toLowerCase();
            if (text.contains(newText)) {
                filteredQualifications.add(qualifi);
            }
        }
        return filteredQualifications;
    }

}
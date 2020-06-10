package br.com.pererao;
/*
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchQualifications extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener {

    SearchView searchQualifications;
    ListView lvQualificaions;
    Button btn_cancelQualifications, btn_sendQualifications, btn_removeQualifications;
    Resources res;
    String[] qualificationsStringArray;
    List<String> selectedQualificationsList;
    ArrayAdapter<String> qualificationsAdapter;
    int idQuali, key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_qualifications);

        //TODO: Declarações
        searchQualifications = findViewById(R.id.search_qualifications);
        lvQualificaions = findViewById(R.id.lv_qualifications);
        btn_cancelQualifications = findViewById(R.id.btn_cancelQualifications);
        btn_sendQualifications = findViewById(R.id.btn_sendQualifications);
        btn_removeQualifications = findViewById(R.id.btn_removeQualifications);
        btn_removeQualifications.setOnClickListener(this);
        btn_sendQualifications.setOnClickListener(this);
        btn_cancelQualifications.setOnClickListener(this);

        res = getResources();
        qualificationsStringArray = res.getStringArray(R.array.qualifications_array);
        selectedQualificationsList = new ArrayList<>();


        //TODO: Ações
        Arrays.sort(qualificationsStringArray);
        qualificationsAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, qualificationsStringArray);
        qualificationsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, qualificationsStringArray);
        lvQualificaions.setAdapter(qualificationsAdapter);
        searchQualifications.setOnQueryTextListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sendQualifications:
                SparseBooleanArray itemChecked = lvQualificaions.getCheckedItemPositions();
                for (int i = 0; i < itemChecked.size(); i++) {
                    int key = itemChecked.keyAt(i);
                    boolean value = itemChecked.get(key);
                    if (value) {
                        idQuali++;
                        selectedQualificationsList.add(lvQualificaions.getItemAtPosition(key).toString());
                    }
                }
                if (selectedQualificationsList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Por Favor, Selecione Ao Menos Uma Qualificação", Toast.LENGTH_SHORT).show();
                } else {
                    Intent bundlequali = new Intent(SearchQualifications.this, RegisterActivity.class);
                    bundlequali.putExtra("qualifications", selectedQualificationsList.toString());
                    bundlequali.putExtra("sizeQuali", idQuali);
                    startActivity(bundlequali);
                    finish();
                    //bundle aki aaaaa levando o selectedQualifications para a register activiy
                }
                break;
            case R.id.btn_removeQualifications:
                itemChecked = lvQualificaions.getCheckedItemPositions();
                for (int i = 0; i < itemChecked.size(); i++) {
                    int key = itemChecked.keyAt(i);
                    boolean value = itemChecked.get(key);
                    if (value) {
                        idQuali++;
                        selectedQualificationsList.add(lvQualificaions.getItemAtPosition(key).toString());
                    }
                }
                if (selectedQualificationsList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Por Favor, Selecione Ao Menos Uma Qualificação", Toast.LENGTH_SHORT).show();
                } else {
                    Intent bundlequali = new Intent(SearchQualifications.this, RegisterActivity.class);
                    bundlequali.putExtra("removeQuali", idQuali);
                    startActivity(bundlequali);
                    finish();
                }
                break;
            case R.id.btn_cancelQualifications:
                Intent intent = new Intent(SearchQualifications.this, RegisterActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        qualificationsAdapter.getFilter().filter(newText);
        return false;
    }
}
*/
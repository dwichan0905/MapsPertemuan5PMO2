package com.dwichan.uts.maps;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FindAsMapsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_as_maps);

        setTitle("Cari Lokasi");

        final TextView tvCriteria = findViewById(R.id.tvCriteria);
        Button btnSearch = findViewById(R.id.btnSearch);
        Button btnCancel = findViewById(R.id.btnCancel);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Menemukan lokasi dengan kriteria tertentu
                if (tvCriteria.getText().toString().equals("")) {
                    Toast.makeText(FindAsMapsActivity.this,"Tentukan kriteria pencariannya!", Toast.LENGTH_SHORT).show();
                } else {
                    String criteria = tvCriteria.getText().toString();
                    Uri uriGoogleMaps = Uri.parse("geo:0,0?q=" + criteria);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, uriGoogleMaps);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
                // akhir dari Menemukan lokasi dengan kriteria tertentu
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
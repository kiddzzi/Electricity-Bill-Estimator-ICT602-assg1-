package com.example.electricitybillestimator;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class BillListActivity extends AppCompatActivity {

    ListView lvBills;
    DataHelper dbHelper;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<Integer> ids = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_list);
        setTitle("Bill History");
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DataHelper(this);
        lvBills = findViewById(R.id.lvBills);
        lvBills.setEmptyView(findViewById(R.id.tvEmpty));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lvBills.setAdapter(adapter);

        lvBills.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra("id", ids.get(position));
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        items.clear();
        ids.clear();
        Cursor c = dbHelper.getAllBills();
        while (c.moveToNext()) {
            ids.add(c.getInt(0));
            items.add(c.getString(1) + "   -   " + BillCalculator.money(c.getDouble(2)));
        }
        c.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.electricitybillestimator;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    Spinner spMonth;
    EditText etUnits;
    SeekBar sbRebate;
    TextView tvRebate, tvTotal, tvFinal;
    DataHelper dbHelper;
    ArrayAdapter<CharSequence> adapter;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle("Bill Details");
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DataHelper(this);
        id = getIntent().getIntExtra("id", -1);

        spMonth = findViewById(R.id.spMonth);
        etUnits = findViewById(R.id.etUnits);
        sbRebate = findViewById(R.id.sbRebate);
        tvRebate = findViewById(R.id.tvRebate);
        tvTotal = findViewById(R.id.tvTotal);
        tvFinal = findViewById(R.id.tvFinal);

        adapter = ArrayAdapter.createFromResource(this, R.array.months,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMonth.setAdapter(adapter);

        sbRebate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar s, int progress, boolean fromUser) {
                tvRebate.setText("Rebate: " + (progress / 2.0) + "%");
            }
            @Override public void onStartTrackingTouch(SeekBar s) { }
            @Override public void onStopTrackingTouch(SeekBar s) { }
        });

        loadBill();

        findViewById(R.id.btnUpdate).setOnClickListener(v -> {
            int units = BillCalculator.readUnits(etUnits);
            if (units == -1) return;
            double rebate = sbRebate.getProgress() / 2.0;
            double total = BillCalculator.calcTotal(units);
            double fin = BillCalculator.applyRebate(total, rebate);
            dbHelper.updateBill(id, spMonth.getSelectedItem().toString(), units, total, rebate, fin);
            Toast.makeText(this, "Bill updated", Toast.LENGTH_SHORT).show();
            finish();
        });

        findViewById(R.id.btnDelete).setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Delete this bill?")
                        .setMessage("This action cannot be undone.")
                        .setPositiveButton("Delete", (d, w) -> {
                            dbHelper.deleteBill(id);
                            Toast.makeText(this, "Bill deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .setNegativeButton("Cancel", null)
                        .show());

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadBill() {
        Cursor c = dbHelper.getBill(id);
        if (c.moveToFirst()) {
            spMonth.setSelection(adapter.getPosition(c.getString(0)));
            etUnits.setText(String.valueOf(c.getInt(1)));
            tvTotal.setText("Total charges: " + BillCalculator.money(c.getDouble(2)));
            sbRebate.setProgress((int) Math.round(c.getDouble(3) * 2));
            tvFinal.setText("Final cost: " + BillCalculator.money(c.getDouble(4)));
        }
        c.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}

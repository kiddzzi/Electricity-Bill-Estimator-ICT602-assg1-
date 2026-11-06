package com.example.electricitybillestimator;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Spinner spMonth;
    EditText etUnits;
    SeekBar sbRebate;
    TextView tvRebate, tvTotal, tvFinal;
    Button btnCalculate, btnSave, btnHistory;
    DataHelper dbHelper;

    double lastTotal, lastFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Electricity Bill Estimator");

        dbHelper = new DataHelper(this);
        spMonth = findViewById(R.id.spMonth);
        etUnits = findViewById(R.id.etUnits);
        sbRebate = findViewById(R.id.sbRebate);
        tvRebate = findViewById(R.id.tvRebate);
        tvTotal = findViewById(R.id.tvTotal);
        tvFinal = findViewById(R.id.tvFinal);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnSave = findViewById(R.id.btnSave);
        btnHistory = findViewById(R.id.btnHistory);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.months, android.R.layout.simple_spinner_item);
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

        btnCalculate.setOnClickListener(v -> calculate());

        btnSave.setOnClickListener(v -> {
            if (calculate()) {
                int units = Integer.parseInt(etUnits.getText().toString().trim());
                double rebate = sbRebate.getProgress() / 2.0;
                dbHelper.insertBill(spMonth.getSelectedItem().toString(),
                        units, lastTotal, rebate, lastFinal);
                Toast.makeText(this, "Bill saved to history", Toast.LENGTH_SHORT).show();
            }
        });

        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(this, BillListActivity.class)));
    }

    private boolean calculate() {
        int units = BillCalculator.readUnits(etUnits);
        if (units == -1) {
            Toast.makeText(this, "Please correct the highlighted field", Toast.LENGTH_SHORT).show();
            return false;
        }
        double rebate = sbRebate.getProgress() / 2.0;
        lastTotal = BillCalculator.calcTotal(units);
        lastFinal = BillCalculator.applyRebate(lastTotal, rebate);
        tvTotal.setText("Total charges: " + BillCalculator.money(lastTotal));
        tvFinal.setText("Final cost after rebate: " + BillCalculator.money(lastFinal));
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

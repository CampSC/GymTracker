package com.sergio.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sergio.gymtracker.repository.GymRepository;

import java.util.ArrayList;
import java.util.List;

public class CreatePlanActivity extends AppCompatActivity {

    private EditText etPlanName;
    private LinearLayout llDaysContainer;
    private Button btnSavePlan;

    private final List<EditText> dayNameInputs =
            new ArrayList<>();

    private final List<Spinner> dayTypeInputs =
            new ArrayList<>();

    private GymRepository repository;

    private static final String[] DAYS = {
            "Segunda-feira",
            "Terça-feira",
            "Quarta-feira",
            "Quinta-feira",
            "Sexta-feira",
            "Sábado",
            "Domingo"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        etPlanName = findViewById(R.id.etPlanName);
        llDaysContainer = findViewById(R.id.llDaysContainer);
        btnSavePlan = findViewById(R.id.btnSavePlan);

        repository = new GymRepository(getApplicationContext());

        createDayFields();

        btnSavePlan.setOnClickListener(v -> savePlan());
    }

    private void createDayFields() {

        String[] dayTypes = {
                "Treino",
                "Descanso"
        };

        for (int i = 0; i < 7; i++) {

            TextView dayTitle = new TextView(this);

            dayTitle.setText(DAYS[i]);
            dayTitle.setTextSize(18);
            dayTitle.setPadding(0, 16, 0, 8);

            EditText dayName = new EditText(this);

            dayName.setHint("Nome do treino (ex.: Push)");
            dayName.setSingleLine(true);

            Spinner dayType = new Spinner(this);

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            dayTypes
                    );

            adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
            );

            dayType.setAdapter(adapter);

            llDaysContainer.addView(dayTitle);
            llDaysContainer.addView(dayName);
            llDaysContainer.addView(dayType);

            dayNameInputs.add(dayName);
            dayTypeInputs.add(dayType);
        }
    }

    private void savePlan() {

        String planName = etPlanName.getText()
                .toString()
                .trim();

        if (planName.isEmpty()) {
            etPlanName.setError(
                    "Introduz um nome para o plano."
            );
            return;
        }

        List<String> dayNames = new ArrayList<>();
        List<Boolean> restDays = new ArrayList<>();

        for (int i = 0; i < 7; i++) {

            String name = dayNameInputs.get(i)
                    .getText()
                    .toString()
                    .trim();

            boolean isRestDay =
                    dayTypeInputs.get(i)
                            .getSelectedItem()
                            .toString()
                            .equals("Descanso");

            if (name.isEmpty()) {
                name = isRestDay
                        ? "Descanso"
                        : "Dia " + (i + 1);
            }

            dayNames.add(name);
            restDays.add(isRestDay);
        }

        btnSavePlan.setEnabled(false);
        btnSavePlan.setText("A guardar...");

        repository.createPlanWithDays(
                planName,
                dayNames,
                restDays,
                planId -> runOnUiThread(() -> {

                    Intent result = new Intent();

                    result.putExtra("planId", planId);
                    result.putExtra("planName", planName);

                    setResult(RESULT_OK, result);
                    finish();
                })
        );
    }
}
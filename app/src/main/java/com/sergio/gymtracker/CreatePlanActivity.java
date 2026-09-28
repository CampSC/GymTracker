package com.sergio.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class CreatePlanActivity extends AppCompatActivity {

    private EditText etPlanName;
    private LinearLayout llDaysContainer;
    private Button btnSavePlan;

    private ArrayList<EditText> dayNameInputs = new ArrayList<>();
    private ArrayList<Spinner> dayTypeInputs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        etPlanName = findViewById(R.id.etPlanName);
        llDaysContainer = findViewById(R.id.llDaysContainer);
        btnSavePlan = findViewById(R.id.btnSavePlan);

        createDayFields();

        btnSavePlan.setOnClickListener(v -> savePlan());
    }

    private void createDayFields() {

        String[] dayTypes = {"Treino", "Descanso"};

        for (int i = 1; i <= 7; i++) {

            // Criar o título do dia
            TextView dayTitle = new TextView(this);
            dayTitle.setText("Dia " + i);
            dayTitle.setTextSize(18);
            dayTitle.setPadding(0, 16, 0, 8);

            // Criar campo para o nome
            EditText dayName = new EditText(this);
            dayName.setHint("Nome do dia (ex: Push)");
            dayName.setSingleLine(true);

            // Criar seleção do tipo de dia
            Spinner dayType = new Spinner(this);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    dayTypes
            );

            adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
            );

            dayType.setAdapter(adapter);

            // Adicionar elementos ao formulário
            llDaysContainer.addView(dayTitle);
            llDaysContainer.addView(dayName);
            llDaysContainer.addView(dayType);

            // Guardar referências aos campos
            dayNameInputs.add(dayName);
            dayTypeInputs.add(dayType);
        }
    }

    private void savePlan() {

        String planName = etPlanName.getText().toString().trim();

        if (planName.isEmpty()) {
            etPlanName.setError("Introduz um nome para o plano");
            return;
        }

        TrainingPlan plan = new TrainingPlan(1, planName);

        for (int i = 0; i < 7; i++) {

            String dayName = dayNameInputs.get(i)
                    .getText().toString().trim();

            String dayType = dayTypeInputs.get(i)
                    .getSelectedItem().toString();

            if (dayName.isEmpty()) {
                dayName = dayType.equals("Descanso")
                        ? "Descanso"
                        : "Dia " + (i + 1);
            }

            boolean isRestDay = dayType.equals("Descanso");

            PlanDay day = new PlanDay(
                    i + 1,
                    i + 1,
                    dayName,
                    isRestDay
            );

            plan.addDay(day);
        }

        getIntent().putExtra("planName", plan.getName());

        // Devolver o plano à atividade anterior
        android.content.Intent resultIntent =
                new android.content.Intent();

        resultIntent.putExtra("plan", plan);

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
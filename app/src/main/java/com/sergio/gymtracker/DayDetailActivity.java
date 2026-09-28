
package com.sergio.gymtracker;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DayDetailActivity extends AppCompatActivity {

    private TextView tvDayName;
    private TextView tvExerciseCount;
    private LinearLayout llExercisesContainer;
    private Button btnAddExercise;

    private TrainingPlan plan;
    private PlanDay selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_detail);

        tvDayName = findViewById(R.id.tvDayName);
        tvExerciseCount = findViewById(R.id.tvExerciseCount);
        llExercisesContainer = findViewById(R.id.llExercisesContainer);
        btnAddExercise = findViewById(R.id.btnAddExercise);

        plan = (TrainingPlan) getIntent()
                .getSerializableExtra("plan");

        int dayId = getIntent().getIntExtra("dayId", -1);

        if (plan == null) {
            finish();
            return;
        }

        for (PlanDay day : plan.getDays()) {

            if (day.getId() == dayId) {
                selectedDay = day;
                break;
            }
        }

        if (selectedDay == null) {
            finish();
            return;
        }

        tvDayName.setText(selectedDay.getName());

        btnAddExercise.setOnClickListener(v -> showAddExerciseDialog());

        displayExercises();
    }

    private void displayExercises() {

        llExercisesContainer.removeAllViews();

        int exerciseCount = selectedDay.getExercises().size();

        tvExerciseCount.setText(
                exerciseCount + (exerciseCount == 1
                        ? " exercício"
                        : " exercícios")
        );

        if (exerciseCount == 0) {

            TextView emptyMessage = new TextView(this);

            emptyMessage.setText(
                    "Ainda não adicionaste exercícios a este dia."
            );

            emptyMessage.setTextSize(16);
            emptyMessage.setPadding(16, 24, 16, 24);

            llExercisesContainer.addView(emptyMessage);
        }

        for (PlanExercise exercise : selectedDay.getExercises()) {

            LinearLayout exerciseCard = new LinearLayout(this);

            exerciseCard.setOrientation(LinearLayout.VERTICAL);
            exerciseCard.setPadding(24, 24, 24, 24);
            exerciseCard.setBackgroundColor(0xFFFFFFFF);

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            cardParams.setMargins(0, 0, 0, 16);

            exerciseCard.setLayoutParams(cardParams);

            TextView nameView = new TextView(this);

            nameView.setText(exercise.getName());
            nameView.setTextSize(18);
            nameView.setTextColor(0xFF212121);

            TextView detailsView = new TextView(this);

            detailsView.setText(
                    exercise.getSets() + " séries × "
                            + exercise.getReps() + " repetições\n"
                            + exercise.getWeight() + " kg"
            );

            detailsView.setTextSize(15);
            detailsView.setTextColor(0xFF757575);

            Button removeButton = new Button(this);

            removeButton.setText("Remover");

            removeButton.setOnClickListener(v -> {

                new AlertDialog.Builder(this)
                        .setTitle("Remover exercício")
                        .setMessage(
                                "Queres remover " + exercise.getName() + "?"
                        )
                        .setPositiveButton("Remover", (dialog, which) -> {

                            selectedDay.removeExercise(exercise);

                            displayExercises();

                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });

            exerciseCard.addView(nameView);
            exerciseCard.addView(detailsView);
            exerciseCard.addView(removeButton);

            llExercisesContainer.addView(exerciseCard);
        }
    }

    private void showAddExerciseDialog() {

        LinearLayout form = new LinearLayout(this);

        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(48, 16, 48, 0);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Nome do exercício");
        nameInput.setSingleLine(true);

        EditText setsInput = new EditText(this);
        setsInput.setHint("Número de séries");
        setsInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText repsInput = new EditText(this);
        repsInput.setHint("Número de repetições");
        repsInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText weightInput = new EditText(this);
        weightInput.setHint("Peso inicial (kg)");
        weightInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        EditText notesInput = new EditText(this);
        notesInput.setHint("Notas (opcional)");

        form.addView(nameInput);
        form.addView(setsInput);
        form.addView(repsInput);
        form.addView(weightInput);
        form.addView(notesInput);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Adicionar exercício")
                .setView(form)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.setOnShowListener(d -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        String name = nameInput.getText()
                                .toString().trim();

                        String setsText = setsInput.getText()
                                .toString().trim();

                        String repsText = repsInput.getText()
                                .toString().trim();

                        String weightText = weightInput.getText()
                                .toString().trim();

                        String notes = notesInput.getText()
                                .toString().trim();

                        if (name.isEmpty()
                                || setsText.isEmpty()
                                || repsText.isEmpty()
                                || weightText.isEmpty()) {

                            Toast.makeText(
                                    this,
                                    "Preenche todos os campos obrigatórios.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        try {

                            int sets = Integer.parseInt(setsText);
                            int reps = Integer.parseInt(repsText);
                            double weight = Double.parseDouble(
                                    weightText.replace(',', '.')
                            );

                            if (sets <= 0 || reps <= 0 || weight < 0) {

                                Toast.makeText(
                                        this,
                                        "Introduz valores válidos.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            int exerciseId =
                                    selectedDay.getExercises().size() + 1;

                            PlanExercise exercise = new PlanExercise(
                                    exerciseId,
                                    name,
                                    sets,
                                    reps,
                                    weight,
                                    notes
                            );

                            selectedDay.addExercise(exercise);

                            displayExercises();

                            dialog.dismiss();

                        } catch (NumberFormatException e) {

                            Toast.makeText(
                                    this,
                                    "Verifica os valores introduzidos.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
        });

        dialog.show();
    }
}
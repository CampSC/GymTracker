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

import com.sergio.gymtracker.data.ExerciseLibraryEntity;
import com.sergio.gymtracker.data.PlanExerciseEntity;
import com.sergio.gymtracker.repository.GymRepository;

import java.util.ArrayList;
import java.util.List;

public class DayDetailActivity extends AppCompatActivity {

    private TextView tvDayName;
    private TextView tvExerciseCount;
    private LinearLayout llExercisesContainer;
    private Button btnAddExercise;

    private long dayId;
    private String dayName;

    private GymRepository repository;

    private List<PlanExerciseEntity> exercises =
            new ArrayList<>();

    private List<ExerciseLibraryEntity> libraryExercises =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_detail);

        tvDayName = findViewById(R.id.tvDayName);
        tvExerciseCount = findViewById(R.id.tvExerciseCount);
        llExercisesContainer =
                findViewById(R.id.llExercisesContainer);
        btnAddExercise = findViewById(R.id.btnAddExercise);

        dayId = getIntent().getLongExtra("dayId", -1);
        dayName = getIntent().getStringExtra("dayName");

        if (dayId == -1 || dayName == null) {
            finish();
            return;
        }

        tvDayName.setText(dayName);

        repository = new GymRepository(getApplicationContext());

        repository.observeExercises(dayId).observe(this, exerciseList -> {
            exercises = exerciseList;
            displayExercises();
        });

        btnAddExercise.setOnClickListener(
                v -> loadLibraryAndSelectExercise()
        );
    }

    // =====================================================
    // LISTA DE EXERCÍCIOS DO DIA
    // =====================================================

    private void displayExercises() {

        llExercisesContainer.removeAllViews();

        int count = exercises == null ? 0 : exercises.size();

        tvExerciseCount.setText(
                count + (count == 1
                        ? " exercício"
                        : " exercícios")
        );

        if (count == 0) {

            TextView empty = new TextView(this);
            empty.setText(
                    "Ainda não adicionaste exercícios a este dia."
            );
            empty.setTextSize(16);
            empty.setPadding(16, 24, 16, 24);

            llExercisesContainer.addView(empty);
            return;
        }

        for (int i = 0; i < exercises.size(); i++) {

            PlanExerciseEntity exercise = exercises.get(i);

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(24, 24, 24, 24);
            card.setBackgroundColor(0xFFFFFFFF);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            params.setMargins(0, 0, 0, 16);
            card.setLayoutParams(params);

            TextView name = new TextView(this);
            name.setText(exercise.name);
            name.setTextSize(18);
            name.setTextColor(0xFF212121);

            TextView details = new TextView(this);
            details.setText(
                    exercise.sets + " séries × "
                            + exercise.reps + " repetições\n"
                            + exercise.weight + " kg"
                            + (exercise.notes == null
                            || exercise.notes.isEmpty()
                            ? ""
                            : "\n" + exercise.notes)
            );
            details.setTextSize(15);
            details.setTextColor(0xFF757575);


            // Linha com os controlos de ordenação
            LinearLayout reorderControls = new LinearLayout(this);
            reorderControls.setOrientation(LinearLayout.HORIZONTAL);

            // Botão para subir
            Button btnUp = new Button(this);
            btnUp.setText("↑ Subir");

            // Botão para descer
            Button btnDown = new Button(this);
            btnDown.setText("↓ Descer");

            // Desativar os botões nos limites da lista
            btnUp.setEnabled(i > 0);
            btnDown.setEnabled(i < exercises.size() - 1);

            // Mover o exercício uma posição para cima
            btnUp.setOnClickListener(v ->
                    repository.moveExercise(
                            dayId,
                            exercise.id,
                            -1
                    )
            );

            // Mover o exercício uma posição para baixo
            btnDown.setOnClickListener(v ->
                    repository.moveExercise(
                            dayId,
                            exercise.id,
                            1
                    )
            );

            // Adicionar os botões à linha
            reorderControls.addView(btnUp);
            reorderControls.addView(btnDown);

            Button edit = new Button(this);
            edit.setText("Editar");

            edit.setOnClickListener(v ->
                    showExerciseConfiguration(exercise.name, exercise)
            );

            Button remove = new Button(this);
            remove.setText("Remover");

            remove.setOnClickListener(v ->
                    new AlertDialog.Builder(this)
                            .setTitle("Remover exercício")
                            .setMessage(
                                    "Queres remover " + exercise.name + "?"
                            )
                            .setPositiveButton(
                                    "Remover",
                                    (dialog, which) ->
                                            repository.removeExercise(exercise.id)
                            )
                            .setNegativeButton("Cancelar", null)
                            .show()
            );

            card.addView(name);
            card.addView(details);
            card.addView(reorderControls);
            card.addView(edit);
            card.addView(remove);

            llExercisesContainer.addView(card);
        }
    }

    // =====================================================
    // SELEÇÃO NA BIBLIOTECA
    // =====================================================

    private void loadLibraryAndSelectExercise() {

        repository.getLibraryExercises(result ->
                runOnUiThread(() -> {

                    libraryExercises = result;

                    if (libraryExercises.isEmpty()) {

                        Toast.makeText(
                                this,
                                "A biblioteca está vazia.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    showExercisePicker();
                })
        );
    }

    private void showExercisePicker() {

        String[] names = new String[libraryExercises.size()];

        for (int i = 0; i < libraryExercises.size(); i++) {

            ExerciseLibraryEntity exercise =
                    libraryExercises.get(i);

            names[i] = exercise.name
                    + " • "
                    + exercise.muscleGroup;
        }

        new AlertDialog.Builder(this)
                .setTitle("Selecionar exercício")
                .setItems(names, (dialog, which) -> {

                    ExerciseLibraryEntity selected =
                            libraryExercises.get(which);

                    showExerciseConfiguration(selected.name, null);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // =====================================================
    // CONFIGURAR EXERCÍCIO
    // =====================================================

    private void showExerciseConfiguration(String exerciseName, PlanExerciseEntity existingExercise) {

        // Se existingExercise não for null, estamos a editar.
        // Caso contrário, estamos a adicionar um exercício novo.
        boolean isEditing = existingExercise != null;

        // Criar o formulário
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(48, 16, 48, 0);

        // Campo das séries
        EditText setsInput = new EditText(this);
        setsInput.setHint("Número de séries");
        setsInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Campo das repetições
        EditText repsInput = new EditText(this);
        repsInput.setHint("Número de repetições");
        repsInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Campo do peso
        EditText weightInput = new EditText(this);
        weightInput.setHint("Peso de referência (kg)");
        weightInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        // Campo das notas
        EditText notesInput = new EditText(this);
        notesInput.setHint("Notas (opcional)");

        // Se estamos a editar, preencher os campos
        // com os dados que já estão guardados.
        if (isEditing) {

            setsInput.setText(
                    String.valueOf(existingExercise.sets)
            );

            repsInput.setText(
                    String.valueOf(existingExercise.reps)
            );

            weightInput.setText(
                    String.valueOf(existingExercise.weight)
            );

            if (existingExercise.notes != null) {
                notesInput.setText(existingExercise.notes);
            }
        }

        // Adicionar os campos ao formulário
        form.addView(setsInput);
        form.addView(repsInput);
        form.addView(weightInput);
        form.addView(notesInput);

        // O texto do botão depende da operação
        String buttonText = isEditing
                ? "Guardar alterações"
                : "Adicionar";

        // Criar a janela
        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle(exerciseName)
                        .setView(form)
                        .setPositiveButton(buttonText, null)
                        .setNegativeButton("Cancelar", null)
                        .create();

        // Configurar o botão de confirmação
        dialog.setOnShowListener(d ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {

                            String setsText =
                                    setsInput.getText().toString().trim();

                            String repsText =
                                    repsInput.getText().toString().trim();

                            String weightText =
                                    weightInput.getText().toString().trim();

                            String notes =
                                    notesInput.getText().toString().trim();

                            // Validar campos obrigatórios
                            if (setsText.isEmpty()
                                    || repsText.isEmpty()
                                    || weightText.isEmpty()) {

                                Toast.makeText(
                                        this,
                                        "Preenche séries, repetições e peso.",
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

                                // Validar valores
                                if (sets <= 0 || reps <= 0 || weight < 0) {

                                    Toast.makeText(
                                            this,
                                            "Introduz valores válidos.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                if (isEditing) {

                                    // Atualizar o exercício existente
                                    existingExercise.sets = sets;
                                    existingExercise.reps = reps;
                                    existingExercise.weight = weight;
                                    existingExercise.notes = notes;

                                    repository.updateExercise(
                                            existingExercise
                                    );

                                } else {

                                    // Criar um exercício novo
                                    repository.addExercise(
                                            dayId,
                                            exerciseName,
                                            sets,
                                            reps,
                                            weight,
                                            notes
                                    );
                                }

                                // Fechar o formulário
                                dialog.dismiss();

                            } catch (NumberFormatException e) {

                                Toast.makeText(
                                        this,
                                        "Verifica os valores introduzidos.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        })
        );

        dialog.show();
    }
}
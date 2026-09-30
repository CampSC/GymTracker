package com.sergio.gymtracker;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.sergio.gymtracker.data.AppDatabase;
import com.sergio.gymtracker.data.ExerciseLibraryEntity;
import com.sergio.gymtracker.data.ExerciseCatalogImporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExerciseLibraryActivity extends AppCompatActivity {

    private EditText etSearch;
    private Spinner spMuscleGroup;
    private LinearLayout llExercises;

    private AppDatabase database;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private final List<ExerciseLibraryEntity> allExercises =
            new ArrayList<>();

    private final String[] muscleGroups = {
            "Todos",
            "Peito",
            "Costas",
            "Ombros",
            "Bíceps",
            "Tríceps",
            "Quadríceps",
            "Posterior de coxa",
            "Glúteos",
            "Gémeos",
            "Abdominais",
            "Antebraços"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_library);

        etSearch = findViewById(R.id.etSearch);
        spMuscleGroup = findViewById(R.id.spMuscleGroup);
        llExercises = findViewById(R.id.llExercises);

        Button btnAddExercise =
                findViewById(R.id.btnAddCustomExercise);

        database = AppDatabase.getInstance(this);

        setupMuscleGroupSpinner();

        executor.execute(() -> {
            ExerciseCatalogImporter.importCatalog(this, database);
            runOnUiThread(this::loadExercises);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {
                filterExercises();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        spMuscleGroup.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        filterExercises();
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent
                    ) {
                    }
                }
        );

        btnAddExercise.setOnClickListener(
                v -> showAddExerciseDialog()
        );
    }

    private void setupMuscleGroupSpinner() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        muscleGroups
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spMuscleGroup.setAdapter(adapter);
    }

    // =====================================================
    // EXERCÍCIOS PREDEFINIDOS
    // =====================================================

    private void seedExercisesIfNeeded() {

        executor.execute(() -> {

            int count = database.exerciseLibraryDao().count();

            if (count == 0) {

                List<ExerciseLibraryEntity> exercises =
                        createDefaultExercises();

                database.exerciseLibraryDao().insertAll(exercises);
            }

            runOnUiThread(this::loadExercises);
        });
    }

    private List<ExerciseLibraryEntity> createDefaultExercises() {

        List<ExerciseLibraryEntity> exercises =
                new ArrayList<>();

        addExercises(exercises, "Peito", "Barra",
                "Supino plano",
                "Supino inclinado",
                "Supino declinado");

        addExercises(exercises, "Peito", "Halteres",
                "Supino plano com halteres",
                "Supino inclinado com halteres",
                "Crucifixo com halteres");

        addExercises(exercises, "Peito", "Máquina",
                "Chest press",
                "Peck deck",
                "Crossover");

        addExercises(exercises, "Costas", "Barra",
                "Peso morto",
                "Remada curvada",
                "Pull-up");

        addExercises(exercises, "Costas", "Máquina",
                "Puxada frontal",
                "Remada sentada",
                "Remada unilateral na máquina");

        addExercises(exercises, "Costas", "Halteres",
                "Remada unilateral com halter",
                "Pullover com halter");

        addExercises(exercises, "Ombros", "Halteres",
                "Press militar com halteres",
                "Elevação lateral",
                "Elevação frontal",
                "Reverse fly");

        addExercises(exercises, "Ombros", "Barra",
                "Press militar",
                "Arnold press");

        addExercises(exercises, "Ombros", "Máquina",
                "Elevação lateral na máquina",
                "Reverse pec deck");

        addExercises(exercises, "Bíceps", "Halteres",
                "Curl de bíceps",
                "Curl martelo",
                "Curl inclinado");

        addExercises(exercises, "Bíceps", "Barra",
                "Curl com barra",
                "Curl com barra EZ");

        addExercises(exercises, "Bíceps", "Cabo",
                "Curl de bíceps no cabo",
                "Curl bayesian");

        addExercises(exercises, "Tríceps", "Cabo",
                "Tríceps na polia",
                "Tríceps com corda",
                "Extensão de tríceps acima da cabeça");

        addExercises(exercises, "Tríceps", "Halteres",
                "Extensão de tríceps com halter",
                "Skull crusher");

        addExercises(exercises, "Tríceps", "Peso corporal",
                "Fundos em paralelas",
                "Flexões fechadas");

        addExercises(exercises, "Quadríceps", "Barra",
                "Agachamento livre",
                "Agachamento frontal");

        addExercises(exercises, "Quadríceps", "Máquina",
                "Leg press",
                "Cadeira extensora",
                "Hack squat");

        addExercises(exercises, "Quadríceps", "Peso corporal",
                "Agachamento búlgaro",
                "Afundos");

        addExercises(exercises, "Posterior de coxa", "Máquina",
                "Leg curl sentado",
                "Leg curl deitado");

        addExercises(exercises, "Posterior de coxa", "Barra",
                "Peso morto romeno",
                "Good morning");

        addExercises(exercises, "Posterior de coxa", "Halteres",
                "Peso morto romeno com halteres");

        addExercises(exercises, "Glúteos", "Barra",
                "Hip thrust",
                "Agachamento sumo");

        addExercises(exercises, "Glúteos", "Máquina",
                "Abdução de anca",
                "Kickback na máquina");

        addExercises(exercises, "Glúteos", "Cabo",
                "Kickback no cabo");

        addExercises(exercises, "Gémeos", "Máquina",
                "Elevação de gémeos sentado",
                "Elevação de gémeos em pé");

        addExercises(exercises, "Gémeos", "Peso corporal",
                "Elevação de gémeos unilateral");

        addExercises(exercises, "Abdominais", "Peso corporal",
                "Crunch",
                "Elevação de pernas",
                "Prancha");

        addExercises(exercises, "Abdominais", "Cabo",
                "Crunch no cabo",
                "Woodchopper");

        addExercises(exercises, "Antebraços", "Halteres",
                "Wrist curl",
                "Reverse wrist curl");

        return exercises;
    }

    private void addExercises(
            List<ExerciseLibraryEntity> list,
            String muscleGroup,
            String equipment,
            String... names
    ) {

        for (String name : names) {

            list.add(
                    new ExerciseLibraryEntity(
                            name,
                            muscleGroup,
                            equipment,
                            false,
                            false
                    )
            );
        }
    }

    // =====================================================
    // CARREGAR E FILTRAR
    // =====================================================

    private void loadExercises() {

        executor.execute(() -> {

            List<ExerciseLibraryEntity> result =
                    database.exerciseLibraryDao().getAllSync();

            runOnUiThread(() -> {

                allExercises.clear();
                allExercises.addAll(result);

                filterExercises();
            });
        });
    }

    private void filterExercises() {

        String query = etSearch.getText()
                .toString()
                .trim()
                .toLowerCase();

        String selectedGroup =
                spMuscleGroup.getSelectedItem() == null
                        ? "Todos"
                        : spMuscleGroup.getSelectedItem().toString();

        List<ExerciseLibraryEntity> filtered =
                new ArrayList<>();

        for (ExerciseLibraryEntity exercise : allExercises) {

            boolean matchesName =
                    exercise.name.toLowerCase().contains(query);

            boolean matchesGroup =
                    selectedGroup.equals("Todos")
                            || exercise.muscleGroup.equals(selectedGroup);

            if (matchesName && matchesGroup) {
                filtered.add(exercise);
            }
        }

        displayExercises(filtered);
    }

    // =====================================================
    // MOSTRAR EXERCÍCIOS
    // =====================================================

    private void displayExercises(
            List<ExerciseLibraryEntity> exercises
    ) {

        llExercises.removeAllViews();

        if (exercises.isEmpty()) {

            TextView empty = new TextView(this);

            empty.setText("Não foram encontrados exercícios.");
            empty.setTextSize(16);
            empty.setPadding(16, 32, 16, 32);
            empty.setGravity(Gravity.CENTER);

            llExercises.addView(empty);
            return;
        }

        for (ExerciseLibraryEntity exercise : exercises) {

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(24, 20, 24, 20);
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
                    exercise.muscleGroup
                            + " • "
                            + exercise.equipment
                            + (exercise.custom ? " • Personalizado" : "")
            );
            details.setTextSize(14);
            details.setTextColor(0xFF757575);

            LinearLayout actions = new LinearLayout(this);
            actions.setOrientation(LinearLayout.HORIZONTAL);

            Button favoriteButton = new Button(this);
            favoriteButton.setText(
                    exercise.favorite ? "★ Favorito" : "☆ Favorito"
            );

            favoriteButton.setOnClickListener(v -> {

                executor.execute(() -> {

                    database.exerciseLibraryDao().setFavorite(
                            exercise.id,
                            !exercise.favorite
                    );

                    runOnUiThread(this::loadExercises);
                });
            });

            actions.addView(favoriteButton);

            if (exercise.custom) {

                Button deleteButton = new Button(this);
                deleteButton.setText("Eliminar");

                deleteButton.setOnClickListener(v -> {

                    new AlertDialog.Builder(this)
                            .setTitle("Eliminar exercício")
                            .setMessage(
                                    "Queres eliminar " + exercise.name + "?"
                            )
                            .setPositiveButton(
                                    "Eliminar",
                                    (dialog, which) -> executor.execute(() -> {

                                        database.exerciseLibraryDao()
                                                .delete(exercise);

                                        runOnUiThread(this::loadExercises);
                                    })
                            )
                            .setNegativeButton("Cancelar", null)
                            .show();
                });

                actions.addView(deleteButton);
            }

            card.addView(name);
            card.addView(details);
            card.addView(actions);

            llExercises.addView(card);
        }
    }

    // =====================================================
    // CRIAR EXERCÍCIO PERSONALIZADO
    // =====================================================

    private void showAddExerciseDialog() {

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(48, 16, 48, 0);

        EditText etName = new EditText(this);
        etName.setHint("Nome do exercício");

        Spinner spGroup = new Spinner(this);

        String[] customGroups =
                Arrays.copyOfRange(muscleGroups, 1, muscleGroups.length);

        ArrayAdapter<String> groupAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        customGroups
                );

        groupAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spGroup.setAdapter(groupAdapter);

        EditText etEquipment = new EditText(this);
        etEquipment.setHint("Equipamento (ex.: halteres)");

        form.addView(etName);
        form.addView(spGroup);
        form.addView(etEquipment);

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Novo exercício")
                        .setView(form)
                        .setPositiveButton("Guardar", null)
                        .setNegativeButton("Cancelar", null)
                        .create();

        dialog.setOnShowListener(d -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        String name = etName.getText()
                                .toString()
                                .trim();

                        String equipment = etEquipment.getText()
                                .toString()
                                .trim();

                        String group = spGroup.getSelectedItem()
                                .toString();

                        if (name.isEmpty()) {

                            etName.setError(
                                    "Introduz o nome do exercício."
                            );

                            return;
                        }

                        if (equipment.isEmpty()) {
                            equipment = "Não especificado";
                        }

                        String finalEquipment = equipment;

                        executor.execute(() -> {

                            ExerciseLibraryEntity exercise =
                                    new ExerciseLibraryEntity(
                                            name,
                                            group,
                                            finalEquipment,
                                            true,
                                            false
                                    );

                            database.exerciseLibraryDao()
                                    .insert(exercise);

                            runOnUiThread(() -> {

                                dialog.dismiss();
                                loadExercises();

                                Toast.makeText(
                                        this,
                                        "Exercício criado!",
                                        Toast.LENGTH_SHORT
                                ).show();
                            });
                        });
                    });
        });

        dialog.show();
    }
}
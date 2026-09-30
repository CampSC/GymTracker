package com.sergio.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.sergio.gymtracker.data.PlanEntity;
import com.sergio.gymtracker.repository.GymRepository;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout llPlansContainer;
    private TextView tvEmptyPlans;

    private GymRepository repository;

    private List<PlanEntity> plans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llPlansContainer = findViewById(R.id.llPlansContainer);
        tvEmptyPlans = findViewById(R.id.tvEmptyPlans);

        Button btnCreatePlan = findViewById(R.id.btnCreatePlan);
        Button btnSeePlans = findViewById(R.id.btnSeePlans);
        Button btnExerciseLibrary =
                findViewById(R.id.btnExerciseLibrary);

        repository = new GymRepository(getApplicationContext());

        repository.observePlans().observe(this, planList -> {
            plans = planList;
            updatePlansList();
        });

        btnCreatePlan.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    CreatePlanActivity.class
            );

            startActivity(intent);
        });

        btnSeePlans.setOnClickListener(v -> updatePlansList());

        btnExerciseLibrary.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ExerciseLibraryActivity.class
            );

            startActivity(intent);
        });
    }

    // =====================================================
    // LISTA DE PLANOS
    // =====================================================

    private void updatePlansList() {

        llPlansContainer.removeAllViews();

        boolean isEmpty = plans == null || plans.isEmpty();

        tvEmptyPlans.setVisibility(
                isEmpty ? View.VISIBLE : View.GONE
        );

        if (isEmpty) {
            return;
        }

        for (PlanEntity plan : plans) {

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

            TextView title = new TextView(this);
            title.setText(plan.name);
            title.setTextSize(20);
            title.setTextColor(0xFF212121);

            TextView status = new TextView(this);
            status.setText(getPlanStatus(plan));
            status.setTextSize(14);
            status.setTextColor(0xFF757575);
            status.setPadding(0, 8, 0, 0);

            TextView hint = new TextView(this);
            hint.setText("Toca para abrir • Mantém pressionado para gerir");
            hint.setTextSize(12);
            hint.setTextColor(0xFF9E9E9E);
            hint.setPadding(0, 8, 0, 0);

            card.addView(title);
            card.addView(status);
            card.addView(hint);

            card.setOnClickListener(v -> openPlan(plan));

            card.setOnLongClickListener(v -> {
                showPlanActions(plan);
                return true;
            });

            llPlansContainer.addView(card);
        }
    }

    private String getPlanStatus(PlanEntity plan) {

        String status;

        if (PlanEntity.STATUS_ACTIVE.equals(plan.status)) {
            status = "Ativo";
        } else if (PlanEntity.STATUS_DRAFT.equals(plan.status)) {
            status = "Rascunho";
        } else {
            status = "Pronto";
        }

        if (plan.savedAsTemplate) {
            status += " • Modelo";
        }

        return status;
    }

    // =====================================================
    // ABRIR PLANO
    // =====================================================

    private void openPlan(PlanEntity plan) {

        Intent intent = new Intent(
                MainActivity.this,
                PlanDetailActivity.class
        );

        intent.putExtra("planId", plan.id);
        intent.putExtra("planName", plan.name);

        startActivity(intent);
    }

    // =====================================================
    // MENU DE GESTÃO
    // =====================================================

    private void showPlanActions(PlanEntity plan) {

        java.util.ArrayList<String> actions =
                new java.util.ArrayList<>();

        java.util.ArrayList<String> actionKeys =
                new java.util.ArrayList<>();

        if (PlanEntity.STATUS_DRAFT.equals(plan.status)) {
            actions.add("Finalizar plano");
            actionKeys.add("ready");
        }

        if (!PlanEntity.STATUS_ACTIVE.equals(plan.status)) {
            actions.add("Ativar plano");
            actionKeys.add("activate");
        } else {
            actions.add("Desativar plano");
            actionKeys.add("deactivate");
        }

        actions.add(
                plan.savedAsTemplate
                        ? "Remover dos modelos"
                        : "Guardar como modelo"
        );
        actionKeys.add("template");

        actions.add("Eliminar plano");
        actionKeys.add("delete");

        String[] options = actions.toArray(new String[0]);

        new AlertDialog.Builder(this)
                .setTitle(plan.name)
                .setItems(options, (dialog, which) -> {

                    String action = actionKeys.get(which);

                    switch (action) {

                        case "ready":
                            finalizePlan(plan);
                            break;

                        case "activate":
                            activatePlan(plan);
                            break;

                        case "deactivate":
                            deactivatePlan(plan);
                            break;

                        case "template":
                            toggleTemplate(plan);
                            break;

                        case "delete":
                            confirmDeletePlan(plan);
                            break;
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // =====================================================
    // FINALIZAR
    // =====================================================

    private void finalizePlan(PlanEntity plan) {

        new AlertDialog.Builder(this)
                .setTitle("Finalizar plano")
                .setMessage(
                        "O plano \"" + plan.name
                                + "\" passará a estar pronto para utilização."
                )
                .setPositiveButton("Finalizar", (dialog, which) -> {

                    repository.updatePlanStatus(
                            plan.id,
                            PlanEntity.STATUS_READY
                    );

                    Toast.makeText(
                            this,
                            "Plano finalizado.",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // =====================================================
    // ATIVAR / DESATIVAR
    // =====================================================

    private void activatePlan(PlanEntity plan) {

        new AlertDialog.Builder(this)
                .setTitle("Ativar plano")
                .setMessage(
                        "Queres ativar \"" + plan.name
                                + "\"? O plano atualmente ativo será desativado."
                )
                .setPositiveButton("Ativar", (dialog, which) -> {

                    repository.setActivePlan(plan.id);

                    Toast.makeText(
                            this,
                            "Plano ativado.",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deactivatePlan(PlanEntity plan) {

        new AlertDialog.Builder(this)
                .setTitle("Desativar plano")
                .setMessage(
                        "Queres desativar o plano ativo?"
                )
                .setPositiveButton("Desativar", (dialog, which) -> {

                    repository.deactivatePlan(plan.id);

                    Toast.makeText(
                            this,
                            "Plano desativado.",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // =====================================================
    // MODELOS
    // =====================================================

    private void toggleTemplate(PlanEntity plan) {

        boolean newValue = !plan.savedAsTemplate;

        repository.savePlanAsTemplate(
                plan.id,
                newValue
        );

        Toast.makeText(
                this,
                newValue
                        ? "Plano guardado como modelo."
                        : "Plano removido dos modelos.",
                Toast.LENGTH_SHORT
        ).show();
    }

    // =====================================================
    // ELIMINAR
    // =====================================================

    private void confirmDeletePlan(PlanEntity plan) {

        new AlertDialog.Builder(this)
                .setTitle("Eliminar plano")
                .setMessage(
                        "Tens a certeza de que queres eliminar \""
                                + plan.name
                                + "\"? Esta ação não pode ser anulada."
                )
                .setPositiveButton("Eliminar", (dialog, which) -> {

                    repository.deletePlan(plan);

                    Toast.makeText(
                            this,
                            "Plano eliminado.",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
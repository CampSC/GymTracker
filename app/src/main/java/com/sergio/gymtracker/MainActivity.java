
package com.sergio.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private LinearLayout llPlansContainer;
    private TextView tvEmptyPlans;
    private Button btnCreatePlan;
    private Button btnSeePlans;

    private ArrayList<TrainingPlan> plans = new ArrayList<>();

    private ActivityResultLauncher<Intent> createPlanLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llPlansContainer = findViewById(R.id.llPlansContainer);
        tvEmptyPlans = findViewById(R.id.tvEmptyPlans);
        btnCreatePlan = findViewById(R.id.btnCreatePlan);
        btnSeePlans = findViewById(R.id.btnSeePlans);

        createPlanLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK
                            && result.getData() != null) {

                        TrainingPlan plan = (TrainingPlan)
                                result.getData().getSerializableExtra("plan");

                        if (plan != null) {
                            plan.setActive(plans.isEmpty());

                            plans.add(plan);
                            updatePlansList();

                            Toast.makeText(
                                    this,
                                    "Plano criado com sucesso!",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                }
        );

        btnCreatePlan.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    CreatePlanActivity.class
            );

            createPlanLauncher.launch(intent);
        });

        btnSeePlans.setOnClickListener(v -> {
            updatePlansList();
        });
        updatePlansList();
    }

    private void updatePlansList() {

        llPlansContainer.removeAllViews();

        tvEmptyPlans.setVisibility(
                plans.isEmpty() ? TextView.VISIBLE : TextView.GONE
        );

        for (TrainingPlan plan : plans) {

            TextView planView = new TextView(this);

            String status = plan.isActive()
                    ? " (Plano ativo)"
                    : "";

            planView.setText(plan.getName() + status);
            planView.setTextSize(18);
            planView.setPadding(24, 24, 24, 24);

            planView.setOnClickListener(v -> {

                Intent intent = new Intent(
                        MainActivity.this,
                        PlanDetailActivity.class
                );

                intent.putExtra("plan", plan);

                startActivity(intent);
            });

            llPlansContainer.addView(planView);
        }
    }
}
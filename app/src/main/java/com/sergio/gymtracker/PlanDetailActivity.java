
package com.sergio.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PlanDetailActivity extends AppCompatActivity {

    private TextView tvPlanName;
    private LinearLayout llDaysContainer;

    private TrainingPlan plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);

        tvPlanName = findViewById(R.id.tvPlanName);
        llDaysContainer = findViewById(R.id.llDaysContainer);

        plan = (TrainingPlan) getIntent()
                .getSerializableExtra("plan");

        if (plan != null) {
            tvPlanName.setText(plan.getName());
            displayDays();
        }
    }

    private void displayDays() {

        llDaysContainer.removeAllViews();

        for (PlanDay day : plan.getDays()) {

            TextView dayView = new TextView(this);

            String dayInfo;

            if (day.isRestDay()) {
                dayInfo = day.getName() + "\nDia de descanso";
            } else {
                dayInfo = day.getName() + "\nTreino";
            }

            dayView.setText(dayInfo);
            dayView.setTextSize(18);
            dayView.setPadding(24, 24, 24, 24);
            dayView.setBackgroundColor(0xFFFFFFFF);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            params.setMargins(0, 0, 0, 16);

            dayView.setLayoutParams(params);

            dayView.setOnClickListener(v -> {

                if (day.isRestDay()) {
                    return;
                }

                Intent intent = new Intent(
                        PlanDetailActivity.this,
                        DayDetailActivity.class
                );

                intent.putExtra("plan", plan);
                intent.putExtra("dayId", day.getId());

                startActivity(intent);
            });

            llDaysContainer.addView(dayView);
        }
    }
}
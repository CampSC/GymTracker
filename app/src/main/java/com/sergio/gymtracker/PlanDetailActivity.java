package com.sergio.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sergio.gymtracker.data.PlanDayEntity;
import com.sergio.gymtracker.repository.GymRepository;

import java.util.ArrayList;
import java.util.List;

public class PlanDetailActivity extends AppCompatActivity {

    private TextView tvPlanName;
    private LinearLayout llDaysContainer;

    private long planId;
    private String planName;

    private GymRepository repository;

    private List<PlanDayEntity> days = new ArrayList<>();

    private static final String[] WEEKDAYS = {
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
        setContentView(R.layout.activity_plan_detail);

        tvPlanName = findViewById(R.id.tvPlanName);
        llDaysContainer = findViewById(R.id.llDaysContainer);

        planId = getIntent().getLongExtra("planId", -1);
        planName = getIntent().getStringExtra("planName");

        if (planId == -1 || planName == null) {
            finish();
            return;
        }

        tvPlanName.setText(planName);

        repository = new GymRepository(getApplicationContext());

        repository.observeDays(planId).observe(this, dayList -> {
            days = dayList;
            displayDays();
        });
    }

    private void displayDays() {

        llDaysContainer.removeAllViews();

        if (days == null) {
            return;
        }

        for (PlanDayEntity day : days) {

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

            String weekday = day.dayNumber >= 1 && day.dayNumber <= 7
                    ? WEEKDAYS[day.dayNumber - 1]
                    : "Dia " + day.dayNumber;

            title.setText(weekday);
            title.setTextSize(14);
            title.setTextColor(0xFF757575);

            TextView name = new TextView(this);
            name.setText(day.name);
            name.setTextSize(20);
            name.setTextColor(0xFF212121);

            TextView subtitle = new TextView(this);
            subtitle.setText(
                    day.restDay
                            ? "Dia de descanso"
                            : "Toca para gerir os exercícios"
            );
            subtitle.setTextSize(14);
            subtitle.setTextColor(0xFF757575);

            card.addView(title);
            card.addView(name);
            card.addView(subtitle);

            if (!day.restDay) {

                card.setOnClickListener(v -> {

                    Intent intent = new Intent(
                            PlanDetailActivity.this,
                            DayDetailActivity.class
                    );

                    intent.putExtra("dayId", day.id);
                    intent.putExtra("dayName", day.name);

                    startActivity(intent);
                });

            } else {
                card.setAlpha(0.75f);
            }

            llDaysContainer.addView(card);
        }
    }
}
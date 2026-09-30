package com.sergio.gymtracker.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {
                PlanEntity.class,
                PlanDayEntity.class,
                PlanExerciseEntity.class,
                WorkoutSessionEntity.class,
                SessionExerciseEntity.class,
                PerformedSetEntity.class,
                ExerciseLibraryEntity.class
        },
        version = 3,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PlanDao planDao();

    public abstract PlanDayDao planDayDao();

    public abstract PlanExerciseDao planExerciseDao();

    public abstract WorkoutSessionDao workoutSessionDao();

    public abstract SessionExerciseDao sessionExerciseDao();

    public abstract PerformedSetDao performedSetDao();

    public abstract ExerciseLibraryDao exerciseLibraryDao();

    private static volatile AppDatabase INSTANCE;

    // Migração da versão 2 para a versão 3
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {

        @Override
        public void migrate(SupportSQLiteDatabase db) {

            // Adiciona o estado do plano
            db.execSQL(
                    "ALTER TABLE plans ADD COLUMN status TEXT NOT NULL DEFAULT 'READY'"
            );

            // Adiciona a opção de guardar como modelo
            db.execSQL(
                    "ALTER TABLE plans ADD COLUMN savedAsTemplate INTEGER NOT NULL DEFAULT 0"
            );

            // Preserva os planos que já estavam ativos
            db.execSQL(
                    "UPDATE plans SET status = CASE WHEN active = 1 THEN 'ACTIVE' ELSE 'READY' END"
            );

            // Adiciona a posição dos exercícios
            db.execSQL(
                    "ALTER TABLE plan_exercises ADD COLUMN position INTEGER NOT NULL DEFAULT 0"
            );

            // Inicializa a posição dos exercícios existentes
            db.execSQL(
                    "UPDATE plan_exercises SET position = id"
            );

            // Cria a biblioteca local de exercícios
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS exercise_library (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "name TEXT, " +
                            "muscleGroup TEXT, " +
                            "equipment TEXT, " +
                            "`custom` INTEGER NOT NULL, " +
                            "favorite INTEGER NOT NULL)"
            );
        }
    };

    public static AppDatabase getInstance(Context context) {

        if (INSTANCE == null) {

            synchronized (AppDatabase.class) {

                if (INSTANCE == null) {

                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "gymtracker_database"
                            )
                            .addMigrations(MIGRATION_2_3)
                            .build();
                }
            }
        }

        return INSTANCE;
    }
}
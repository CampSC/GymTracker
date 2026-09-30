package com.sergio.gymtracker.data;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ExerciseCatalogImporter {

    private static final String TAG = "ExerciseCatalogImporter";
    private static final String FILE_NAME = "exercises.json";

    private ExerciseCatalogImporter() {
        // Classe utilitária
    }

    public static void importCatalog(Context context, AppDatabase database) {

        try {
            String json = readAsset(context);
            JSONObject root = new JSONObject(json);

            JSONArray exercises = root.getJSONArray("exercises");

            ExerciseLibraryDao dao = database.exerciseLibraryDao();

            int imported = 0;

            for (int i = 0; i < exercises.length(); i++) {

                JSONObject item = exercises.getJSONObject(i);

                String name = item.getString("name").trim();
                String muscleGroup = item.getString("muscleGroup").trim();
                String equipment = item.getString("equipment").trim();

                if (name.isEmpty() || muscleGroup.isEmpty()) {
                    continue;
                }

                boolean exists = dao.predefinedExerciseExists(
                        name,
                        muscleGroup
                );

                if (!exists) {

                    ExerciseLibraryEntity exercise =
                            new ExerciseLibraryEntity(
                                    name,
                                    muscleGroup,
                                    equipment,
                                    false,
                                    false
                            );

                    dao.insert(exercise);
                    imported++;
                }
            }

            Log.i(TAG, "Catálogo processado. Novos exercícios: " + imported);

        } catch (Exception e) {
            Log.e(TAG, "Erro ao importar catálogo de exercícios", e);
        }
    }

    private static String readAsset(Context context) throws Exception {

        StringBuilder builder = new StringBuilder();

        try (InputStream input =
                     context.getAssets().open(FILE_NAME);

             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(
                             input,
                             StandardCharsets.UTF_8
                     )
             )) {

            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }

        return builder.toString();
    }
}
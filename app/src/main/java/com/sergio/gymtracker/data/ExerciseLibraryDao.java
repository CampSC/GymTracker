package com.sergio.gymtracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExerciseLibraryDao {

    @Query("SELECT * FROM exercise_library ORDER BY name COLLATE NOCASE ASC")
    LiveData<List<ExerciseLibraryEntity>> getAll();

    @Query("SELECT * FROM exercise_library ORDER BY name COLLATE NOCASE ASC")
    List<ExerciseLibraryEntity> getAllSync();

    @Query("SELECT * FROM exercise_library WHERE id = :id LIMIT 1")
    ExerciseLibraryEntity getById(long id);

    @Query("SELECT * FROM exercise_library WHERE name LIKE '%' || :query || '%' ORDER BY name COLLATE NOCASE ASC")
    LiveData<List<ExerciseLibraryEntity>> search(String query);

    @Query("SELECT * FROM exercise_library WHERE muscleGroup = :muscleGroup ORDER BY name COLLATE NOCASE ASC")
    LiveData<List<ExerciseLibraryEntity>> getByMuscleGroup(String muscleGroup);

    @Query("SELECT * FROM exercise_library WHERE favorite = 1 ORDER BY name COLLATE NOCASE ASC")
    LiveData<List<ExerciseLibraryEntity>> getFavorites();

    @Query("SELECT * FROM exercise_library WHERE `custom` = 1 ORDER BY name COLLATE NOCASE ASC")
    LiveData<List<ExerciseLibraryEntity>> getCustomExercises();

    @Query("SELECT EXISTS(SELECT 1 FROM exercise_library WHERE name = :name AND muscleGroup = :muscleGroup AND `custom` = 0)")
    boolean predefinedExerciseExists(String name, String muscleGroup);

    @Insert
    long insert(ExerciseLibraryEntity exercise);

    @Insert
    void insertAll(List<ExerciseLibraryEntity> exercises);

    @Update
    void update(ExerciseLibraryEntity exercise);

    @Delete
    void delete(ExerciseLibraryEntity exercise);

    @Query("UPDATE exercise_library SET favorite = :favorite WHERE id = :id")
    void setFavorite(long id, boolean favorite);

    @Query("SELECT COUNT(*) FROM exercise_library")
    int count();
}
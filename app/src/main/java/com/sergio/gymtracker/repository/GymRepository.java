package com.sergio.gymtracker.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.sergio.gymtracker.PlanDay;
import com.sergio.gymtracker.data.AppDatabase;
import com.sergio.gymtracker.data.ExerciseLibraryDao;
import com.sergio.gymtracker.data.ExerciseLibraryEntity;
import com.sergio.gymtracker.data.PerformedSetDao;
import com.sergio.gymtracker.data.PerformedSetEntity;
import com.sergio.gymtracker.data.PlanDao;
import com.sergio.gymtracker.data.PlanDayDao;
import com.sergio.gymtracker.data.PlanDayEntity;
import com.sergio.gymtracker.data.PlanEntity;
import com.sergio.gymtracker.data.PlanExerciseDao;
import com.sergio.gymtracker.data.PlanExerciseEntity;
import com.sergio.gymtracker.data.SessionExerciseDao;
import com.sergio.gymtracker.data.SessionExerciseEntity;
import com.sergio.gymtracker.data.WorkoutSessionDao;
import com.sergio.gymtracker.data.WorkoutSessionEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.ArrayList;
public class GymRepository {

    private final PlanDao planDao;
    private final PlanDayDao planDayDao;
    private final PlanExerciseDao exerciseDao;
    private final WorkoutSessionDao sessionDao;
    private final SessionExerciseDao sessionExerciseDao;
    private final PerformedSetDao performedSetDao;
    private final ExerciseLibraryDao exerciseLibraryDao;
    private final AppDatabase database;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    public GymRepository(Context context) {

        database = AppDatabase.getInstance(context);

        planDao = database.planDao();
        planDayDao = database.planDayDao();
        exerciseDao = database.planExerciseDao();
        sessionDao = database.workoutSessionDao();
        sessionExerciseDao = database.sessionExerciseDao();
        performedSetDao = database.performedSetDao();
        exerciseLibraryDao = database.exerciseLibraryDao();
    }

    // =====================================================
    // PLANOS
    // =====================================================

    public LiveData<List<PlanEntity>> observePlans() {
        return planDao.observeAll();
    }

    public LiveData<List<PlanEntity>> observeDrafts() {
        return planDao.observeDrafts();
    }

    public LiveData<List<PlanEntity>> observeReadyPlans() {
        return planDao.observeReady();
    }

    public LiveData<List<PlanEntity>> observeTemplates() {
        return planDao.observeTemplates();
    }

    public void insertPlan(PlanEntity plan) {
        executor.execute(() -> planDao.insert(plan));
    }

    public void updatePlan(PlanEntity plan) {
        executor.execute(() -> planDao.update(plan));
    }

    public void deletePlan(PlanEntity plan) {
        executor.execute(() -> planDao.delete(plan));
    }

    public void setActivePlan(long planId) {
        executor.execute(() -> {
            planDao.clearActive();
            planDao.setActive(planId);
        });
    }

    public void updatePlanStatus(long planId, String status) {
        executor.execute(() -> planDao.updateStatus(planId, status));
    }

    public void savePlanAsTemplate(long planId, boolean saved) {
        executor.execute(() ->
                planDao.setSavedAsTemplate(planId, saved)
        );
    }

    // =====================================================
    // DIAS
    // =====================================================

    public LiveData<List<PlanDayEntity>> observeDays(long planId) {
        return planDayDao.observeByPlan(planId);
    }

    public void insertDay(PlanDayEntity day) {
        executor.execute(() -> planDayDao.insert(day));
    }

    public void updateDay(PlanDayEntity day) {
        executor.execute(() -> planDayDao.update(day));
    }

    public void deleteDay(PlanDayEntity day) {
        executor.execute(() -> planDayDao.delete(day));
    }

    // =====================================================
    // EXERCÍCIOS DOS PLANOS
    // =====================================================

    public LiveData<List<PlanExerciseEntity>> observeExercises(
            long dayId
    ) {
        return exerciseDao.getByDay(dayId);
    }

    public void insertExercise(PlanExerciseEntity exercise) {
        executor.execute(() -> exerciseDao.insert(exercise));
    }

    public void updateExercise(PlanExerciseEntity exercise) {
        executor.execute(() -> exerciseDao.update(exercise));
    }

    public void deleteExercise(PlanExerciseEntity exercise) {
        executor.execute(() -> exerciseDao.delete(exercise));
    }

    // Guarda um plano e os seus dias
    public void insertPlanWithDays(
            String planName,
            boolean active,
            List<PlanDay> days
    ) {
        executor.execute(() -> {

            PlanEntity planEntity = new PlanEntity(
                    planName,
                    active
            );

            long planId = planDao.insert(planEntity);

            for (PlanDay day : days) {

                PlanDayEntity dayEntity = new PlanDayEntity(
                        planId,
                        day.getDayNumber(),
                        day.getName(),
                        day.isRestDay()
                );

                planDayDao.insert(dayEntity);
            }
        });
    }

    // Adiciona um exercício a um dia
    public void addExercise(
            long dayId,
            String name,
            int sets,
            int reps,
            double weight,
            String notes
    ) {
        executor.execute(() -> {

            List<PlanExerciseEntity> currentExercises =
                    exerciseDao.getByDaySync(dayId);

            int nextPosition = currentExercises.size();

            PlanExerciseEntity exercise = new PlanExerciseEntity(
                    dayId,
                    name,
                    sets,
                    reps,
                    weight,
                    notes,
                    nextPosition
            );

            exerciseDao.insert(exercise);
        });
    }

    // Remove um exercício pelo ID
    public void removeExercise(long exerciseId) {
        executor.execute(() -> {

            PlanExerciseEntity exercise =
                    exerciseDao.getById(exerciseId);

            if (exercise != null) {
                exerciseDao.delete(exercise);
            }
        });
    }

    // Move um exercício para cima ou para baixo na lista
    // direction = -1 -> subir
    // direction =  1 -> descer
    public void moveExercise(long dayId, long exerciseId, int direction) {

        executor.execute(() -> {

            // Obter todos os exercícios do dia pela ordem atual
            List<PlanExerciseEntity> currentExercises =
                    exerciseDao.getByDaySync(dayId);

            // Encontrar a posição do exercício que queremos mover
            int currentIndex = -1;

            for (int i = 0; i < currentExercises.size(); i++) {

                if (currentExercises.get(i).id == exerciseId) {
                    currentIndex = i;
                    break;
                }
            }

            // Se não encontrarmos o exercício, não fazemos nada
            if (currentIndex == -1) {
                return;
            }

            // Calcular a posição de destino
            int targetIndex = currentIndex + direction;

            // Impedir que o exercício ultrapasse os limites da lista
            if (targetIndex < 0
                    || targetIndex >= currentExercises.size()) {
                return;
            }

            // Obter os dois exercícios que vão trocar de posição
            PlanExerciseEntity current =
                    currentExercises.get(currentIndex);

            PlanExerciseEntity target =
                    currentExercises.get(targetIndex);

            // Trocar as posições
            int oldPosition = current.position;

            current.position = target.position;
            target.position = oldPosition;

            // Guardar as novas posições na base de dados
            exerciseDao.updatePosition(
                    current.id,
                    current.position
            );

            exerciseDao.updatePosition(
                    target.id,
                    target.position
            );
        });
    }

    // =====================================================
    // BIBLIOTECA DE EXERCÍCIOS
    // =====================================================

    public LiveData<List<ExerciseLibraryEntity>> observeLibrary() {
        return exerciseLibraryDao.getAll();
    }

    public LiveData<List<ExerciseLibraryEntity>> searchLibrary(
            String query
    ) {
        return exerciseLibraryDao.search(query);
    }

    public LiveData<List<ExerciseLibraryEntity>> observeExercisesByMuscle(
            String muscleGroup
    ) {
        return exerciseLibraryDao.getByMuscleGroup(muscleGroup);
    }

    public LiveData<List<ExerciseLibraryEntity>> observeFavoriteExercises() {
        return exerciseLibraryDao.getFavorites();
    }

    public LiveData<List<ExerciseLibraryEntity>> observeCustomExercises() {
        return exerciseLibraryDao.getCustomExercises();
    }

    public void insertLibraryExercise(ExerciseLibraryEntity exercise) {
        executor.execute(() ->
                exerciseLibraryDao.insert(exercise)
        );
    }

    public void updateLibraryExercise(ExerciseLibraryEntity exercise) {
        executor.execute(() ->
                exerciseLibraryDao.update(exercise)
        );
    }

    public void deleteLibraryExercise(ExerciseLibraryEntity exercise) {
        executor.execute(() ->
                exerciseLibraryDao.delete(exercise)
        );
    }

    public void setExerciseFavorite(
            long exerciseId,
            boolean favorite
    ) {
        executor.execute(() ->
                exerciseLibraryDao.setFavorite(exerciseId, favorite)
        );
    }

    // =====================================================
    // SESSÕES
    // =====================================================

    public LiveData<List<WorkoutSessionEntity>> observeSessions() {
        return sessionDao.observeAll();
    }

    public LiveData<WorkoutSessionEntity> observeSession(long sessionId) {
        return sessionDao.observeById(sessionId);
    }

    public LiveData<List<SessionExerciseEntity>> observeSessionExercises(
            long sessionId
    ) {
        return sessionExerciseDao.observeBySession(sessionId);
    }

    public LiveData<List<PerformedSetEntity>> observePerformedSets(
            long exerciseId
    ) {
        return performedSetDao.observeByExercise(exerciseId);
    }

    public void insertSession(
            WorkoutSessionEntity session,
            Consumer<Long> onInserted
    ) {
        executor.execute(() -> {

            long id = sessionDao.insert(session);

            if (onInserted != null) {
                onInserted.accept(id);
            }
        });
    }

    public void insertSessionExercise(
            SessionExerciseEntity exercise,
            Consumer<Long> onInserted
    ) {
        executor.execute(() -> {

            long id = sessionExerciseDao.insert(exercise);

            if (onInserted != null) {
                onInserted.accept(id);
            }
        });
    }

    public void insertPerformedSet(PerformedSetEntity set) {
        executor.execute(() ->
                performedSetDao.insert(set)
        );
    }

    public void updatePerformedSet(PerformedSetEntity set) {
        executor.execute(() ->
                performedSetDao.update(set)
        );
    }

    public void finishSession(long sessionId) {
        executor.execute(() -> {

            WorkoutSessionEntity session =
                    sessionDao.getById(sessionId);

            if (session != null) {
                session.finishedAt = System.currentTimeMillis();
                sessionDao.update(session);
            }
        });
    }

    // =====================================================
// CRIAÇÃO DE PLANOS E DIAS
// =====================================================

    public void createPlanWithDays(
            String planName,
            List<String> dayNames,
            List<Boolean> restDays,
            Consumer<Long> onCreated
    ) {
        executor.execute(() -> {

            final long[] createdPlanId = {-1};

            database.runInTransaction(() -> {

                PlanEntity plan = new PlanEntity(
                        planName,
                        false,
                        PlanEntity.STATUS_DRAFT,
                        false
                );

                long planId = planDao.insert(plan);
                createdPlanId[0] = planId;

                for (int i = 0; i < 7; i++) {

                    PlanDayEntity day = new PlanDayEntity(
                            planId,
                            i + 1,
                            dayNames.get(i),
                            restDays.get(i)
                    );

                    planDayDao.insert(day);
                }
            });

            if (onCreated != null) {
                onCreated.accept(createdPlanId[0]);
            }
        });
    }

    public void getLibraryExercises(
            Consumer<List<ExerciseLibraryEntity>> onLoaded
    ) {
        executor.execute(() -> {

            List<ExerciseLibraryEntity> exercises =
                    exerciseLibraryDao.getAllSync();

            if (onLoaded != null) {
                onLoaded.accept(exercises);
            }
        });
    }
    public void deactivatePlan(long planId) {
        executor.execute(() -> {
            planDao.clearActive();
        });
    }

}
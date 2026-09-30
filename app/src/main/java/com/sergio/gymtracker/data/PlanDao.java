package com.sergio.gymtracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlanDao {

    @Query("SELECT * FROM plans ORDER BY name ASC")
    LiveData<List<PlanEntity>> observeAll();

    @Query("SELECT * FROM plans WHERE id = :id LIMIT 1")
    PlanEntity getById(long id);

    @Query("SELECT * FROM plans WHERE active = 1 LIMIT 1")
    PlanEntity getActive();

    @Insert
    long insert(PlanEntity plan);

    @Update
    void update(PlanEntity plan);

    @Delete
    void delete(PlanEntity plan);

    // Desativa o plano atual e altera o seu estado para READY
    @Query("UPDATE plans SET active = 0, status = CASE WHEN status = 'ACTIVE' THEN 'READY' ELSE status END")
    void clearActive();

    // Ativa um plano
    @Query("UPDATE plans SET active = 1, status = 'ACTIVE' WHERE id = :planId")
    void setActive(long planId);

    // Obtém apenas os rascunhos
    @Query("SELECT * FROM plans WHERE status = 'DRAFT' ORDER BY name ASC")
    LiveData<List<PlanEntity>> observeDrafts();

    // Obtém os planos prontos que não estão ativos
    @Query("SELECT * FROM plans WHERE status = 'READY' ORDER BY name ASC")
    LiveData<List<PlanEntity>> observeReady();

    // Obtém os modelos guardados
    @Query("SELECT * FROM plans WHERE savedAsTemplate = 1 ORDER BY name ASC")
    LiveData<List<PlanEntity>> observeTemplates();

    // Atualiza o estado de um plano
    @Query("UPDATE plans SET status = :status WHERE id = :planId")
    void updateStatus(long planId, String status);

    // Guarda ou remove um plano dos modelos
    @Query("UPDATE plans SET savedAsTemplate = :saved WHERE id = :planId")
    void setSavedAsTemplate(long planId, boolean saved);
}
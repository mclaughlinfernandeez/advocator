package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- Screener Results ---
    @Query("SELECT * FROM screener_results ORDER BY timestamp DESC")
    fun getAllScreenerResults(): Flow<List<ScreenerResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreenerResult(result: ScreenerResult): Long

    @Query("DELETE FROM screener_results WHERE id = :id")
    suspend fun deleteScreenerResultById(id: Int)


    // --- 504 Plans ---
    @Query("SELECT * FROM plans_504 ORDER BY timestamp DESC")
    fun getAllPlans504(): Flow<List<Plan504>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan504(plan: Plan504): Long

    @Query("DELETE FROM plans_504 WHERE id = :id")
    suspend fun deletePlan504ById(id: Int)


    // --- Genetic Variants ---
    @Query("SELECT * FROM genetic_variants ORDER BY timestamp DESC")
    fun getAllGeneticVariants(): Flow<List<GeneticVariant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneticVariant(variant: GeneticVariant): Long

    @Query("DELETE FROM genetic_variants WHERE id = :id")
    suspend fun deleteGeneticVariantById(id: Int)


    // --- Legal Cases ---
    @Query("SELECT * FROM legal_cases ORDER BY timestamp DESC")
    fun getAllLegalCases(): Flow<List<LegalCase>>

    @Query("SELECT * FROM legal_cases WHERE id = :id")
    suspend fun getLegalCaseById(id: Int): LegalCase?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLegalCase(legalCase: LegalCase): Long

    @Query("DELETE FROM legal_cases WHERE id = :id")
    suspend fun deleteLegalCaseById(id: Int)
}

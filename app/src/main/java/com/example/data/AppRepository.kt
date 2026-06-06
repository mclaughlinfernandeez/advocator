package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val dao: AppDao) {
    // --- Screeners ---
    val allScreenerResults: Flow<List<ScreenerResult>> = dao.getAllScreenerResults()

    suspend fun insertScreenerResult(result: ScreenerResult): Long {
        return dao.insertScreenerResult(result)
    }

    suspend fun deleteScreenerResult(id: Int) {
        dao.deleteScreenerResultById(id)
    }


    // --- 504 Plans ---
    val allPlans504: Flow<List<Plan504>> = dao.getAllPlans504()

    suspend fun insertPlan504(plan: Plan504): Long {
        return dao.insertPlan504(plan)
    }

    suspend fun deletePlan504(id: Int) {
        dao.deletePlan504ById(id)
    }


    // --- Genetic Variants ---
    val allGeneticVariants: Flow<List<GeneticVariant>> = dao.getAllGeneticVariants()

    suspend fun insertGeneticVariant(variant: GeneticVariant): Long {
        return dao.insertGeneticVariant(variant)
    }

    suspend fun deleteGeneticVariant(id: Int) {
        dao.deleteGeneticVariantById(id)
    }


    // --- Legal Cases ---
    val allLegalCases: Flow<List<LegalCase>> = dao.getAllLegalCases()

    suspend fun getLegalCaseById(id: Int): LegalCase? {
        return dao.getLegalCaseById(id)
    }

    suspend fun insertLegalCase(legalCase: LegalCase): Long {
        return dao.insertLegalCase(legalCase)
    }

    suspend fun deleteLegalCase(id: Int) {
        dao.deleteLegalCaseById(id)
    }
}

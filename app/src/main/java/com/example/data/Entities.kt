package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screener_results")
data class ScreenerResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val screenerType: String, // "ASRS", "CADI", "HIV_ADHD"
    val answersJson: String, // Serialized map of answers
    val score: Int,
    val severity: String, // "Likely ADHD", "Moderate", "High Risk", etc.
    val recommendationsJson: String, // Serialized list of strings
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "plans_504")
data class Plan504(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentName: String,
    val grade: String,
    val disabilitySummary: String,
    val limitationsJson: String, // Serialized JSON list of functional limitations
    val accommodationsJson: String, // Serialized JSON list of accommodations
    val reviewDate: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "genetic_variants")
data class GeneticVariant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rsid: String,
    val gene: String, // e.g., "COMT", "SLC6A2", "BDNF", "DAT1", "DRD4"
    val genotype: String, // e.g., "Val/Val", "Val/Met", "Met/Met"
    val riskAllele: String,
    val pValue: Double?,
    val oddsRatio: Double?,
    val pubmedCount: Int,
    val riskScore: Double,
    val impactDescription: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "legal_cases")
data class LegalCase(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val claimantName: String,
    val caseType: String, // "SSI_SSDI_APPEAL", "SEC_1983_CIVIL_RIGHTS", "ADA_TITLE_II_ACCOMMODATION"
    val factualBackground: String,
    val linkedScreenerId: Int?,
    val linkedPlanId: Int?,
    val linkedGeneticVariantIds: String?, // Comma separated IDs: "1,2,5"
    val generatedBriefText: String,
    val timestamp: Long = System.currentTimeMillis()
)

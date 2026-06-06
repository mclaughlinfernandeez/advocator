package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.network.GeneticRiskEngine
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GeneticLabScreen(viewModel: MainViewModel) {
    val customVariantInput by viewModel.customVariantInput.collectAsState()
    val activeGenotypeInput by viewModel.activeGenotypeInput.collectAsState()
    val progress by viewModel.analysisProgress.collectAsState()
    val status by viewModel.analysisStatus.collectAsState()
    val savedVariants by viewModel.savedGeneticVariants.collectAsState()

    var showImportDropdown by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- GENOMIC LAB HEADER ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Genomic & Neurobiological Evidence Lab",
                        color = AmberPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Under the ADAAA, neurological status is listed as a major bodily function. Upload rsIDs or import preset files to index dopamine metabolization (COMT), epinephrine transport (SLC6A2), or neuroplasticity (BDNF) alleles with direct REST cross-references to the EBI GWAS Catalog & PubMed.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // --- PROGRESS OVERLAY ---
        if (progress != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                    modifier = Modifier.fillMaxWidth().border(1.dp, AmberPrimary, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AmberPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(status, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Connecting dynamic scientific data layers...", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        // --- EVIDENCE INPUT SECTION ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "MANUALLY DISCOVER OR CHOOSE VARIANTS",
                        color = CyberViolet,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customVariantInput,
                            onValueChange = { viewModel.customVariantInput.value = it },
                            label = { Text("Variant rsID (e.g. rs4680)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmberPrimary,
                                unfocusedBorderColor = DarkBorder,
                                focusedLabelColor = AmberPrimary
                            ),
                            modifier = Modifier.weight(1f).testTag("rsid_input_field")
                        )

                        Box {
                            Button(
                                onClick = { showImportDropdown = true },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface2, contentColor = AmberPrimary),
                                border = BorderStroke(1.dp, DarkBorder),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(56.dp).testTag("select_preset_dropdown")
                            ) {
                                Icon(Icons.Default.Dns, contentDescription = "Presets")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Presets", fontSize = 12.sp)
                            }

                            DropdownMenu(
                                expanded = showImportDropdown,
                                onDismissRequest = { showImportDropdown = false },
                                modifier = Modifier.background(DarkSurface2).border(1.dp, DarkBorder)
                            ) {
                                GeneticRiskEngine.PRELOAD_GENETIC_DB.forEach { (rsid, fact) ->
                                    DropdownMenuItem(
                                        text = { Text("${fact.gene} (${rsid}) - ${fact.genotypeOption}", color = TextPrimary) },
                                        onClick = {
                                            viewModel.customVariantInput.value = rsid
                                            viewModel.activeGenotypeInput.value = fact.genotypeOption
                                            showImportDropdown = false
                                        },
                                        modifier = Modifier.testTag("preset_$rsid")
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Observed Subject Genotype:",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Val/Val", "Val/Met", "Met/Met", "9R", "7R").forEach { option ->
                            val isSelected = activeGenotypeInput == option
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) AmberPrimary else DarkSurface2)
                                    .border(1.dp, if (isSelected) AmberPrimary else DarkBorder, RoundedCornerShape(6.dp))
                                    .clickable { viewModel.activeGenotypeInput.value = option }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = option,
                                    color = if (isSelected) DarkBackground else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.runGeneticQuantification(customVariantInput, activeGenotypeInput)
                        },
                        enabled = customVariantInput.trim().isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberPrimary,
                            contentColor = DarkBackground,
                            disabledContainerColor = DarkSurface2,
                            disabledContentColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("quantification_button")
                    ) {
                        Icon(Icons.Default.QueryStats, contentDescription = "Quantify")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Connect and Quantify Risk", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- QUANTIFIED GENOMIC VARIANTS LIST ---
        item {
            Text(
                text = "Quantified Neurobiological Factors (${savedVariants.size})",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
        }

        if (savedVariants.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No genetic variant evidence quantified. Paste an rsID or use one of the presets above.", color = TextSecondary, fontSize = 13.sp, textAlign = TextAlign.Center)
                }
            }
        } else {
            items(savedVariants) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .background(AmberPrimary, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(item.rsid, color = DarkBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gene: ${item.gene}", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("(${item.genotype})", color = CyberViolet, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }

                            IconButton(onClick = { viewModel.deleteGeneticVariant(item.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ColorDanger)
                            }
                        }

                        Divider(color = DarkBorder, modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("GWAS p-Value", color = TextSecondary, fontSize = 10.sp)
                                Text(item.pValue?.let { String.format("%.2e", it) } ?: "—", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Column {
                                Text("Odds Ratio / Scale", color = TextSecondary, fontSize = 10.sp)
                                Text(item.oddsRatio?.let { String.format("%.2f", it) } ?: "—", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Column {
                                Text("PubMed Hits", color = TextSecondary, fontSize = 10.sp)
                                Text(item.pubmedCount.toString(), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Column {
                                Text("Risk Index", color = TextSecondary, fontSize = 10.sp)
                                Text(String.format("%.1f", item.riskScore), color = ColorWarning, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("BIOLOGICAL RELEVANCE:", color = AmberPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text(item.impactDescription, color = TextPrimary, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
        }
    }
}

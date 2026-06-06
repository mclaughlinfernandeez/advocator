package com.example.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ScreenerResult
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun ScreenerScreen(viewModel: MainViewModel) {
    val screenerType by viewModel.currentScreenerType.collectAsState()
    val questions by viewModel.activeScreenerQuestions.collectAsState()
    val answers by viewModel.screenerAnswers.collectAsState()
    val currentIndex by viewModel.activeScreenerIndex.collectAsState()
    val calculatedReport by viewModel.calculatedScreenerReport.collectAsState()
    val savedScreeners by viewModel.savedScreeners.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- SCREENER SELECTION HEADER ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Clinical Screeners & Protocols",
                        color = AmberPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Select a validated disability screener. Results can be linked as legal-medical evidence to bolster SSI/SSDI or Section 504 claims.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("ASRS", "CADI", "HIV_ADHD").forEach { type ->
                            val isSelected = screenerType == type
                            Button(
                                onClick = { viewModel.changeScreenerType(type) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) AmberPrimary else DarkSurface2,
                                    contentColor = if (isSelected) DarkBackground else TextPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("screener_tab_$type")
                            ) {
                                Text(
                                    text = when(type) {
                                        "ASRS" -> "ASRS (6Q)"
                                        "CADI" -> "CADI (9Q)"
                                        "HIV_ADHD" -> "HIV+ Aging"
                                        else -> type
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- ACTIVE SCREENER QUESTION CAROUSEL ---
        if (calculatedReport == null) {
            item {
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
                            Text(
                                text = "Question ${currentIndex + 1} of ${questions.size}",
                                color = CyberViolet,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "${((currentIndex.toFloat() / questions.size) * 100).toInt()}% Complete",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        // Progress Indicator
                        LinearProgressIndicator(
                            progress = { (currentIndex + 1).toFloat() / questions.size },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = AmberPrimary,
                            trackColor = DarkSurface2
                        )

                        Text(
                            text = questions[currentIndex].text,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Score options
                        val options = listOf(
                            "Never" to 0,
                            "Rarely" to 1,
                            "Sometimes" to 2,
                            "Often" to 3,
                            "Very Often" to 4
                        )

                        options.forEach { (label, value) ->
                            val currentScore = answers[currentIndex]
                            val isSelected = currentScore == value
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) DarkBorder else DarkSurface2)
                                    .border(1.dp, if (isSelected) AmberPrimary else DarkBorder, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.submitAnswer(currentIndex, value) }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = label, color = TextPrimary, fontSize = 14.sp)
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.submitAnswer(currentIndex, value) },
                                    colors = RadioButtonDefaults.colors(selectedColor = AmberPrimary, unselectedColor = TextSecondary),
                                    modifier = Modifier.testTag("radio_${label.lowercase()}")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { if (currentIndex > 0) viewModel.activeScreenerIndex.value = currentIndex - 1 },
                                enabled = currentIndex > 0,
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface2),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Previous", tint = TextPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Prev", color = TextPrimary, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            if (currentIndex < questions.size - 1) {
                                Button(
                                    onClick = {
                                        if (answers[currentIndex] != null) {
                                            viewModel.activeScreenerIndex.value = currentIndex + 1
                                        }
                                    },
                                    enabled = answers[currentIndex] != null,
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary, contentColor = DarkBackground),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).testTag("next_button")
                                ) {
                                    Text("Next", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.calculateScreener() },
                                    enabled = answers.size == questions.size,
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorSuccess, contentColor = DarkBackground),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).testTag("generate_report_button")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Calculate")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Evaluate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // --- GENERATED SCREENER REPORT VIEW ---
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    modifier = Modifier.fillMaxWidth().border(2.dp, ColorSuccess, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MedicalServices, contentDescription = "Report", tint = ColorSuccess, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Clinical Clinical Assessment Report", color = ColorSuccess, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Divider(color = DarkBorder, modifier = Modifier.padding(vertical = 12.dp))

                        Text("Screener Name: ${calculatedReport!!.screenerType}", fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                        Text("Overall Score: ${calculatedReport!!.score} Points", fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                        Text("Symptom Indication: ${calculatedReport!!.severity}", fontWeight = FontWeight.Bold, color = AmberPrimary, fontSize = 15.sp)

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("PHYSICIAN ACTION ACTION PLAN:", fontWeight = FontWeight.Bold, color = CyberViolet, fontSize = 12.sp, fontFamily = FontFamily.Monospace)

                        val recsList = try {
                            val arr = JSONArray(calculatedReport!!.recommendationsJson)
                            List(arr.length()) { arr.getString(it) }
                        } catch (e: Exception) {
                            emptyList()
                        }

                        recsList.forEach { rec ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("• ", color = AmberPrimary, fontWeight = FontWeight.Bold)
                                Text(rec, color = TextPrimary, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.changeScreenerType(screenerType) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorDanger),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Discard")
                            }

                            Button(
                                onClick = { viewModel.saveScreenerReport() },
                                colors = ButtonDefaults.buttonColors(containerColor = ColorSuccess, contentColor = DarkBackground),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("save_report_button")
                            ) {
                                Text("Save Clinical Record", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // --- HISTORICAL SAVED LABS LISTING ---
        item {
            Text(
                text = "Clinical Case History (${savedScreeners.size})",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
        }

        if (savedScreeners.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No saved clinical screeners.", color = TextSecondary, fontSize = 13.sp)
                }
            }
        } else {
            items(savedScreeners) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                    modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .background(AmberPrimary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(item.screenerType, color = AmberPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Score: ${item.score} pts", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Indication: ${item.severity}", color = TextPrimary, fontSize = 13.sp)
                        }

                        IconButton(
                            onClick = { viewModel.deleteScreener(item.id) },
                            modifier = Modifier.testTag("delete_screener_${item.id}")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ColorDanger)
                        }
                    }
                }
            }
        }
    }
}

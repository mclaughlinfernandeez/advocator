package com.example.ui

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import org.json.JSONArray

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Plan504Screen(viewModel: MainViewModel) {
    val studentName by viewModel.studentName.collectAsState()
    val gradeLevel by viewModel.gradeLevel.collectAsState()
    val disabilitySummary by viewModel.disabilitySummary.collectAsState()
    val selectedLimitations by viewModel.selectedLimitations.collectAsState()
    val selectedAccommodations by viewModel.selectedAccommodations.collectAsState()
    val reviewDate by viewModel.reviewDate.collectAsState()
    val savedPlans by viewModel.savedPlans504.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- SECTION 504 HEADER ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Section 504 Plan Builder",
                        color = AmberPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Automate personalized classroom guidelines by mapping specific functional deficits to validated academic accommodations.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // --- EXPLICIT BUILDER FORM ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "STUDENT & DISABILITY METADATA",
                        color = CyberViolet,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { viewModel.studentName.value = it },
                        label = { Text("Student Full Name") },
                        modifier = Modifier.fillMaxWidth().testTag("student_name_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedLabelColor = AmberPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = gradeLevel,
                        onValueChange = { viewModel.gradeLevel.value = it },
                        label = { Text("Grade Level / Academic Year") },
                        modifier = Modifier.fillMaxWidth().testTag("student_grade_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedLabelColor = AmberPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = disabilitySummary,
                        onValueChange = { viewModel.disabilitySummary.value = it },
                        label = { Text("Disability Profile / Pathology") },
                        modifier = Modifier.fillMaxWidth().testTag("student_disability_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedLabelColor = AmberPrimary
                        )
                    )
                }
            }
        }

        // --- LIMITATION SELECTOR ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "IDENTIFY FUNCTIONAL LIMITATIONS",
                        color = CyberViolet,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Select all chronic deficits impacting educational outcomes:",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    viewModel.limitationOptions.forEach { lim ->
                        val isChecked = selectedLimitations.contains(lim)
                        val cleanTitle = lim.substringBefore(":")
                        val cleanDesc = lim.substringAfter(":")

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isChecked) DarkBorder else DarkSurface2)
                                .border(1.dp, if (isChecked) AmberPrimary else DarkBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleLimitation(lim) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { viewModel.toggleLimitation(lim) },
                                colors = CheckboxDefaults.colors(checkedColor = AmberPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(cleanTitle, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(cleanDesc, color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- ACCOMMODATIONS CORRESPONDENCE ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CORRESPONDING ACCOMMODATION ADAPTATIONS",
                        color = CyberViolet,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Match accommodations designed to mitigate identified deficits:",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    viewModel.accommodationOptions.forEach { acc ->
                        val isChecked = selectedAccommodations.contains(acc)
                        val prefix = acc.substringBefore(":")
                        val value = acc.substringAfter(":")

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isChecked) DarkBorder else DarkSurface2)
                                .border(1.dp, if (isChecked) AmberPrimary else DarkBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleAccommodation(acc) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { viewModel.toggleAccommodation(acc) },
                                colors = CheckboxDefaults.colors(checkedColor = AmberPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(prefix, color = AmberPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(value, color = TextPrimary, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- SAVE CONTROLS ---
        item {
            Button(
                onClick = { viewModel.save504Plan() },
                enabled = studentName.isNotEmpty() && selectedLimitations.isNotEmpty() && selectedAccommodations.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorSuccess,
                    contentColor = DarkBackground,
                    disabledContainerColor = DarkSurface2,
                    disabledContentColor = TextSecondary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_504_plan_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save Plan")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate & Save 504 Plan", fontWeight = FontWeight.Bold)
            }
        }

        // --- LIST OF EXISTING GENERATED PLANS ---
        item {
            Text(
                text = "Saved Section 504 Plans / Adaptations (${savedPlans.size})",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
        }

        if (savedPlans.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No saved plans found.", color = TextSecondary, fontSize = 13.sp)
                }
            }
        } else {
            items(savedPlans) { plan ->
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
                            Column {
                                Text(plan.studentName, color = AmberPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text("Grade: ${plan.grade}", color = TextSecondary, fontSize = 12.sp)
                            }
                            IconButton(onClick = { viewModel.deletePlan504(plan.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ColorDanger)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Disability summary: ${plan.disabilitySummary}", color = TextPrimary, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("IDENTIFIED FUNCTIONAL LIMITATIONS:", color = CyberViolet, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        val lims = try {
                            val arr = JSONArray(plan.limitationsJson)
                            List(arr.length()) { arr.getString(it) }
                        } catch (e: Exception) {
                            emptyList()
                        }
                        lims.forEach { lim ->
                            Text("• ${lim.substringBefore(":")}", color = TextPrimary, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp, top = 2.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("DESIGNATED ENVIRONMENTAL ACCOMMODATIONS:", color = ColorSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        val accs = try {
                            val arr = JSONArray(plan.accommodationsJson)
                            List(arr.length()) { arr.getString(it) }
                        } catch (e: Exception) {
                            emptyList()
                        }
                        accs.forEach { acc ->
                            Text("✔ ${acc.substringBefore(":")}: ${acc.substringAfter(":")}", color = TextPrimary, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp, top = 2.dp))
                        }
                    }
                }
            }
        }
    }
}

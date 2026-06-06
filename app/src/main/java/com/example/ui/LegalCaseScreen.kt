package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LegalCaseScreen(viewModel: MainViewModel) {
    var selectedSubTab by remember { mutableStateOf(0) } // 0: Create Brief, 1: Brief Library, 2: Legal AI Chat

    Column(modifier = Modifier.fillMaxSize()) {
        // Sub-Navigation Tabs
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = DarkSurface,
            contentColor = AmberPrimary
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = { Text("Case Brief Builder", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.Balance, contentDescription = "Builder") },
                modifier = Modifier.testTag("subtab_builder")
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = { Text("Brief Library", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.Folder, contentDescription = "Library") },
                modifier = Modifier.testTag("subtab_library")
            )
            Tab(
                selected = selectedSubTab == 2,
                onClick = { selectedSubTab = 2 },
                text = { Text("Government LLM Partner", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.Chat, contentDescription = "Assistant") },
                modifier = Modifier.testTag("subtab_assistant")
            )
        }

        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            when (selectedSubTab) {
                0 -> CaseBuilderView(viewModel, onBriefCompleted = { selectedSubTab = 1 })
                1 -> SavedBriefsView(viewModel)
                2 -> LegalChatView(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CaseBuilderView(viewModel: MainViewModel, onBriefCompleted: () -> Unit) {
    val claimantName by viewModel.claimantName.collectAsState()
    val selectedCaseType by viewModel.selectedCaseType.collectAsState()
    val factualBackground by viewModel.caseFactualBackground.collectAsState()
    val linkedScreenerId by viewModel.linkedScreenerId.collectAsState()
    val linkedPlanId by viewModel.linkedPlanId.collectAsState()
    val selectedGeneticIds by viewModel.selectedGeneticIds.collectAsState()

    val savedScreeners by viewModel.savedScreeners.collectAsState()
    val savedPlans by viewModel.savedPlans504.collectAsState()
    val savedGenetics by viewModel.savedGeneticVariants.collectAsState()

    val briefStatus by viewModel.aiBriefStatus.collectAsState()
    val context = LocalContext.current

    var screenerExpanded by remember { mutableStateOf(false) }
    var planExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- CASE DETAILS ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "NEW COMPREHENSIVE LEGAL FILING",
                        color = CyberViolet,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = claimantName,
                        onValueChange = { viewModel.claimantName.value = it },
                        label = { Text("Claimant Pro Se Name") },
                        modifier = Modifier.fillMaxWidth().testTag("claimant_name_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedLabelColor = AmberPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Select Case Type / Cause of Action:", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "SSI_SSDI_APPEAL" to "SSI Appeal",
                            "SEC_1983_CIVIL_RIGHTS" to "§ 1983 Claim",
                            "ADA_TITLE_II_ACCOMMODATION" to "ADA Accom."
                        ).forEach { (type, label) ->
                            val isSelected = selectedCaseType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AmberPrimary else DarkSurface2)
                                    .border(1.dp, if (isSelected) AmberPrimary else DarkBorder, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.selectedCaseType.value = type }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, color = if (isSelected) DarkBackground else TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = factualBackground,
                        onValueChange = { viewModel.caseFactualBackground.value = it },
                        label = { Text("Describe Factual Background / Infringements") },
                        modifier = Modifier.fillMaxWidth().height(120.dp).testTag("factual_background_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedLabelColor = AmberPrimary
                        )
                    )
                }
            }
        }

        // --- LINK CLINICAL SCREENER ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("LINK VALIDATED CLINICAL SCREENING", color = CyberViolet, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Embed clinical reports directly into the Statement of Facts.", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Box {
                        OutlinedButton(
                            onClick = { screenerExpanded = true },
                            modifier = Modifier.fillMaxWidth().testTag("link_screener_dropdown"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberPrimary)
                        ) {
                            val selectedText = if (linkedScreenerId == null) {
                                "Select Stored Clinical Screen"
                            } else {
                                val item = savedScreeners.find { it.id == linkedScreenerId }
                                "${item?.screenerType} (${item?.severity}) - Score: ${item?.score} pts"
                            }
                            Text(selectedText)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                        }

                        DropdownMenu(
                            expanded = screenerExpanded,
                            onDismissRequest = { screenerExpanded = false },
                            modifier = Modifier.fillMaxWidth().background(DarkSurface2).border(1.dp, DarkBorder)
                        ) {
                            DropdownMenuItem(
                                text = { Text("(Clear selection)", color = ColorDanger) },
                                onClick = {
                                    viewModel.linkedScreenerId.value = null
                                    screenerExpanded = false
                                }
                            )
                            savedScreeners.forEach { sc ->
                                DropdownMenuItem(
                                    text = { Text("${sc.screenerType} (${sc.severity}) - Score: ${sc.score}", color = TextPrimary) },
                                    onClick = {
                                        viewModel.linkedScreenerId.value = sc.id
                                        screenerExpanded = false
                                    },
                                    modifier = Modifier.testTag("screener_item_${sc.id}")
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- LINK 504 ACCOMMODATIONS PLAN ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("LINK STUDENT / INDIVIDUAL 504 PLAN", color = CyberViolet, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Substantiate prior failure to accommodate allegations.", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Box {
                        OutlinedButton(
                            onClick = { planExpanded = true },
                            modifier = Modifier.fillMaxWidth().testTag("link_504_dropdown"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberPrimary)
                        ) {
                            val selectedText = if (linkedPlanId == null) {
                                "Select Stored 504 Adaptations Profile"
                            } else {
                                val plan = savedPlans.find { it.id == linkedPlanId }
                                "${plan?.studentName} - Grade: ${plan?.grade}"
                            }
                            Text(selectedText)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                        }

                        DropdownMenu(
                            expanded = planExpanded,
                            onDismissRequest = { planExpanded = false },
                            modifier = Modifier.fillMaxWidth().background(DarkSurface2).border(1.dp, DarkBorder)
                        ) {
                            DropdownMenuItem(
                                text = { Text("(Clear selection)", color = ColorDanger) },
                                onClick = {
                                    viewModel.linkedPlanId.value = null
                                    planExpanded = false
                                }
                            )
                            savedPlans.forEach { plan ->
                                DropdownMenuItem(
                                    text = { Text("${plan.studentName} (Grade ${plan.grade})", color = TextPrimary) },
                                    onClick = {
                                        viewModel.linkedPlanId.value = plan.id
                                        planExpanded = false
                                    },
                                    modifier = Modifier.testTag("plan_item_${plan.id}")
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- LINK GENETICS VARIANTS (MULTI-SELECT) ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("LINK NEUROBIOLOGICAL / GENETIC CORROBORATION", color = CyberViolet, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Select biological alleles demonstrating executive physical impairment. Disregards mitigating arguments.", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (savedGenetics.isEmpty()) {
                        Text("No saved variants in laboratory. Go to Genomic Lab to index alleles.", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
                    } else {
                        savedGenetics.forEach { gene ->
                            val isChecked = selectedGeneticIds.contains(gene.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isChecked) DarkBorder else DarkSurface2)
                                    .clickable {
                                        val currentSet = selectedGeneticIds.toMutableSet()
                                        if (currentSet.contains(gene.id)) currentSet.remove(gene.id) else currentSet.add(gene.id)
                                        viewModel.selectedGeneticIds.value = currentSet
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = {
                                        val currentSet = selectedGeneticIds.toMutableSet()
                                        if (currentSet.contains(gene.id)) currentSet.remove(gene.id) else currentSet.add(gene.id)
                                        viewModel.selectedGeneticIds.value = currentSet
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = AmberPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${gene.gene} (${gene.rsid}) - Genotype: ${gene.genotype} (Scored: ${gene.riskScore})", color = TextPrimary, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- SUBMIT PROGRESS ---
        if (briefStatus.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                    modifier = Modifier.fillMaxWidth().border(1.dp, AmberPrimary, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AmberPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(briefStatus, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- SYNTHESIZE BRIEF BUTTON ---
        item {
            Button(
                onClick = {
                    viewModel.generateLegalBrief {
                        Toast.makeText(context, "Legal brief successfully optimized!", Toast.LENGTH_LONG).show()
                        onBriefCompleted()
                    }
                },
                enabled = claimantName.trim().isNotEmpty() && factualBackground.trim().isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = ColorSuccess, contentColor = DarkBackground),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("generate_brief_button")
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "AI Generate")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Synthesize & Draft Legal Brief", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SavedBriefsView(viewModel: MainViewModel) {
    val savedCases by viewModel.savedLegalCases.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Drafted Briefs & Complaints (${savedCases.size})",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        if (savedCases.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No legal briefs drafted yet. Fill down details on Case Brief Builder and click generate.", color = TextSecondary, fontSize = 13.sp)
                }
            }
        } else {
            items(savedCases) { item ->
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
                                Box(
                                    modifier = Modifier
                                        .background(AmberPrimary, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = when(item.caseType) {
                                            "SSI_SSDI_APPEAL" -> "SSI APPEAL"
                                            "SEC_1983_CIVIL_RIGHTS" -> "COMPLAINT § 1983"
                                            else -> "ADA PETITION"
                                        },
                                        color = DarkBackground,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Claimant: ${item.claimantName}", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Legal Brief", item.generatedBriefText)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Brief copied successfully!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy text", tint = AmberPrimary)
                                }

                                IconButton(onClick = { viewModel.deleteLegalCase(item.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Case", tint = ColorDanger)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp)
                                .background(DarkSurface2, RoundedCornerShape(8.dp))
                                .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                item {
                                    Text(
                                        text = item.generatedBriefText,
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegalChatView(viewModel: MainViewModel) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    var chatMessageText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Chat list area
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chatHistory) { msg ->
                val isAssistant = msg.role == "assistant"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isAssistant) Arrangement.Start else Arrangement.End
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAssistant) DarkSurface else AmberPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .border(1.dp, if (isAssistant) DarkBorder else AmberAccent, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isAssistant) Icons.Default.SmartToy else Icons.Default.Person,
                                    contentDescription = msg.role,
                                    tint = if (isAssistant) AmberPrimary else DarkBackground,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isAssistant) "Civil Rights Assistant" else "Claimant (Pro Se)",
                                    color = if (isAssistant) AmberPrimary else DarkBackground,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = msg.message,
                                color = if (isAssistant) TextPrimary else DarkBackground,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            if (isChatLoading) {
                item {
                    Row(modifier = Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.Start) {
                        Card(colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(color = AmberPrimary, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Analyzing federal statutes...", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Input send box
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = chatMessageText,
                onValueChange = { chatMessageText = it },
                placeholder = { Text("Ask about GINA, Section 504, Fifth Amendment takings...", fontSize = 13.sp) },
                modifier = Modifier.weight(1f).testTag("chat_input_text"),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberPrimary,
                    unfocusedBorderColor = DarkBorder
                )
            )

            Button(
                onClick = {
                    viewModel.sendChatMessage(chatMessageText)
                    chatMessageText = ""
                },
                enabled = chatMessageText.trim().isNotEmpty() && !isChatLoading,
                colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary, contentColor = DarkBackground),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(56.dp).testTag("chat_send_button")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send Message")
            }
        }
    }
}

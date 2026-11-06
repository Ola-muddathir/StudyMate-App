package com.example.studymate.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskForm(
    courses: List<Pair<Int, String>>,
    initialTitle: String = "",
    initialDescription: String = "",
    initialCourseId: Int? = null,
    initialDueDate: Long = System.currentTimeMillis(),
    initialPriority: String = "MEDIUM",
    onSave: (String, String, Int, Long, String) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }
    var selectedCourseId by remember { mutableStateOf(initialCourseId) }
    var dueDate by remember { mutableLongStateOf(initialDueDate) }
    var priority by remember { mutableStateOf(initialPriority) }
    var errorMessage by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dueDate
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (initialTitle.isBlank()) "New Task" else "Edit Task",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it; errorMessage = "" },
            label = { Text("Task Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Text("Course", style = MaterialTheme.typography.titleMedium)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = courses.firstOrNull { it.first == selectedCourseId }?.second ?: "Select a course",
                onValueChange = {},
                readOnly = true,
                label = { Text("Course") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                courses.forEach { course ->
                    DropdownMenuItem(
                        text = { Text(course.second) },
                        onClick = {
                            selectedCourseId = course.first
                            expanded = false
                        }
                    )
                }
            }
        }

        Text("Priority", style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("LOW", "MEDIUM", "HIGH").forEach { option ->
                FilterChip(
                    selected = priority == option,
                    onClick = { priority = option },
                    label = { Text(option) }
                )
            }
        }

        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Choose Due Date")
        }

        Text(
            text = java.text.SimpleDateFormat(
                "dd MMM yyyy",
                java.util.Locale.getDefault()
            ).format(java.util.Date(dueDate))
        )

        if (errorMessage.isNotBlank()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    when {
                        title.isBlank() ->
                            errorMessage = "Please enter a task title"

                        selectedCourseId == null ->
                            errorMessage = "Please select a course"

                        courses.none { it.first == selectedCourseId } ->
                            errorMessage = "Selected course is not available"

                        else -> onSave(
                            title.trim(),
                            description.trim(),
                            selectedCourseId!!,
                            dueDate,
                            priority
                        )
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Task")
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            dueDate = it
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

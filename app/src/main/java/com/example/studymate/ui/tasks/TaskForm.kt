package com.example.studymate.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studymate.data.Course
import com.example.studymate.data.Priority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val BackgroundPink = Color(0xFFFAF2F3)
private val DarkBurgundy = Color(0xFF40232D)
private val MutedRose = Color(0xFF967A83)
private val CardWhite = Color(0xFFFFFEFE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskForm(
    courses: List<Course>,
    initialTitle: String = "",
    initialDescription: String = "",
    initialCourseId: Int? = null,
    initialDueDate: Long = System.currentTimeMillis(),
    initialPriority: String = Priority.MEDIUM,
    errorMessage: String? = null,
    onInputChange: () -> Unit = {},
    onSave: (String, String, Int?, Long, String) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember(initialTitle) { mutableStateOf(initialTitle) }
    var description by remember(initialDescription) { mutableStateOf(initialDescription) }
    var selectedCourseId by remember(initialCourseId) { mutableStateOf(initialCourseId) }
    var dueDate by remember(initialDueDate) { mutableLongStateOf(initialDueDate) }
    var priority by remember(initialPriority) { mutableStateOf(initialPriority) }
    var showDatePicker by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dueDate
    )

    val selectedCourseLabel = courses.firstOrNull { it.courseId == selectedCourseId }
        ?.let { "${it.courseCode.uppercase()} · ${it.courseName}" }
        ?: "Select a course"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPink)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (initialTitle.isBlank()) "New Task." else "Edit Task.",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBurgundy
        )

        Text(
            text = "FILL IN ASSIGNMENT DETAILS",
            fontSize = 10.sp,
            letterSpacing = 2.5.sp,
            color = MutedRose
        )

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                onInputChange()
            },
            label = { Text("Task Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            isError = errorMessage != null && title.isBlank()
        )

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                onInputChange()
            },
            label = { Text("Description (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            shape = RoundedCornerShape(14.dp)
        )

        Text(
            text = "Course",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = DarkBurgundy
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCourseLabel,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                isError = errorMessage != null && selectedCourseId == null
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                courses.forEach { course ->
                    DropdownMenuItem(
                        text = {
                            Text("${course.courseCode.uppercase()} · ${course.courseName}")
                        },
                        onClick = {
                            selectedCourseId = course.courseId
                            expanded = false
                            onInputChange()
                        }
                    )
                }
            }
        }

        Text(
            text = "Priority",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = DarkBurgundy
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Priority.ALL.forEach { option ->
                FilterChip(
                    selected = priority == option,
                    onClick = {
                        priority = option
                        onInputChange()
                    },
                    label = { Text(option) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DarkBurgundy,
                        selectedLabelColor = Color.White,
                        containerColor = CardWhite,
                        labelColor = MutedRose
                    )
                )
            }
        }

        Text(
            text = "Due Date",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = DarkBurgundy
        )

        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkBurgundy)
        ) {
            val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(Date(dueDate))
            Text("Selected: $formattedDate  (Change)")
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkBurgundy)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    onSave(title, description, selectedCourseId, dueDate, priority)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkBurgundy,
                    contentColor = Color.White
                )
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
                        datePickerState.selectedDateMillis?.let { dueDate = it }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = DarkBurgundy)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = MutedRose)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
package com.example.studymate.ui.courses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CourseForm(
    initialName: String = "",
    initialCode: String = "",
    title: String = "Add Course",
    errorMessage: String? = null,
    onInputChange: () -> Unit = {},
    onSave: (String, String) -> Unit,
    onCancel: () -> Unit
) {
    var courseName by remember(initialName) { mutableStateOf(initialName) }
    var courseCode by remember(initialCode) { mutableStateOf(initialCode) }

    AlertDialog(
        onDismissRequest = onCancel,
        shape = RoundedCornerShape(20.dp),
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = courseName,
                    onValueChange = {
                        courseName = it
                        onInputChange()
                    },
                    label = { Text("Course Name") },
                    singleLine = true,
                    isError = errorMessage != null && courseName.isBlank()
                )

                OutlinedTextField(
                    value = courseCode,
                    onValueChange = {
                        courseCode = it
                        onInputChange()
                    },
                    label = { Text("Course Code") },
                    singleLine = true,
                    isError = errorMessage != null
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(courseName, courseCode) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}
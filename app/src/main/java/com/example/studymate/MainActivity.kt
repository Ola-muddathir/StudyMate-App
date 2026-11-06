
package com.example.studymate

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.studymate.ui.tasks.TasksScreen
import com.example.studymate.ui.theme.StudyMateTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StudyMateTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        TasksScreen(
                            tasks = emptyList(),

                            onAddTask = {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Task form will be connected soon",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },

                            onEditTask = { task ->
                                Toast.makeText(
                                    this@MainActivity,
                                    "Edit: ${task.title}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },

                            onDeleteTask = { task ->
                                Toast.makeText(
                                    this@MainActivity,
                                    "Delete: ${task.title}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },

                            onToggleComplete = { task ->
                                Toast.makeText(
                                    this@MainActivity,
                                    "Task: ${task.title}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

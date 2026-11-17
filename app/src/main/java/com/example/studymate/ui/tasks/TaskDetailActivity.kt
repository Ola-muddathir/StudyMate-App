package com.example.studymate.ui.tasks

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.studymate.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        val title = intent.getStringExtra("EXTRA_TITLE") ?: "Untitled Task"
        val course = intent.getStringExtra("EXTRA_COURSE") ?: ""
        val description = intent.getStringExtra("EXTRA_DESC") ?: ""
        val priority = intent.getStringExtra("EXTRA_PRIORITY") ?: "MEDIUM"
        val dueDate = intent.getLongExtra("EXTRA_DUE_DATE", System.currentTimeMillis())
        val isCompleted = intent.getBooleanExtra("EXTRA_COMPLETED", false)

        val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            .format(Date(dueDate))
        val statusText = if (isCompleted) "Completed" else "Pending"

        findViewById<TextView>(R.id.tvDetailPriority).text = "$priority PRIORITY"
        findViewById<TextView>(R.id.tvDetailTitle).text = title
        findViewById<TextView>(R.id.tvDetailCourse).text = course
        findViewById<TextView>(R.id.tvDetailDescription).text =
            description.ifBlank { "No description provided." }
        findViewById<TextView>(R.id.tvDetailMeta).text =
            "Due: $formattedDate  ·  Status: $statusText"

        findViewById<Button>(R.id.btnShareTask).setOnClickListener {
            val shareText = "StudyMate Task:\n$title ($course)\nDue: $formattedDate\nPriority: $priority"
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(sendIntent, "Share task via"))
        }

        findViewById<Button>(R.id.btnBackToTasks).setOnClickListener {
            finish()
        }
    }
}
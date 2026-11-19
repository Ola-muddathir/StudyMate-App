
package com.example.studymate.utils

import android.content.Context
import android.content.Intent

class ShareUtils {

    fun shareTask(
        context: Context,
        title: String,
        description: String,
        dueDate: String,
        priority: String
    ) {
        val shareText = """
            Task: $title
            Description: $description
            Due Date: $dueDate
            Priority: $priority
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        context.startActivity(
            Intent.createChooser(intent, "Share Task")
        )
    }
}

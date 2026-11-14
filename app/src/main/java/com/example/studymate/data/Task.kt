package com.example.studymate.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ForeignKey


object Priority {
    const val LOW = "LOW"
    const val MEDIUM = "MEDIUM"
    const val HIGH = "HIGH"

    val ALL = listOf(LOW, MEDIUM, HIGH)

    fun isValid(value: String): Boolean = value in ALL
}

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Course::class,
            parentColumns = ["courseId"],
            childColumns = ["courseId"],
            // course cannot be deleted while it still has task
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("courseId")]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId: Int = 0,
    val title: String,
    val description: String = "",
    val courseId: Int,
    val dueDate: Long,
    val priority: String = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
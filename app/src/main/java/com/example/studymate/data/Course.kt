package com.example.studymate.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true)
    val courseId: Int = 0,
    val courseName: String,
    val courseCode: String
)

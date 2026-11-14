package com.example.studymate.data
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Insert
    suspend fun insertCourse(course: Course): Long

    @Update
    suspend fun updateCourse(course: Course)
 // Throws Exception if  the course still has tasks (RESTRICT)
    @Delete
    suspend fun deleteCourse(course: Course)

    @Query("SELECT * FROM courses ORDER BY courseName ASC")
    fun getAllCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE courseId = :courseId")
    suspend fun getCourseById(courseId: Int): Course?
}
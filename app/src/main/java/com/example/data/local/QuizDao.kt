package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Question
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {

    @Query("SELECT * FROM quiz_questions WHERE isActive = 1")
    fun getAllActiveQuestionsFlow(): Flow<List<Question>>

    @Query("SELECT * FROM quiz_questions WHERE isActive = 1")
    suspend fun getAllActiveQuestions(): List<Question>

    @Query("SELECT * FROM quiz_questions WHERE categoryId = :categoryId AND isActive = 1")
    suspend fun getQuestionsByCategory(categoryId: String): List<Question>

    @Query("SELECT * FROM quiz_questions WHERE id = :id LIMIT 1")
    suspend fun getQuestionById(id: String): Question?

    @Query("SELECT COUNT(*) FROM quiz_questions WHERE isActive = 1")
    suspend fun getTotalQuestionsCount(): Int

    @Query("SELECT COUNT(*) FROM quiz_questions WHERE categoryId = :categoryId AND isActive = 1")
    suspend fun getCategoryQuestionsCount(categoryId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<Question>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: Question)

    @Update
    suspend fun updateQuestion(question: Question)

    @Query("DELETE FROM quiz_questions WHERE id = :id")
    suspend fun deleteQuestionById(id: String)
}

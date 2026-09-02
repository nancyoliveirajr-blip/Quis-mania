package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.backend.DefaultQuestionBank
import com.example.data.model.MatchResult
import com.example.data.model.Question
import com.example.data.model.SecurityLog
import com.example.data.model.UserPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Question::class, UserPlayer::class, MatchResult::class, SecurityLog::class],
    version = 1,
    exportSchema = false
)
abstract class QuizDatabase : RoomDatabase() {

    abstract fun quizDao(): QuizDao
    abstract fun userDao(): UserDao
    abstract fun securityLogDao(): SecurityLogDao

    companion object {
        @Volatile
        private var INSTANCE: QuizDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): QuizDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    "quiz_mania_secure.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate secure questions bank and default player
                        INSTANCE?.let { database ->
                            scope.launch(Dispatchers.IO) {
                                database.quizDao().insertQuestions(DefaultQuestionBank.questions)
                                database.userDao().insertUser(
                                    UserPlayer(
                                        id = "local_player_1",
                                        username = "Mestre Quiz",
                                        email = "jogador@quizmania.app",
                                        level = 1,
                                        coins = 200,
                                        lives = 5
                                    )
                                )
                                database.securityLogDao().insertLog(
                                    SecurityLog(
                                        eventType = "SYSTEM_INIT",
                                        description = "Quiz Mania Core Engine inicializado com proteção e 300+ perguntas.",
                                        ipOrDeviceId = "local_secure_engine",
                                        severity = "INFO"
                                    )
                                )
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

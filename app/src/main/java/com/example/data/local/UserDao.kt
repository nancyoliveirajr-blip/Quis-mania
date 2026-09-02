package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MatchResult
import com.example.data.model.UserPlayer
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM user_players WHERE id = :userId LIMIT 1")
    fun getUserFlow(userId: String = "local_player_1"): Flow<UserPlayer?>

    @Query("SELECT * FROM user_players WHERE id = :userId LIMIT 1")
    suspend fun getUser(userId: String = "local_player_1"): UserPlayer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserPlayer)

    @Update
    suspend fun updateUser(user: UserPlayer)

    @Query("SELECT * FROM match_history ORDER BY playedAt DESC LIMIT 50")
    fun getMatchHistoryFlow(): Flow<List<MatchResult>>

    @Query("SELECT * FROM match_history ORDER BY playedAt DESC LIMIT 100")
    suspend fun getAllMatches(): List<MatchResult>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchResult(result: MatchResult)

    @Query("SELECT COUNT(*) FROM match_history")
    suspend fun getTotalMatchesCount(): Long

    @Query("SELECT AVG(accuracy) FROM match_history")
    suspend fun getAverageAccuracy(): Double?

    @Query("SELECT * FROM user_players")
    suspend fun getAllPlayers(): List<UserPlayer>
}

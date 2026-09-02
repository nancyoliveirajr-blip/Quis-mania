package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SecurityLog
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityLogDao {

    @Query("SELECT * FROM security_audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllLogsFlow(): Flow<List<SecurityLog>>

    @Query("SELECT * FROM security_audit_logs ORDER BY timestamp DESC LIMIT 200")
    suspend fun getAllLogs(): List<SecurityLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SecurityLog)

    @Query("SELECT COUNT(*) FROM security_audit_logs WHERE eventType = 'ANTI_CHEAT_TRIGGER'")
    suspend fun getAntiCheatCount(): Long

    @Query("SELECT COUNT(*) FROM security_audit_logs WHERE eventType = 'RATE_LIMIT'")
    suspend fun getRateLimitCount(): Long

    @Query("DELETE FROM security_audit_logs WHERE timestamp < :olderThanTimestamp")
    suspend fun pruneOldLogs(olderThanTimestamp: Long)
}

package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "security_audit_logs")
@JsonClass(generateAdapter = true)
data class SecurityLog(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0,
    val eventType: String, // AUTH_FAIL, ANTI_CHEAT_TRIGGER, SESSION_EXPIRED, ADMIN_ACTION, RATE_LIMIT
    val description: String,
    val ipOrDeviceId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val severity: String = "INFO" // INFO, WARN, ALERT, CRITICAL
)

data class SystemAnalytics(
    val totalQuestionsInBank: Int,
    val activeCategoriesCount: Int,
    val totalMatchesPlayed: Long,
    val registeredPlayersCount: Long,
    val activeVipSubscribers: Long,
    val antiCheatInterceptionsCount: Long,
    val averageMatchAccuracy: Double,
    val rateLimitBlockedRequests: Long,
    val serverUptimeHours: Double = 99.98
)

data class DailyChallengeConfig(
    val dateString: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val xpMultiplier: Float = 2.0f,
    val coinReward: Int = 100,
    val requiredScore: Int = 800
)

data class AdSystemConfig(
    val interstitialIntervalMatches: Int = 3,
    val rewardedVideoCoins: Int = 50,
    val rewardedVideoLives: Int = 2,
    val adsEnabledForFreeUsers: Boolean = true,
    val bannerAdsEnabled: Boolean = true
)

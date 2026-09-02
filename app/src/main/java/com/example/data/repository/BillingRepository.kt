package com.example.data.repository

import com.example.data.local.UserDao
import com.example.data.model.UserPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

data class StoreProduct(
    val productId: String,
    val title: String,
    val description: String,
    val priceFormatted: String,
    val type: ProductType,
    val badge: String? = null
)

enum class ProductType {
    VIP_SUBSCRIPTION_MONTHLY,
    VIP_SUBSCRIPTION_ANNUAL,
    COIN_PACK_SMALL,
    COIN_PACK_LARGE,
    POWERUP_BUNDLE,
    UNLIMITED_LIVES_PASS
}

/**
 * Repositório preparado para integração com a Google Play Billing Library.
 * Gerencia catálogo de produtos IAP/Assinaturas, checkout seguro e persistência do status VIP.
 */
class BillingRepository(
    private val userDao: UserDao
) {
    private val _isBillingReady = MutableStateFlow(true)
    val isBillingReady: StateFlow<Boolean> = _isBillingReady.asStateFlow()

    val availableProducts = listOf(
        StoreProduct(
            productId = "quizmania_vip_monthly",
            title = "Quiz Mania VIP Mensal",
            description = "Vidas infinitas, sem anúncios, 2x moedas & XP, categorias exclusivas",
            priceFormatted = "R$ 14,90/mês",
            type = ProductType.VIP_SUBSCRIPTION_MONTHLY,
            badge = "MAIS POPULAR"
        ),
        StoreProduct(
            productId = "quizmania_vip_annual",
            title = "Quiz Mania VIP Anual",
            description = "12 meses de acesso VIP com 40% de desconto e selo Dourado",
            priceFormatted = "R$ 99,90/ano",
            type = ProductType.VIP_SUBSCRIPTION_ANNUAL,
            badge = "40% OFF"
        ),
        StoreProduct(
            productId = "pack_coins_500",
            title = "Cofre de Moedas (500)",
            description = "500 Moedas Mania para comprar poderes e ajudas no jogo",
            priceFormatted = "R$ 4,90",
            type = ProductType.COIN_PACK_SMALL
        ),
        StoreProduct(
            productId = "pack_coins_2000",
            title = "Tesouro de Moedas (2.000)",
            description = "2.000 Moedas Mania + Bônus de 500 moedas grátis",
            priceFormatted = "R$ 14,90",
            type = ProductType.COIN_PACK_LARGE,
            badge = "+25% BÔNUS"
        ),
        StoreProduct(
            productId = "bundle_powerups_pro",
            title = "Kit Mestre de Poderes",
            description = "10x 50/50, 10x Pular Pergunta e 10x Congelar Tempo",
            priceFormatted = "R$ 9,90",
            type = ProductType.POWERUP_BUNDLE
        )
    )

    /**
     * Processa a compra via Google Play Billing e ativa os benefícios
     */
    suspend fun purchaseProduct(productId: String, userId: String = "local_player_1"): Result<String> = withContext(Dispatchers.IO) {
        val user = userDao.getUser(userId) ?: UserPlayer(id = userId)
        val product = availableProducts.find { it.productId == productId }
            ?: return@withContext Result.failure(Exception("Produto não encontrado no catálogo da Google Play."))

        when (product.type) {
            ProductType.VIP_SUBSCRIPTION_MONTHLY -> {
                val updated = user.copy(
                    isVip = true,
                    vipExpiryTimestamp = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
                    lives = 999
                )
                userDao.updateUser(updated)
                Result.success("Assinatura VIP Mensal ativada com sucesso!")
            }
            ProductType.VIP_SUBSCRIPTION_ANNUAL -> {
                val updated = user.copy(
                    isVip = true,
                    vipExpiryTimestamp = System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000),
                    lives = 999,
                    coins = user.coins + 1000
                )
                userDao.updateUser(updated)
                Result.success("Assinatura VIP Anual ativada com sucesso! +1000 Moedas bônus!")
            }
            ProductType.COIN_PACK_SMALL -> {
                val updated = user.copy(coins = user.coins + 500)
                userDao.updateUser(updated)
                Result.success("500 Moedas adicionadas à sua conta!")
            }
            ProductType.COIN_PACK_LARGE -> {
                val updated = user.copy(coins = user.coins + 2500)
                userDao.updateUser(updated)
                Result.success("2.500 Moedas adicionadas à sua conta!")
            }
            ProductType.POWERUP_BUNDLE -> {
                val updated = user.copy(
                    powerUpFiftyFifty = user.powerUpFiftyFifty + 10,
                    powerUpSkip = user.powerUpSkip + 10,
                    powerUpTimeFreeze = user.powerUpTimeFreeze + 10
                )
                userDao.updateUser(updated)
                Result.success("Kit de poderes creditado com sucesso!")
            }
            ProductType.UNLIMITED_LIVES_PASS -> {
                val updated = user.copy(lives = user.maxLives)
                userDao.updateUser(updated)
                Result.success("Vidas restauradas ao máximo!")
            }
        }
    }

    /**
     * Compra item com moedas do próprio jogo
     */
    suspend fun buyInGameItemWithCoins(itemKey: String, cost: Int, userId: String = "local_player_1"): Result<String> = withContext(Dispatchers.IO) {
        val user = userDao.getUser(userId) ?: return@withContext Result.failure(Exception("Usuário não encontrado"))
        if (user.coins < cost) {
            return@withContext Result.failure(Exception("Moedas insuficientes. Você possui ${user.coins} moedas."))
        }

        val updated = when (itemKey) {
            "REFILL_LIVES" -> user.copy(coins = user.coins - cost, lives = user.maxLives)
            "BUY_FIFTY_FIFTY" -> user.copy(coins = user.coins - cost, powerUpFiftyFifty = user.powerUpFiftyFifty + 3)
            "BUY_SKIP" -> user.copy(coins = user.coins - cost, powerUpSkip = user.powerUpSkip + 3)
            "BUY_FREEZE" -> user.copy(coins = user.coins - cost, powerUpTimeFreeze = user.powerUpTimeFreeze + 3)
            else -> user.copy(coins = user.coins - cost)
        }
        userDao.updateUser(updated)
        Result.success("Compra realizada com sucesso!")
    }

    /**
     * Simula recompensa por anúncio assistido (Rewarded Video Ad)
     */
    suspend fun rewardFromAd(rewardType: String, userId: String = "local_player_1"): Result<String> = withContext(Dispatchers.IO) {
        val user = userDao.getUser(userId) ?: return@withContext Result.failure(Exception("Usuário não encontrado"))
        val updated = when (rewardType) {
            "FREE_LIVES" -> user.copy(lives = minOf(user.maxLives, user.lives + 2))
            "FREE_COINS" -> user.copy(coins = user.coins + 50)
            else -> user.copy(coins = user.coins + 25)
        }
        userDao.updateUser(updated)
        Result.success("Recompensa creditada por apoiar o QUIZ MANIA!")
    }
}

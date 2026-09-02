package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class QuizCategory(
    val id: String,
    val displayName: String,
    val description: String,
    val iconName: String,
    val primaryColor: Long,
    val secondaryColor: Long
) {
    GERAL(
        id = "geral",
        displayName = "Conhecimentos Gerais",
        description = "Cultura pop, fatos do mundo e atualidades",
        iconName = "Public",
        primaryColor = 0xFF6366F1,
        secondaryColor = 0xFF4338CA
    ),
    BIBLIA(
        id = "biblia",
        displayName = "Bíblia",
        description = "Antigo e Novo Testamento, personagens e ensinamentos",
        iconName = "MenuBook",
        primaryColor = 0xFFF59E0B,
        secondaryColor = 0xFFD97706
    ),
    HISTORIA(
        id = "historia",
        displayName = "História",
        description = "Grandes civilizações, guerras e momentos marcantes",
        iconName = "AccountBalance",
        primaryColor = 0xFFEC4899,
        secondaryColor = 0xFFBE185D
    ),
    GEOGRAFIA(
        id = "geografia",
        displayName = "Geografia",
        description = "Países, capitais, relevos, climas e bandeiras",
        iconName = "Explore",
        primaryColor = 0xFF10B981,
        secondaryColor = 0xFF047857
    ),
    CIENCIA(
        id = "ciencia",
        displayName = "Ciência",
        description = "Física, Química, Biologia, Astronomia e o Universo",
        iconName = "Science",
        primaryColor = 0xFF06B6D4,
        secondaryColor = 0xFF0E7490
    ),
    MATEMATICA(
        id = "matematica",
        displayName = "Matemática",
        description = "Lógica, aritmética, álgebra, geometria e enigmas",
        iconName = "Calculate",
        primaryColor = 0xFF8B5CF6,
        secondaryColor = 0xFF6D28D9
    ),
    PORTUGUES(
        id = "portugues",
        displayName = "Português",
        description = "Gramática, ortografia, literatura e figuras de linguagem",
        iconName = "Spellcheck",
        primaryColor = 0xFF3B82F6,
        secondaryColor = 0xFF1D4ED8
    ),
    TECNOLOGIA(
        id = "tecnologia",
        displayName = "Tecnologia",
        description = "Informática, inteligência artificial, games e internet",
        iconName = "Memory",
        primaryColor = 0xFF14B8A6,
        secondaryColor = 0xFF0F766E
    ),
    FUTEBOL(
        id = "futebol",
        displayName = "Futebol",
        description = "Copas do Mundo, clubes, lendas do esporte e regras",
        iconName = "SportsSoccer",
        primaryColor = 0xFF22C55E,
        secondaryColor = 0xFF15803D
    ),
    MUSICA(
        id = "musica",
        displayName = "Música",
        description = "Gêneros musicais, instrumentos, cantores e hits mundiais",
        iconName = "MusicNote",
        primaryColor = 0xFFA855F7,
        secondaryColor = 0xFF7E22CE
    ),
    FILMES_SERIES(
        id = "filmes_series",
        displayName = "Filmes e Séries",
        description = "Cinema, Hollywood, clássicos, streaming e bastidores",
        iconName = "Movie",
        primaryColor = 0xFFE11D48,
        secondaryColor = 0xFF9F1239
    ),
    CURIOSIDADES(
        id = "curiosidades",
        displayName = "Curiosidades",
        description = "Recordes do Guinness, fatos bizarros e mistérios",
        iconName = "Psychology",
        primaryColor = 0xFFF97316,
        secondaryColor = 0xFFC2410C
    );

    companion object {
        fun fromId(id: String): QuizCategory {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: GERAL
        }
    }
}

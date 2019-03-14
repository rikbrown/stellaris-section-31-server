package codes.rik.klausewitz.stellaris.server

import codes.rik.klausewitz.parser.stellaris.GameState
import me.ntrrgc.tsGenerator.TypeScriptGenerator
import java.time.LocalDate
import java.time.LocalDateTime

val typesTs: String by lazy {
    TypeScriptGenerator(
            rootClasses = setOf(
                    BasicCountryStats::class,
                    PerennialCountryData::class,
                    GameState::class
            ),
            mappings = mapOf(
                    LocalDateTime::class to "Date",
                    LocalDate::class to "Date"
            )
    ).individualDefinitions.joinToString("\n\n") { it.replace("^interface ".toRegex(), "export interface ") }
}
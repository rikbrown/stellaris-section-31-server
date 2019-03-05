package codes.rik.klausewitz.stellaris.server

import codes.rik.klausewitz.parser.stellaris.GameState
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import com.google.common.collect.Multimaps
import com.mongodb.MongoClient
import com.mongodb.client.model.Indexes.ascending
import mu.KotlinLogging
import org.litote.kmongo.KMongo
import org.litote.kmongo.distinct
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
import org.litote.kmongo.util.KMongoConfiguration
import java.io.Closeable
import java.time.LocalDate

class StellarisDb(client: MongoClient = KMongo.createClient("localhost", 27017)) : Closeable by client {
    val collection = client.getDatabase("stellaris").getCollection<GameState>()
    val importer = GameImporter(this)

    private val invalidGames: MutableSet<String> = mutableSetOf()
    private val gameStateCache: Multimap<String, GameState> = Multimaps.synchronizedMultimap(MultimapBuilder.hashKeys().arrayListValues().build())

    init {
        initDb()
    }

    fun getGameNames() = collection.distinct(GameState::name).toSet()

    fun getGameStates(gameName: String): List<GameState> {
        synchronized(gameStateCache) {
            if (gameStateCache[gameName].isEmpty() && !invalidGames.contains(gameName)) populateCache(gameName)
        }

        return gameStateCache[gameName].toList()
    }

    fun getGameState(gameName: String, date: LocalDate): GameState? {
        return getGameStates(gameName).find { it.date == date }
    }

    private fun populateCache(gameName: String) {
        logger.debug { "Populating cache for $gameName" }
        val games = collection.find(GameState::name eq gameName).toList()
        if (games.isEmpty()) {
            logger.warn { "No states for $gameName" }
            invalidGames.add(gameName)
        } else {
            gameStateCache.putAll(gameName, games)
        }
    }

    private fun initDb() {
        KMongoConfiguration.extendedJsonMapper
            .registerModule(KotlinModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        collection.createIndex(ascending("name"))
        collection.createIndex(ascending("name", "date"))
    }
}

private val logger = KotlinLogging.logger {}
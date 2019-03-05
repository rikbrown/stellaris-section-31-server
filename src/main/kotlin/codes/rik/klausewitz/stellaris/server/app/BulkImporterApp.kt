package codes.rik.klausewitz.stellaris.server.app

import codes.rik.klausewitz.stellaris.server.StellarisDb
import io.ktor.util.extension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

fun main() {
    StellarisDb().use { db ->
        val jobs = Files.list(path.resolve(".")).toList()
            .filter { it.extension == "sav" }
            .sortedBy { it.fileName }
            .map { GlobalScope.async { db.importer.import(it) }  }

        runBlocking(Dispatchers.Default) {
            jobs.forEach { it.await() }
        }
    }
}

private val path = Paths.get("/Users/rik/stellaris-saves/save games/mpmrbrownsdiddletradingcoltd2_-1385733640")


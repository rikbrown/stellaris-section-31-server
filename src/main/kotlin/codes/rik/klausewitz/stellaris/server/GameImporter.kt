package codes.rik.klausewitz.stellaris.server

import codes.rik.klausewitz.parser.parseParadoxFile
import codes.rik.klausewitz.parser.stellaris.GameState
import codes.rik.kotlinbits.io.createTempDirectory
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.ReplaceOptions
import net.lingala.zip4j.core.ZipFile
import org.litote.kmongo.eq
import java.nio.file.Path

class GameImporter(private val db: StellarisDb) {
    fun import(path: Path) {

        createTempDirectory("stellaris-saves") { dest ->
            val fileName = path.fileName
            val zip = ZipFile(path.toString())
            val zipDest = dest.resolve(fileName)
            zip.extractAll(zipDest.toString())

            println("Processing: $path")
            val gameState = parseParadoxFile<GameState>(zipDest.resolve("gamestate"))

            println("Importing: ${gameState.name} @ ${gameState.date}")

            db.collection.replaceOne(
                and(
                    GameState::name eq gameState.name,
                    GameState::date eq gameState.date
                ),
                gameState,
                ReplaceOptions().upsert(true)
            )
        }
    }
}

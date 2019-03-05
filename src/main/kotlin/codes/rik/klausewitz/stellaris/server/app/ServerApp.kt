package codes.rik.klausewitz.stellaris.server.app

import codes.rik.klausewitz.stellaris.server.serverModule
import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty, 8080, watchPaths = listOf("codes/rik"), module = Application::serverModule).start()
}
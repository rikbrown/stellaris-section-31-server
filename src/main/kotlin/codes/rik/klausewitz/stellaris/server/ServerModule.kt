package codes.rik.klausewitz.stellaris.server

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.routing.routing
import org.slf4j.event.Level.INFO

fun Application.serverModule() {
    install(AutoHeadResponse)
    install(DefaultHeaders)
    install(CallLogging) {
        level = INFO
    }
    install(CORS) {
        anyHost()
    }
    install(ContentNegotiation) {
        jackson {
            registerModule(KotlinModule())
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }

    routing {
        routes()
    }
}


//fun getDates(gameName: String): Set<LocalDate> {
//    val states = db.getGameStates(gameName)
//    return states
//        .map { it.date }
//        .sortedDescending()
//        .toSet()
//}
//fun getCountriesOnDate(gameName: String, date: LocalDate): Set<BasicCountryStats> {
//    val states = db.getGameState(gameName, date) ?: throw IllegalArgumentException("No game $gameName/$date")
//
//    return states.countries
//        .filter { (_, country) -> country != null }
//        .map { (id, country) -> BasicCountryStats(id, country) }
//        .toSet()
//}
//
//fun getCountries(gameName: String): Set<PerennialCountryData> {
//    val states = db.getGameStates(gameName)
//
//    return states
//        .flatMap { it.countries.entries }
//        .filter { (_, country) -> country != null }
//        .map { (id, country) -> PerennialCountryData(id, country) }
//        .toSet()
//}
//

package codes.rik.klausewitz.stellaris.server

import codes.rik.klausewitz.parser.stellaris.GameState
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.util.pipeline.PipelineContext
import me.ntrrgc.tsGenerator.TypeScriptGenerator
import org.slf4j.event.Level.INFO
import java.time.LocalDate
import java.time.LocalDateTime

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
        static {
            resources("static-content")
            defaultResource("index.html", "static-content")
        }

        get("/types.ts") {
            call.respondText(typesTs)
        }

        route("/api") {
            get("ping") {
                call.respondText("pong")
            }

            route("/game") {
                get("") {
                    call.respond(getGames())
                }

                route("{gameName}") {

                    get("country") {
                        call.respond(getCountries(gameName))
                    }

                    route("date") {
                        get("") {
                            call.respond(getDates(gameName))
                        }

                        route("{date}") {
                            get("country") {
                                call.respond(getCountriesOnDate(gameName, date))
                            }
                        }
                    }

                    route("/view") {
                        get("budgets") {
                            call.respond(getBudgets(gameName))
                        }
                    }
                }
            }
        }

    }
}

private val PipelineContext<*, ApplicationCall>.gameName get() = param("gameName")
private val PipelineContext<*, ApplicationCall>.date: LocalDate get() {
    val dateParam = param("date")
    return if (dateParam == "latest") {
        getDates(gameName).last()
    } else {
        LocalDate.parse(dateParam)
    }
}
private fun PipelineContext<*, ApplicationCall>.param(name: String) = call.parameters[name]  ?: throw IllegalArgumentException("Missing parameter: $name")

private val db = StellarisDb()

private val typesTs: String
    get() = TypeScriptGenerator(
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

fun getGames() = db.getGameNames()

fun getDates(gameName: String): Set<LocalDate> {
    val states = db.getGameStates(gameName)
    return states
        .map { it.date }
        .sortedDescending()
        .toSet()
}
fun getCountriesOnDate(gameName: String, date: LocalDate): Set<BasicCountryStats> {
    val states = db.getGameState(gameName, date) ?: throw IllegalArgumentException("No game $gameName/$date")

    return states.countries
        .filter { (_, country) -> country != null }
        .map { (id, country) -> BasicCountryStats(id, country) }
        .toSet()
}

fun getCountries(gameName: String): Set<PerennialCountryData> {
    val states = db.getGameStates(gameName)

    return states
        .flatMap { it.countries.entries }
        .filter { (_, country) -> country != null }
        .map { (id, country) -> PerennialCountryData(id, country) }
        .toSet()
}

fun getBudgets(gameName: String): Any {
    val states = db.getGameStates(gameName)

    val empireIds = states
        .map { it.countries }
        .flatMap { it.keys }
        .distinct()

    return empireIds
}


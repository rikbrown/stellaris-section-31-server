package codes.rik.klausewitz.stellaris.server

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.util.pipeline.PipelineContext

fun Routing.routes() {
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
                call.respond(stellarisDb.getGameNames())
            }

//            route("{gameName}") {
//
//                get("country") {
//                    call.respond(getCountries(gameName))
//                }
//
//                route("date") {
//                    get("") {
//                        call.respond(getDates(gameName))
//                    }
//
//                    route("{date}") {
//                        get("country") {
//                            call.respond(getCountriesOnDate(gameName, date))
//                        }
//                    }
//                }
//
//                route("/view") {
//                    get("budgets") {
//                        call.respond(getBudgets(gameName))
//                    }
//                }
//            }
        }
    }
}

private val PipelineContext<*, ApplicationCall>.gameName get() = param("gameName")
//private val PipelineContext<*, ApplicationCall>.date: LocalDate
//    get() {
//        val dateParam = param("date")
//        return if (dateParam == "latest") {
//            getDates(gameName).last()
//        } else {
//            LocalDate.parse(dateParam)
//        }
//    }
private fun PipelineContext<*, ApplicationCall>.param(name: String) = call.parameters[name]  ?: throw IllegalArgumentException("Missing parameter: $name")
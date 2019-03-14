package codes.rik.klausewitz.stellaris.server.views

import codes.rik.klausewitz.parser.stellaris.Country
import codes.rik.klausewitz.parser.stellaris.GameState
import java.time.LocalDate

interface ViewGenerator<T> {
    fun generate(): T
}

class CountriesByDateViewGenerator(val gameStates: List<GameState>): ViewGenerator<List<ObjectWithContext<Country>>> {
    private val gameName = gameStates.map { it.name }.single()

    override fun generate(): List<ObjectWithContext<Country>> {
        return gameStates
            .map { it.date to it.countries }
            .flatMap { (date, countries) ->
                countries.map { (countryId, country) ->
                    ObjectWithContext(
                        gameName = gameName,
                        date = date,
                        id = countryId,
                        value = country
                    )
                }
            }
    }



}

data class ObjectWithContext<ObjectType>(
    val gameName: String,
    val date: LocalDate,
    val id: Long,
    val value: ObjectType)
package codes.rik.klausewitz.stellaris.server.views

import codes.rik.klausewitz.parser.stellaris.Country
import codes.rik.klausewitz.stellaris.server.StellarisDb
import java.time.LocalDate

/*

    {
        empire_name: 'Brown',
        data: [
            {
                "date": "2300-01-01",
                "income": {},
                "expense" {},
                "balance": {}.
            }
        ]

 */

class MonthlyIncomeViewGenerator(
    private val gameName: String,
    private val db: StellarisDb) {

    fun generate() {
        val states = db.getGameStates(gameName).toList()

        val empireIds = states
            .map { it.countries }
            .flatMap { it.keys }
            .toList()

        empireIds.map { empireId ->
            Dataset(
                empireId = empireId,
                data = states.map { gameState ->
                    Dataset.BudgetRow(
                        date = gameState.date,
                        data = gameState.countries[empireId]?.budget?.lastMonth
                    )
                }
            )
        }

    }

}

data class Dataset(
    val empireId: Long,
    val data: List<BudgetRow>
) {

    data class BudgetRow(
        val date: LocalDate,
        val data: Country.BudgetMonth?)

}
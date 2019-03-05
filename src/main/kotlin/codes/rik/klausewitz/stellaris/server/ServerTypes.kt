package codes.rik.klausewitz.stellaris.server

import codes.rik.klausewitz.parser.stellaris.Country

data class PerennialCountryData(
    val id: Long,
    val name: String,
    val flag: Country.Flag) {

    constructor(id: Long, country: Country) : this(
        id = id,
        name = country.name,
        flag = country.flag)
}

data class BasicCountryStats(
    val id: Long,
    val name: String,
    val flag: Country.Flag,
    val victoryScore: Double,
    val techPower: Double,
    val immigration: Double,
    val emigration: Double,
    val fleetSize: Double,
    val empireSize: Long,
    val militaryPower: Double,
    val economyPower: Double,
    val megastructureCount: Int,
    val traditionCount: Int) {

    constructor(id: Long, country: Country) : this(
        id = id,
        name = country.name,
        flag = country.flag,
        victoryScore = country.victoryScore,
        techPower = country.techPower,
        immigration = country.immigration,
        emigration = country.emigration,
        fleetSize = country.fleetSize,
        empireSize = country.empireSize,
        militaryPower = country.militaryPower,
        economyPower = country.economyPower,
        megastructureCount = country.ownedMegastructures.size,
        traditionCount = country.traditions.size)
}
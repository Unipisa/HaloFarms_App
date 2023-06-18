package it.unipi.halofarms.screen.zone

data class Map(
    var name: String? = null,
    var latitude: String? = null,
    var longitude: String? = null,
    var points: List<String>? = null,
    var perimeterPoints: List<String>? = null,
    var mode: String? = null,
    var area: String? = null,
    var done: String? = null
)

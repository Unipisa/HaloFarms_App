package it.unipi.halofarms.screen.point

data class Point (
    var name: String? = null,
    var zoneName: String? = null,
    var qrCode: String? = null,
    var analyzed: String? = null,
    var toBeAnalyzed: String? = null,
    var sarList: List<String>? = null,
    var phList: List<String>? = null,
    var ecList: List<String>? = null,
    var cecList: List<String>? = null,
    var dateList: List<String>? = null
)

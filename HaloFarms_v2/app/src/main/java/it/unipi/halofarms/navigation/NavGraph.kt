package it.unipi.halofarms.navigation

/**
 * The screens' sealed class
 *
 * @param route Screen's route
 */
sealed class ScreenNavigator(val route : String){
    // Map screen
    object Map : ScreenNavigator(route = "map/{mapId}/{date}"){
        const val ARG_MAP_ID: String = "mapId"
        const val ARG_DATE: String = "date"
        fun route(mapId: String?, date: String?) = "map/$mapId/$date"
    }
    // Heatmap screen
    object HeatMap : ScreenNavigator(route = "heatmap/{mapId}/{mode}/{date}"){
        const val ARG_MAP_ID: String = "mapId"
        const val ARG_MODE_ID: String = "mode"
        const val ARG_DATE: String = "date"
        fun route(mapId: String?, mode: String?, date: String?) = "heatmap/$mapId/$mode/$date"
    }

    // History screen
    object History : ScreenNavigator(route = "history/{mapName}"){
        const val ARG_MAP_ID: String = "mapName"
        fun route(mapName: String?) = "history/$mapName"
    }

    // Home screen
    object Home : ScreenNavigator("home")
}

/**
 * The dialogs' sealed class
 * @param route Specific dialog's route
 */
sealed class Dialog(val route: String){

    // 'Add a map' dialog
    object AddMap : Dialog("addmap")
    // 'Delete a map' dialog
    object LongMap : Dialog("longmap/{mapId}"){
        const val ARG_MAP_ID: String = "mapId"
        fun route(mapId: String?) = "longmap/$mapId"
    }
    // 'Add values' dialog
    object PointOptions : Dialog("pointOptions/{pointId}/{mapId}"){
        const val ARG_POINT_ID: String = "pointId"
        const val ARG_MAP_ID: String = "mapId"
        fun route(pointId: String?, mapId: String?) = "pointOptions/$pointId/$mapId"
    }
    // 'Choose heatmap' dialog
    object ChooseHeatmap : Dialog("mode/{mapId}/{date}"){
        const val ARG_MAP_ID: String = "mapId"
        const val ARG_DATE: String = "date"
        fun route(mapId: String?, date: String?) = "mode/$mapId/$date"
    }
}

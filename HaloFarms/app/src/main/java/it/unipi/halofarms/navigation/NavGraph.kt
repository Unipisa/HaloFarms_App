package it.unipi.halofarms.navigation

import androidx.annotation.DrawableRes
import it.unipi.halofarms.R

/**
 * The bottom bar screens' sealed class
 *
 * @param route Screen's route
 * @param title Screen's title
 * @param icon Screen's icon
 */
sealed class Navigator(val route : String, val title : String?, @DrawableRes val icon : Int?){
    object Map : Navigator(route = "map/{mapId}", title = "Map", icon = R.drawable.mappa){
        const val ARG_MAP_ID: String = "mapId"
        fun route(mapId: String?) = "map/$mapId"
    }
    object HeatMap : Navigator(route = "heatmap/{mapId}/{mode}", title = "HeatMap", icon = R.drawable.heatmap){
        const val ARG_MAP_ID: String = "mapId"
        const val ARG_MODE_ID: String = "mode"
        fun route(mapId: String?, mode: String?) = "heatmap/$mapId/$mode"
    }
}


/**
 * The default screens' sealed class
 *
 * @param route Specific route for default screens
 */
sealed class DefaultNavigation(val route: String) {
    // The 'Home' screen
    object Home : DefaultNavigation("home")
    // The 'Point' screen
    object Point : DefaultNavigation(route = "point/{pointId}"){
        const val ARG_POINT_ID: String = "pointId"
        fun route(pointId: String?) = "point/$pointId"
    }
}

/**
 * The dialogs' sealed class
 * @param route Specific dialog's route
 */
sealed class Dialog(val route: String){
    // The 'Add a map' dialog
    object AddMap : Dialog("addmap")
    // The 'Delete a map' dialog
    object LongMap : Dialog("longmap/{mapId}"){
        const val ARG_MAP_ID: String = "mapId"
        fun route(mapId: String?) = "longmap/$mapId"
    }
    // The 'Delete a map' dialog
    object LongPoint : Dialog("longpoint/{mapId}/{pointId}"){
        const val ARG_POINT_ID: String = "pointId"
        const val ARG_MAP_ID: String = "mapId"
        fun route(pointId: String, mapId: String) = "longPlant/$mapId/$pointId"
    }
    // The 'Add values' dialog
    object PointOptions : Dialog("pointOptions/{pointId}"){
        const val ARG_POINT_ID: String = "pointId"
        fun route(pointId: String?) = "pointOptions/$pointId"
    }
    // The 'Choose heatmap' dialog
    object ChooseHeatmap : Dialog("mode/{mapId}"){
        const val ARG_MAP_ID: String = "mapId"
        fun route(mapId: String?) = "mode/$mapId"
    }
}

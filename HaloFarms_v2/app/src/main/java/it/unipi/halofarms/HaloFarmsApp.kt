package it.unipi.halofarms

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import it.unipi.halofarms.data.HaloFarmsDatabase
import it.unipi.halofarms.data.cloud.FirestoreProxy
import it.unipi.halofarms.data.cloud.FirestoreProxy.Companion.sharedPreferences
import it.unipi.halofarms.data.map.MapRepo
import it.unipi.halofarms.data.perimeterPoint.PerimeterPointRepo
import it.unipi.halofarms.data.point.PointValueRepo
import it.unipi.halofarms.data.sample.SampleRepo
import it.unipi.halofarms.navigation.Dialog
import it.unipi.halofarms.navigation.ScreenNavigator
import it.unipi.halofarms.screen.home.HomeScreen
import it.unipi.halofarms.screen.map.HeatMapScreen
import it.unipi.halofarms.screen.map.MapScreen
import it.unipi.halofarms.screen.map.MapViewModel
import it.unipi.halofarms.screen.map.PointViewModel
import it.unipi.halofarms.ui.theme.HaloFarmsTheme
import it.unipi.halofarms.util.AddDialog
import it.unipi.halofarms.util.ChooseDate
import it.unipi.halofarms.util.ChooseHeatMap
import it.unipi.halofarms.util.DeleteMap
import it.unipi.halofarms.util.NewValuesForPoint
import it.unipi.halofarms.util.locationPermissionState
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
@ExperimentalMaterialApi
fun HaloFarmsApp() {
    HaloFarmsTheme {
        Surface(color = Color.White) {
            val context = LocalContext.current
            val proxy = FirestoreProxy()

            // Navcontroller
            val navController = rememberNavController()

            // Repositories (room)
            val db = HaloFarmsDatabase.getInstance(context)
            val pointValueRepo = PointValueRepo(db.pointValueDao())
            val mapRepo = MapRepo(db.mapDao())
            val perimeterRepo = PerimeterPointRepo(db.perimeterPointDao())
            val sampleRepo = SampleRepo(db.sampleDao())

            val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

            if(isFirstRun){
                proxy.FromCloud(pointValueRepo, mapRepo, perimeterRepo, sampleRepo, context)
                sharedPreferences.edit().putBoolean("isFirstRun", false).apply()
            }

            // Gets map's viewmodel
            val mapsViewModel = MapViewModel(mapRepo, pointValueRepo, perimeterRepo, sampleRepo)
            // Maps
            val maps = mapsViewModel.mapList().observeAsState(initial = emptyList())
            // Perimeter points
            val pPoints =  mapsViewModel.perimeterPoints().observeAsState(initial = emptyList())
            // Checks and gets location permissions
            val locationPermission: MultiplePermissionsState = locationPermissionState()
            // Coroutine's scope
            val scope = rememberCoroutineScope()
            // Gets point's viewmodel
            val pointViewModel = PointViewModel(pointValueRepo, sampleRepo)
            // Selected icon in bottom app bar (map screen)
            val selected = remember { mutableStateOf(BottomIcons.MAP) }

            NavHost(navController, startDestination = ScreenNavigator.Home.route) {

                /* Brings to the home screen */
                composable(ScreenNavigator.Home.route) {
                    HomeScreen(locationPermission, maps) { route, clearBackStack ->
                        navController.navigate(route = route) {
                            if (clearBackStack) popUpTo(route) { inclusive = true }
                        }
                    }
                }

                /* Brings to the map screen */
                composable(ScreenNavigator.Map.route) {
                    // Gets the map name
                    val mapName = it.arguments?.getString(ScreenNavigator.Map.ARG_MAP_ID).orEmpty()
                    // Current date
                    val date = it.arguments?.getString(ScreenNavigator.Map.ARG_DATE).orEmpty()
                    // Current map
                    val map = maps.value.find { map -> map.name == mapName }
                    // Current map's value points
                    val points = pointViewModel.getPoints(mapName).observeAsState(initial = emptyList())
                    // Samples
                    val samples = pointViewModel.getSamples(date, mapName).observeAsState(initial = emptyList())

                    MapScreen(
                        onBackPressed = { navController.navigateUp() },
                        pointViewModel = pointViewModel,
                        samples = samples,
                        selectedValues = selected,
                        date = date,
                        points = points,
                        pPoints = pPoints.value.filter { point -> point.zoneName == mapName },
                        mapsViewModel = mapsViewModel,
                        map = map!!,
                    ){ route ->
                        navController.navigate(route = route) { popUpTo(route) }
                    }
                }

                /* Gets to the 'Heatmap' screen */
                composable(ScreenNavigator.HeatMap.route) {
                    // Gets the map name
                    val mapName = it.arguments?.getString(ScreenNavigator.HeatMap.ARG_MAP_ID).orEmpty()
                    // Current map's mode
                    val mode = it.arguments?.getString(ScreenNavigator.HeatMap.ARG_MODE_ID).orEmpty()
                    // Current date
                    val date = it.arguments?.getString(ScreenNavigator.HeatMap.ARG_DATE).orEmpty()
                    // Current map
                    val map = maps.value.find { map -> map.name == mapName }
                    // Current map's value points
                    val pointsValue = pointViewModel.getPoints(mapName).observeAsState(initial = emptyList())
                    // Samples
                    val samples = pointViewModel.getSamples(date, mapName).observeAsState(initial = emptyList())

                   HeatMapScreen(
                       map = map!!,
                       mode = mode,
                       date = date,
                       perimeterPoints = pPoints.value.filter{ point -> point.zoneName == mapName},
                       onBackPressed = { navController.navigateUp() },
                       selectedValues = selected,
                       pointsValue = pointsValue.value,
                       mapsViewModel = mapsViewModel,
                       samples = samples.value
                   ){ route ->
                        navController.navigate(route = route) { popUpTo(route) }
                    }
                }

                /* Brings to the 'ChooseHeatmap' dialog */
                dialog(
                    Dialog.ChooseHeatmap.route,
                    dialogProperties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                    )
                ) {
                    // Gets the map name
                    val mapName = it.arguments?.getString(Dialog.ChooseHeatmap.ARG_MAP_ID).orEmpty()
                    // Current date
                    val date = it.arguments?.getString(Dialog.ChooseHeatmap.ARG_DATE).orEmpty()

                    ChooseHeatMap(
                        mapName,
                        date,
                    ) { route ->
                        navController.navigate(route = route) { popUpTo(route) }
                    }
                }

                /* Brings to the 'History' dialog */
                dialog(
                    ScreenNavigator.History.route,
                    dialogProperties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                    )
                ){
                    // Gets the map name
                    val mapName = it.arguments?.getString(ScreenNavigator.History.ARG_MAP_ID).orEmpty()

                    ChooseDate(mapName, pointViewModel) { route ->
                        navController.navigate(route = route) { popUpTo(route) }
                    }
                }

                /* Brings to the 'DeleteMap' dialog */
                dialog(
                    Dialog.LongMap.route,
                    dialogProperties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                    )
                ){
                    // Gets the map name
                    val mapName = it.arguments?.getString(Dialog.LongMap.ARG_MAP_ID).orEmpty()

                    // Deletes a map
                    DeleteMap(
                        mapName,
                        mapsViewModel,
                        navController,
                        pPoints.value.filter{ point -> point.zoneName == mapName})
                }

                /* Brings to the 'AddMap' dialog */
                dialog(
                    Dialog.AddMap.route,
                    dialogProperties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                    )){
                    // Adds a map
                    AddDialog(mapsViewModel, navController)
                }

                /* Brings to the 'PointOptions' dialog */
                dialog(
                    Dialog.PointOptions.route,
                    dialogProperties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                    )){
                    // PointValue's name
                    val pointName = it.arguments?.getString(Dialog.PointOptions.ARG_POINT_ID).orEmpty()
                    // Map's name
                    val mapName = it.arguments?.getString(Dialog.PointOptions.ARG_MAP_ID).orEmpty()

                    // Adds new values for the current point
                    NewValuesForPoint(pointViewModel, pointName, mapName, scope, navController)
                }
            }
        }
    }
}

enum class BottomIcons {
    HISTORY, MAP, HEATMAP
}

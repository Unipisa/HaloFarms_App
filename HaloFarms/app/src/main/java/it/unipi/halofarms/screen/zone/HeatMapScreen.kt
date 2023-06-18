package it.unipi.halofarms.screen.zone

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import it.unipi.halofarms.R
import it.unipi.halofarms.screen.point.Point
import it.unipi.halofarms.screen.point.PointViewModel
import it.unipi.halofarms.util.SwitchMode
import it.unipi.halofarms.util.Up
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

// Tag used while interacting with the LOG
private const val TAG = "HeatMapScreen"

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HeatMapScreen(
    map: Map,
    mode: String,
    mapName: String,
    onBackPressed: () -> Unit,
    points: State<List<Point>>,
    mapsViewModel: MapViewModel,
    pointViewModel: PointViewModel,
    navigateTo: (String) -> Unit,
) {
    // Coroutine's scope
    val scope = rememberCoroutineScope()
    // Scaffold's state
    val scaffoldState = rememberScaffoldState()
    // Current clicked point
    val currentPoint = remember { mutableStateOf("") }
    // BottomSheetScaffold's state
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()

    Scaffold(
        bottomBar = { BottomAppBarCompose(scope, scaffoldState, navigateTo, mapName) },
        scaffoldState = scaffoldState,
        drawerContent = {
            Text("Points", fontSize = TextUnit(36F, TextUnitType.Sp), modifier = Modifier.padding(18.dp))
            val lazyListState = rememberLazyListState()

            LazyColumn(state = lazyListState, userScrollEnabled = true) {
                points.value
                    .filter{ point -> point.zoneName == mapName }
                    .forEach { point ->
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                                    .clickable {
                                        scope.launch {
                                            scaffoldState.drawerState.close()
                                            currentPoint.value = point.name!!
                                            bottomSheetScaffoldState.bottomSheetState.expand()
                                        }
                                    },
                                elevation = 0.dp,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                PointRow(point.name)
                            }
                        }
                    }
            }
        },
        drawerGesturesEnabled = true
    ) { padding ->
        BottomSheetScaffold(
            topBar = {
                Row {
                    // Back button
                    Up(onBackPressed)
                    // Decides if the switch should be seen
                    if(map.perimeterPoints == null || map.points == null)
                        SwitchMode(map, mapsViewModel)
                }
            },
            sheetContent = {
                SheetContent(
                    padding,
                    scope,
                    navigateTo,
                    bottomSheetScaffoldState,
                    pointViewModel,
                    points.value.find { point -> point.name == currentPoint.value }
                )
            },
            scaffoldState = bottomSheetScaffoldState,
            sheetPeekHeight = 20.dp,
            sheetShape = RoundedCornerShape(24.dp),
            sheetGesturesEnabled = true,
            sheetElevation = 12.dp,
            sheetBackgroundColor = androidx.compose.ui.graphics.Color.White,
            backgroundColor = androidx.compose.ui.graphics.Color.Transparent,
            content = {
                HeatBody(
                    map,
                    padding,
                    bottomSheetScaffoldState,
                    scope,
                    points.value.filter { point -> point.zoneName == mapName },
                    currentPoint,
                    mode
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HeatBody(
    map: Map,
    padding: PaddingValues,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    scope: CoroutineScope,
    points: List<Point>,
    currentPoint: MutableState<String>,
    mode: String
) {
    // Destination
    val destination = LatLng(map.latitude!!.toDouble(), map.longitude!!.toDouble())
    // Camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(destination, 18f)
    }
    val area = remember { mutableStateOf(0.0) }
    val perimeterPoints:MutableList<LatLng> = remember { mutableListOf() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = MapProperties(isMyLocationEnabled = true, mapType = MapType.HYBRID),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(indoorLevelPickerEnabled = true)
        ) {
            DrawPerimeter(map = map,
                currentPoint = currentPoint,
                scope = scope,
                area = area,
                done = map.done,
                perimeterPoints = perimeterPoints)

            points.forEach { point ->
                when (mode) {
                    "ec" -> {
                        EcHeatMap(point = point)
                    }
                    "cec" -> {
                        CecHeatMap(point = point)
                    }
                    "sar" -> {
                        SarHeatMap(point = point)
                    }
                    else -> {
                        PhHeatMap(point = point)
                    }
                }
            }
        }
    }
}
/**
 * Set the correct color of a point.
 * Point will be colored according to result of analysis.
 * @param p point to color.
 */
@Composable
fun setPointColor(p: Point, mode: String) : IntArray {
    var colors: IntArray = intArrayOf(4)
    val context = LocalContext.current

    if(mode == "ec" && p.ecList != null) {
        if (p.ecList!!.last().toDouble() <= 2) {
            colors = intArrayOf(
                ContextCompat.getColor(context,
                R.color.light_green_A100),
            (ContextCompat.getColor(context,
                R.color.light_green_A200)),
            (ContextCompat.getColor(context,
                R.color.light_green_A400)),
            (ContextCompat.getColor(context,
                R.color.light_green_A700)))
        } else if (p.ecList!!.last().toDouble() <= 4) {
            colors = intArrayOf(
                ContextCompat.getColor(context,
                R.color.lime_A100),
            (ContextCompat.getColor(context,
                R.color.lime_A200)),
            (ContextCompat.getColor(context,
                R.color.lime_A400)),
            (ContextCompat.getColor(context,
                R.color.lime_A700)))
        } else if (p.ecList!!.last().toDouble() <= 8) {
            colors = intArrayOf(ContextCompat.getColor(context,
                R.color.amber_A100))
            (ContextCompat.getColor(context,
                R.color.amber_A200))
            (ContextCompat.getColor(context,
                R.color.amber_A400))
            (ContextCompat.getColor(context,
                R.color.amber_A700))
        } else if (p.ecList!!.last().toDouble() <= 16) {
            colors = intArrayOf(ContextCompat.getColor(context,
                R.color.orangeA100))
            (ContextCompat.getColor(context,
                R.color.orangeA200))
            (ContextCompat.getColor(context,
                R.color.orangeA400))
            (ContextCompat.getColor(context,
                R.color.orangeA700))
        } else {
            colors = intArrayOf(ContextCompat.getColor(context,
                R.color.deep_orangeA100))
            (ContextCompat.getColor(context,
                R.color.deep_orangeA200))
            (ContextCompat.getColor(context,
                R.color.deep_orangeA400))
            (ContextCompat.getColor(context,
                R.color.deep_orangeA700))
        }
    } else if(mode == "sar" && p.sarList != null) {
        if (p.sarList!!.last().toDouble() <= 10) {
            colors = intArrayOf(ContextCompat.getColor(context,
                R.color.light_green_A100))
            (ContextCompat.getColor(context,
                R.color.light_green_A200))
            (ContextCompat.getColor(context,
                R.color.light_green_A400))
            (ContextCompat.getColor(context,
                R.color.light_green_A700))
        } else if (p.sarList!!.last().toDouble() <= 18) {
            colors = intArrayOf(ContextCompat.getColor(context,
                R.color.yellow_A100))
            (ContextCompat.getColor(context,
                R.color.yellow_A200))
            (ContextCompat.getColor(context,
                R.color.yellow_A400))
            (ContextCompat.getColor(context,
                R.color.yellow_A700))
        } else if (p.sarList!!.last().toDouble() <= 26) {
            colors = intArrayOf(ContextCompat.getColor(context,
                R.color.orangeA100))
            (ContextCompat.getColor(context,
                R.color.orangeA200))
            (ContextCompat.getColor(context,
                R.color.orangeA400))
            (ContextCompat.getColor(context,
                R.color.orangeA700))
        } else {
            colors = intArrayOf(ContextCompat.getColor(context,
                R.color.deep_orangeA100))
            (ContextCompat.getColor(context,
                R.color.deep_orangeA200))
            (ContextCompat.getColor(context,
                R.color.deep_orangeA400))
            (ContextCompat.getColor(context,
                R.color.deep_orangeA700))
        }
    } else if(mode == "cec" && p.cecList != null) {
        if (p.cecList!!.last().toDouble() in 50.0..100.0) {
            colors = intArrayOf(ContextCompat.getColor(context,
                R.color.light_green_A100))
            (ContextCompat.getColor(context,
                R.color.light_green_A200))
            (ContextCompat.getColor(context,
                R.color.light_green_A400))
            (ContextCompat.getColor(context,
                R.color.light_green_A700))
        } else if (p.cecList!!.last().toDouble() in 25.0..50.0) {
            colors = intArrayOf(ContextCompat.getColor(context,
                R.color.lime_A100))
            (ContextCompat.getColor(context,
                R.color.lime_A200))
            (ContextCompat.getColor(context,
                R.color.lime_A400))
            (ContextCompat.getColor(context,
                R.color.lime_A700))
        } else if (p.cecList!!.last().toDouble() in 15.0..25.0) {
            colors = intArrayOf(ContextCompat.getColor(context,
                R.color.amber_A100))
            (ContextCompat.getColor(context,
                R.color.amber_A200))
            (ContextCompat.getColor(context,
                R.color.amber_A400))
            (ContextCompat.getColor(context,
                R.color.amber_A700))
        } else if (p.cecList!!.last().toDouble() in 10.0..15.0) {
            colors = intArrayOf(ContextCompat.getColor(context,
                R.color.orangeA100))
            (ContextCompat.getColor(context,
                R.color.orangeA200))
            (ContextCompat.getColor(context,
                R.color.orangeA400))
            (ContextCompat.getColor(context,
                R.color.orangeA700))
        } else {
            colors = intArrayOf(ContextCompat.getColor(context,
                R.color.deep_orangeA100))
            (ContextCompat.getColor(context,
                R.color.deep_orangeA200))
            (ContextCompat.getColor(context,
                R.color.deep_orangeA400))
            (ContextCompat.getColor(context,
                R.color.deep_orangeA700))
        }
    } else if(mode == "ph" && p.phList != null) {
        if (p.phList!!.last().toDouble() <= 5.5) {
            colors = intArrayOf(ContextCompat.getColor(context,
                R.color.orangeA100))
            (ContextCompat.getColor(context,
                R.color.orangeA200))
            (ContextCompat.getColor(context,
                R.color.orangeA400))
            (ContextCompat.getColor(context,
                R.color.orangeA700))
        } else if (p.phList!!.last().toDouble() in 5.8..6.5) {
            colors = intArrayOf(ContextCompat.getColor(context,
                R.color.light_green_A100))
            (ContextCompat.getColor(context,
                R.color.light_green_A200))
            (ContextCompat.getColor(context,
                R.color.light_green_A400))
            (ContextCompat.getColor(context,
                R.color.light_green_A700))
        } else {
            colors = intArrayOf(ContextCompat.getColor(context,
                R.color.deep_orangeA100))
            (ContextCompat.getColor(context,
                R.color.deep_orangeA200))
            (ContextCompat.getColor(context,
                R.color.deep_orangeA400))
            (ContextCompat.getColor(context,
                R.color.deep_orangeA700))
        }
    }

    return colors
}

fun pointLatLng(point: Point): LatLng {
    return LatLng(
        point.name!!.substringAfter("(").substringBefore(",").toDouble(),
        point.name!!.substringAfter(",").substringBefore(")").toDouble()
    )
}

@Composable
fun EcHeatMap(point: Point) {


    val data = pointLatLng(point)
    // starting point for each color, given as a percentage of the maximum intensity
    val startPoints = floatArrayOf(
        0.1f, 0.3f, 0.6f, 1f
    )

    // Create the gradient with whatever start and end colors you wish to use
    val colors = setPointColor(p = point, mode = "ec")
    if(colors.size == 4) {
        val gradient = Gradient(colors, startPoints)
        val heatMapProvider = HeatmapTileProvider.Builder()
            .data(listOf(data))
            .gradient(gradient)
            .radius(50)
            .build()

        TileOverlay(tileProvider = heatMapProvider, visible = true, fadeIn = true)
    }

}

@Composable
fun CecHeatMap(point: Point) {

    val data = pointLatLng(point)

    // starting point for each color, given as a percentage of the maximum intensity
    val startPoints = floatArrayOf(
        0.1f, 0.3f, 0.6f, 1f
    )

    val colors = setPointColor(p = point, mode = "cec")
    if(colors.size == 4) {
        val gradient = Gradient(colors, startPoints)
        val heatMapProvider = HeatmapTileProvider.Builder()
            .data(listOf(data))
            .gradient(gradient) // set gradient
            .radius(50)
            .build()

        TileOverlay(tileProvider = heatMapProvider, visible = true, fadeIn = true)
    }

}

@Composable
fun PhHeatMap(point: Point) {

    val data = pointLatLng(point)

    // starting point for each color, given as a percentage of the maximum intensity
    val startPoints = floatArrayOf(
        0.1f, 0.3f, 0.6f, 1f
    )

    val colors = setPointColor(p = point, mode = "ec")
    if(colors.size == 4) {
        val gradient = Gradient(colors, startPoints)
        val heatMapProvider = HeatmapTileProvider.Builder()
            .data(listOf(data))
            .gradient(gradient) // set gradient
            .radius(50)
            .build()

        TileOverlay(tileProvider = heatMapProvider, visible = true, fadeIn = true)
    }
}

@Composable
fun SarHeatMap(point: Point) {

    val data = pointLatLng(point)

    // starting point for each color, given as a percentage of the maximum intensity
    val startPoints = floatArrayOf(
        0.1f, 0.3f, 0.6f, 1f
    )
    val colors = setPointColor(p = point, mode = "ec")

    if(colors.size == 4) {
        val gradient = Gradient(colors, startPoints)
        val heatMapProvider = HeatmapTileProvider.Builder()
            .data(listOf(data))
            .gradient(gradient) // set gradient
            .radius(50)
            .build()

        TileOverlay(tileProvider = heatMapProvider, visible = true, fadeIn = true)
    }
}

/*
/**
 * Convert a string into a list of LatLng.
 * The string has the format of double.
 * It is saved on Firestore in this way because there is a bug.
 *
 * @param string to convert in a list of LatLng
 * @return the list containing coordinates.
 */
private fun fromStringToLatLng(string: String): ArrayList<LatLng> {
    val tokenizer = StringTokenizer(string)
    val coordinates: ArrayList<LatLng> = ArrayList()
    while (tokenizer.hasMoreTokens()) {
        coordinates.add(LatLng(tokenizer.nextToken().toDouble(),
            tokenizer.nextToken().toDouble()))
    }
    return coordinates
}
*/
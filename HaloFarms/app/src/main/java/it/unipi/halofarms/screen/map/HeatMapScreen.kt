package it.unipi.halofarms.screen.map

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import it.unipi.halofarms.BottomIcons
import it.unipi.halofarms.R
import it.unipi.halofarms.data.map.Map
import it.unipi.halofarms.data.perimeterPoint.PerimeterPoint
import it.unipi.halofarms.data.point.PointValue
import it.unipi.halofarms.data.sample.Sample
import it.unipi.halofarms.util.BackButton
import it.unipi.halofarms.util.BottomAppBarCompose
import it.unipi.halofarms.util.MapArea
import it.unipi.halofarms.util.fromDbToLatLng
import java.util.*

// Tag used while interacting with the LOG
private const val TAG = "HeatMapScreen"

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HeatMapScreen(
    map: Map,
    mode: String,
    date: String,
    perimeterPoints: List<PerimeterPoint>,
    onBackPressed: () -> Unit,
    selectedValues: MutableState<BottomIcons>,
    pointsValue: List<PointValue>?,
    mapsViewModel: MapViewModel,
    samples: List<Sample>,
    navigateTo: (String) -> Unit,
) {

    // Coroutine's scope
    val scope = rememberCoroutineScope()
    // Scaffold state
    val scaffoldState = rememberScaffoldState()


    androidx.compose.material.Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = {
            BottomAppBarCompose(
                scope = scope,
                navigateTo = navigateTo,
                mapName =  map.name,
                date = date,
                selectedValues
            )
        },
        topBar = {
            Row {
                // Back button
                BackButton(onBackPressed)
                MapArea(map)
            }
        }
    ) { padding ->
        if (pointsValue != null) {
            HeatBody(
                map = map,
                mode = mode,
                padding = padding,
                points = pointsValue,
                samples = samples,
                perimeterPoints = perimeterPoints,
                mapsViewModel = mapsViewModel
            )
        }
    }
}

/**
 * Shows the heatmap of the current map
 *
 * @param map Current map
 * @param mode Heatmap's mode
 * @param padding Graphic's padding values
 * @param points Current map's points
 * @param samples Current map's samples
 * @param perimeterPoints Current map's perimeterPoints
 * @param mapsViewModel Maps View Model
 */
@Composable
fun HeatBody(
    map: Map,
    mode: String,
    padding: PaddingValues,
    points: List<PointValue>,
    samples: List<Sample>,
    perimeterPoints: List<PerimeterPoint>,
    mapsViewModel: MapViewModel
) {
    // Destination
    val destination = LatLng(map.latitude, map.longitude)
    // Camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(destination, 18f)
    }

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
            DrawPerimeter(
                done = map.done,
                perimeterPoints = fromDbToLatLng(perimeterPoints),
                mapsViewModel = mapsViewModel,
                map = map
            )

            Log.e("HEATMAPSCREEN", "VALORE DI MODE: $mode")


            points.forEach { point ->

                when (mode) {
                    "ec" -> {
                        samples.find{sample -> sample.latitude == point.latitude && sample.longitude == point.longitude}
                            ?.let { EcHeatMap(sample = it) }
                    }
                    "cec" -> {
                        samples.find{sample -> sample.latitude == point.latitude && sample.longitude == point.longitude}
                            ?.let { CecHeatMap(sample = it) }
                    }
                    "sar" -> {
                        samples.find{sample -> sample.latitude == point.latitude && sample.longitude == point.longitude}
                            ?.let { SarHeatMap(sample = it) }
                     }
                    else -> {
                        samples.find{sample -> sample.latitude == point.latitude && sample.longitude == point.longitude}
                            ?.let { PhHeatMap(sample = it) }
                    }
                }
            }
        }
    }
}

/**
 * Sets the correct color of the current point.
 * PointValue will be colored according to analysis results.
 *
 * @param sample Current sample
 * @param mode Selected mode for map
 */
@Composable
fun setPointColor(sample: Sample, mode: String) : IntArray {
    var colors: IntArray = intArrayOf(4)
    val context = LocalContext.current

    when (mode) {
        "ec" -> {
            when {
                sample.ec <= 2 -> {
                    colors = intArrayOf(
                        ContextCompat.getColor(context,
                            R.color.light_green_A100),
                        ContextCompat.getColor(context,
                            R.color.light_green_A200),
                        ContextCompat.getColor(context,
                            R.color.light_green_A400),
                        ContextCompat.getColor(context,
                            R.color.light_green_A700))
                }

                sample.ec <= 4 -> {
                    colors = intArrayOf(
                        ContextCompat.getColor(context,
                            R.color.lime_A100),
                        ContextCompat.getColor(context,
                            R.color.lime_A200),
                        ContextCompat.getColor(context,
                            R.color.lime_A400),
                        ContextCompat.getColor(context,
                            R.color.lime_A700))
                }

                sample.ec <= 8 -> {
                    colors = intArrayOf(ContextCompat.getColor(context,
                        R.color.amber_A100),
                    ContextCompat.getColor(context,
                        R.color.amber_A200),
                    ContextCompat.getColor(context,
                        R.color.amber_A400),
                    ContextCompat.getColor(context,
                        R.color.amber_A700))
                }

                sample.ec <= 16 -> {
                    colors = intArrayOf(ContextCompat.getColor(context,
                        R.color.orangeA100),
                    ContextCompat.getColor(context,
                        R.color.orangeA200),
                    ContextCompat.getColor(context,
                        R.color.orangeA400),
                    ContextCompat.getColor(context,
                        R.color.orangeA700))
                }

                else -> {
                    colors = intArrayOf(ContextCompat.getColor(context,
                        R.color.deep_orangeA100),
                    ContextCompat.getColor(context,
                        R.color.deep_orangeA200),
                    ContextCompat.getColor(context,
                        R.color.deep_orangeA400),
                    ContextCompat.getColor(context,
                        R.color.deep_orangeA700))
                }
            }
        }
        "sar" -> {
            when {
                sample.sar <= 10 -> {
                    colors = intArrayOf(ContextCompat.getColor(context,
                        R.color.light_green_A100),
                    ContextCompat.getColor(context,
                        R.color.light_green_A200),
                    ContextCompat.getColor(context,
                        R.color.light_green_A400),
                    ContextCompat.getColor(context,
                        R.color.light_green_A700))
                }
                sample.sar <= 18 -> {
                    colors = intArrayOf(ContextCompat.getColor(context,
                        R.color.yellow_A100),
                    ContextCompat.getColor(context,
                        R.color.yellow_A200),
                    ContextCompat.getColor(context,
                        R.color.yellow_A400),
                    ContextCompat.getColor(context,
                        R.color.yellow_A700))
                }

                sample.sar <= 26 -> {
                    colors = intArrayOf(ContextCompat.getColor(context,
                        R.color.orangeA100),
                    ContextCompat.getColor(context,
                        R.color.orangeA200),
                    ContextCompat.getColor(context,
                        R.color.orangeA400),
                    ContextCompat.getColor(context,
                        R.color.orangeA700))
                }

                else -> {
                    colors = intArrayOf(ContextCompat.getColor(context,
                        R.color.deep_orangeA100),
                    ContextCompat.getColor(context,
                        R.color.deep_orangeA200),
                    ContextCompat.getColor(context,
                        R.color.deep_orangeA400),
                    ContextCompat.getColor(context,
                        R.color.deep_orangeA700))
                }
            }
        }
        "cec" -> {
            when (sample.cec) {
                in 50.0..100.0 -> {
                    colors = intArrayOf(ContextCompat.getColor(context,
                        R.color.light_green_A100),
                    ContextCompat.getColor(context,
                        R.color.light_green_A200),
                    ContextCompat.getColor(context,
                        R.color.light_green_A400),
                    ContextCompat.getColor(context,
                        R.color.light_green_A700))
                }

                in 25.0..50.0 -> {
                    colors = intArrayOf(ContextCompat.getColor(context,
                        R.color.lime_A100),
                    ContextCompat.getColor(context,
                        R.color.lime_A200),
                    ContextCompat.getColor(context,
                        R.color.lime_A400),
                    ContextCompat.getColor(context,
                        R.color.lime_A700))
                }

                in 15.0..25.0 -> {
                    colors = intArrayOf(ContextCompat.getColor(context,
                        R.color.amber_A100),
                    ContextCompat.getColor(context,
                        R.color.amber_A200),
                    ContextCompat.getColor(context,
                        R.color.amber_A400),
                    ContextCompat.getColor(context,
                        R.color.amber_A700))
                }

                in 10.0..15.0 -> {
                    colors = intArrayOf(ContextCompat.getColor(context,
                        R.color.orangeA100),
                    ContextCompat.getColor(context,
                        R.color.orangeA200),
                    ContextCompat.getColor(context,
                        R.color.orangeA400),
                    ContextCompat.getColor(context,
                        R.color.orangeA700))
                }

                else -> {
                    colors = intArrayOf(ContextCompat.getColor(context,
                        R.color.deep_orangeA100),
                    ContextCompat.getColor(context,
                        R.color.deep_orangeA200),
                    ContextCompat.getColor(context,
                        R.color.deep_orangeA400),
                    ContextCompat.getColor(context,
                        R.color.deep_orangeA700))
                }
            }
        }
        "ph" -> {
            when {
                sample.ph <= 5.5 -> {
                    colors = intArrayOf(ContextCompat.getColor(context,
                        R.color.orangeA100),
                    ContextCompat.getColor(context,
                        R.color.orangeA200),
                    ContextCompat.getColor(context,
                        R.color.orangeA400),
                    ContextCompat.getColor(context,
                        R.color.orangeA700))
                }

                sample.ph in 5.8..6.5 -> {
                    colors = intArrayOf(ContextCompat.getColor(context,
                        R.color.light_green_A100),
                    ContextCompat.getColor(context,
                        R.color.light_green_A200),
                    ContextCompat.getColor(context,
                        R.color.light_green_A400),
                    ContextCompat.getColor(context,
                        R.color.light_green_A700))
                }

                else -> {
                    colors = intArrayOf(ContextCompat.getColor(context,
                        R.color.deep_orangeA100),
                    ContextCompat.getColor(context,
                        R.color.deep_orangeA200),
                    ContextCompat.getColor(context,
                        R.color.deep_orangeA400),
                    ContextCompat.getColor(context,
                        R.color.deep_orangeA700))
                }
            }
        }
    }

    return colors
}

/**
 * Ec heatmap for current point
 * @param sample Current sample
 */
@Composable
fun EcHeatMap(sample: Sample) {
    val data = LatLng(sample.latitude, sample.longitude)
    val startPoints = floatArrayOf(
        0.1f, 0.3f, 0.6f, 1f
    )

    val colors = setPointColor(sample = sample, mode = "ec")

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

/**
 * Cec heatmap for current point
 *
 * @param sample Current sample
 */
@Composable
fun CecHeatMap(sample: Sample) {
    val data = LatLng(sample.latitude, sample.longitude)

    // Starting point for each color, given as a percentage of the maximum intensity
    val startPoints = floatArrayOf(
        0.1f, 0.3f, 0.6f, 1f
    )

    val colors = setPointColor(sample = sample, mode = "cec")
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

/**
 * Ph heatmap for current point
 *
 * @param sample Current sample
 */
@Composable
fun PhHeatMap(sample: Sample) {
    val data = LatLng(sample.latitude, sample.longitude)

    // Starting point for each color, given as a percentage of the maximum intensity
    val startPoints = floatArrayOf(
        0.1f, 0.3f, 0.6f, 1f
    )

    val colors = setPointColor(sample = sample, mode = "ec")
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

/**
 * Sar heatmap for current point
 *
 * @param sample Current sample
 */
@Composable
fun SarHeatMap(sample: Sample) {
    val data = LatLng(sample.latitude, sample.longitude)

    // Starting point for each color, given as a percentage of the maximum intensity
    val startPoints = floatArrayOf(
        0.1f, 0.3f, 0.6f, 1f
    )
    val colors = setPointColor(sample = sample, mode = "ec")

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
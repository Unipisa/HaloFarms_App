package it.unipi.halofarms.screen.map
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.ktx.utils.sphericalDistance
import it.unipi.halofarms.BottomIcons
import it.unipi.halofarms.R
import it.unipi.halofarms.data.map.Map
import it.unipi.halofarms.data.perimeterPoint.PerimeterPoint
import it.unipi.halofarms.data.point.PointValue
import it.unipi.halofarms.data.sample.Sample
import it.unipi.halofarms.ui.theme.Typography
import it.unipi.halofarms.util.BackButton
import it.unipi.halofarms.util.BottomAppBarCompose
import it.unipi.halofarms.util.ButtonsRow
import it.unipi.halofarms.util.Divider
import it.unipi.halofarms.util.DoneButton
import it.unipi.halofarms.util.MapArea
import it.unipi.halofarms.util.MarkerButton
import it.unipi.halofarms.util.PointState
import it.unipi.halofarms.util.SheetTabs
import it.unipi.halofarms.util.SwitchMode
import it.unipi.halofarms.util.bitmapDescriptorFromVector
import it.unipi.halofarms.util.fromDbToLatLng
import it.unipi.halofarms.util.sortVerticies
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapScreen(
    onBackPressed: () -> Unit,
    map: Map,
    mapsViewModel: MapViewModel,
    pointViewModel: PointViewModel,
    points: State<List<PointValue>>,
    date: String,
    samples: State<List<Sample>>,
    pPoints: List<PerimeterPoint>,
    selectedValues: MutableState<BottomIcons>,
    navigateTo: (String) -> Unit,
)  {
    // Coroutine's scope
    val scope = rememberCoroutineScope()
    // ModalBottomSheet's state
    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded)
    // Current point
    val currentPoint = rememberSaveable { mutableStateOf(LatLng(0.00, 0.00)) }
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
                selected = selectedValues
            )
        },
        floatingActionButton = {
            Box {
                Row {
                    if (!map.done) DoneButton(
                        mapName = map.name,
                        mapsViewModel = mapsViewModel,
                        mode = map.mode,
                        scope = scope
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Back button
                BackButton(onBackPressed)

                // Spacer
                Spacer(modifier = Modifier.padding(43.dp))

                // Switch button
                if (!samples.value.any { sample -> (sample.toBeAnalyzed || sample.sar > (-1.00))})
                    SwitchMode(
                        mapViewModel = mapsViewModel,
                        map = map
                    )

                // Spacer
                Spacer(modifier = Modifier.padding(25.dp))

                // Marker Button
                MarkerButton(map.name, pointViewModel)
            }
        }
    ) { padding ->

        ModalBottomSheetLayout(
            sheetContent = {
                SheetContent(
                    padding,
                    navigateTo,
                    pointViewModel,
                    points.value.find { point -> point.latitude == currentPoint.value.latitude && point.longitude == currentPoint.value.longitude },
                    scope,
                    date,
                    samples.value.filter { sample -> sample.latitude == currentPoint.value.latitude && sample.longitude == currentPoint.value.longitude && sample.date == date},
                )
            },
            sheetState = modalBottomSheetState,
            sheetShape = RoundedCornerShape(24.dp),
            sheetGesturesEnabled = true,
            sheetElevation = 145.dp,
            sheetBackgroundColor = Color.White,
            content = {
                    Mapper(
                        date,
                        map,
                        padding,
                        modalBottomSheetState,
                        scope,
                        pointViewModel,
                        points.value,
                        currentPoint,
                        pPoints,
                        mapsViewModel,
                        samples
                    )
            }
        )
    }
}

/**
 * Shows map and points
 *
 * @param date Points' date
 * @param map Current map
 * @param padding Graphics' padding values
 * @param modalBottomSheet Graphics tool (modal bottom sheet)
 * @param scope Current coroutine scope
 * @param pointViewModel Point View Model
 * @param points Maps' points value
 * @param currentPoint Point selected by the user
 * @param pPoints Map's perimeter points
 * @param mapsViewModel Map View Model
 * @param samples Map's samples for the given date
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Mapper(
    date: String,
    map: Map,
    padding: PaddingValues,
    modalBottomSheet: ModalBottomSheetState,
    scope: CoroutineScope,
    pointViewModel: PointViewModel,
    points: List<PointValue>,
    currentPoint: MutableState<LatLng>,
    pPoints: List<PerimeterPoint>,
    mapsViewModel: MapViewModel,
    samples: State<List<Sample>>
) {
    // Destination
    val destination = LatLng(map.latitude, map.longitude)
    // Camera position state
    val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(destination, 18f) }

    // Fetching current year Month and day
    val calendar = Calendar.getInstance()
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]

    val selectedDateText by remember { mutableStateOf("$dayOfMonth-${month + 1}-$year") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = MapProperties(mapType = MapType.HYBRID, isMyLocationEnabled = true),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(compassEnabled = true, indoorLevelPickerEnabled = true, scrollGesturesEnabled = true, zoomGesturesEnabled = true),
            onMapClick = { latlng ->
                if (!map.done) {
                    mapsViewModel.addPerimeterPoint(latlng.latitude, latlng.longitude, map.name)
                } else if (PolyUtil.containsLocation(latlng, fromDbToLatLng(pPoints), false)) {
                    pointViewModel.addPoint(latlng.latitude, latlng.longitude, map.name)
                    pointViewModel.addSampling(latlng.latitude, latlng.longitude, (-1.00), (-1.00), (-1.00), (-1.00), selectedDateText, map.name)
                }
            }
        ) {
            DrawPerimeter(
                done = map.done,
                perimeterPoints = fromDbToLatLng(pPoints),
                mapsViewModel = mapsViewModel,
                map = map
            )

            if(map.done) {
                if (map.mode == "1") {
                    HandfreeSamplingPoints(
                        points,
                        scope,
                        modalBottomSheet,
                        currentPoint,
                        samples,
                        selectedDateText
                    )
                } else {
                    DrawSamplingPoints(samples, map, selectedDateText, currentPoint, scope, modalBottomSheet, pointViewModel, points, fromDbToLatLng(pPoints))
                }
            }
        }

        // Area
        MapArea(map)

    }
}

/**
 * Displays the bottom sheet content
 *
 * @param padding Padding values
 * @param navigateTo Function that brings to a dialog or a new screen
 * @param pointViewModel Point's viewmodel
 * @param currentPoint Current point
 * @param scope Coroutine scope
 * @param date Current date
 * @param samples Map's samples
 */
@Composable
fun SheetContent(
    padding: PaddingValues,
    navigateTo: (String) -> Unit,
    pointViewModel: PointViewModel,
    currentPoint: PointValue?,
    scope: CoroutineScope,
    date: String,
    samples: List<Sample>
) {
    val context = LocalContext.current
    val lazyColumnState = rememberLazyListState()
    val lazyInternalRowState = rememberLazyListState()

    if(currentPoint != null) {
        Box(modifier = Modifier.padding(padding)) {
            LazyColumn (
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                state = lazyColumnState,
                userScrollEnabled = true
            ) {

                item {
                    Column(modifier = Modifier.padding(4.dp)) {
                        // Divider
                        Divider()
                        // Title
                        Text("Point", style = Typography.displaySmall)
                        // PointValue's state
                        PointState(currentPoint, samples)
                        // PointValue's coordinates
                        Text("(${currentPoint.latitude}, ${currentPoint.longitude}", modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        state = lazyInternalRowState
                    ) {
                        item {
                            ButtonsRow(
                                currentPoint,
                                navigateTo,
                                pointViewModel,
                                scope,
                                context,
                                date,
                                samples
                            )
                        }
                    }
                }

                item { SheetTabs(currentPoint, date, samples) }
            }
        }
    }
}

/**
 * Draws the map perimeter
 *
 * @param done Done flag
 * @param perimeterPoints Perimeter points
 * @param mapsViewModel Map's viewmodel
 * @param map Current map
 */
@Composable
fun DrawPerimeter(
    done: Boolean,
    perimeterPoints: ArrayList<LatLng>,
    mapsViewModel: MapViewModel,
    map: Map
) {
    val area = remember { mutableDoubleStateOf(0.00) }
    for (p in perimeterPoints.toList()) {
        Marker(
            state = rememberMarkerState(position = p),
            visible = !done,
            draggable = true
        )

        if (perimeterPoints.size > 0) {
            area.doubleValue = String.format("%.2f", SphericalUtil.computeArea(perimeterPoints)).toDouble()

            sortVerticies(perimeterPoints)
            Polygon(
                points = perimeterPoints.toList(),
                fillColor = Color.Transparent,
                visible = true
            )
        }
    }
    if(!map.done) {
        mapsViewModel.updateArea(map.name, area.doubleValue)
    } else {
        mapsViewModel.updateLatLng(map.name, perimeterPoints)
    }
}

/**
 * Draws the points for the 'Handfree' mode
 *
 * @param points Value points
 * @param scope Coroutine scope
 * @param modalState Modal bottom sheet state
 * @param currentPoint Current point
 * @param samples Map's samples
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HandfreeSamplingPoints(
    points: List<PointValue>,
    scope: CoroutineScope,
    modalState: ModalBottomSheetState,
    currentPoint: MutableState<LatLng>,
    samples: State<List<Sample>>,
    date: String
) {

    Log.e("MAPSCREEN", "VALORE DI DATE IN HANDFREESAMPLINGPOINTS: $date")
    val context = LocalContext.current
        for (p in points) {
            val sample = samples.value.find { sample -> sample.latitude == p.latitude && sample.longitude == p.longitude && sample.date == date }

            if(sample != null)
                Marker(
                    state = rememberMarkerState(position = LatLng(p.latitude, p.longitude)),
                    icon =
                        when {
                            sample.toBeAnalyzed -> {
                                bitmapDescriptorFromVector(context, R.drawable.yellow_round_shape)
                            }

                            (sample.ph > (-1.00)) && (sample.sar > (-1.00)) && (sample.ec > (-1.00)) && (sample.cec > (-1.00)) -> {
                                bitmapDescriptorFromVector(context, R.drawable.green_round_shape)
                            }
                            else -> {
                                bitmapDescriptorFromVector(context, R.drawable.red_round_shape)
                            }

                    },
                    onClick = { marker ->
                        currentPoint.value = marker.position
                        scope.launch { modalState.show() }
                        true
                    },
                    draggable = true
                )

    }
}

/**
 * Draws the points for the 'Draw' mode
 *
 * @param map Current map
 * @param currentPoint Current point
 * @param scope Coroutine scope
 * @param modalState Modal bottom sheet state
 * @param pointViewModel Point's viewmodel
 * @param points Value points
 * @param perimeterPoints Perimeter points
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DrawSamplingPoints(
    samples: State<List<Sample>>,
    map: Map,
    date: String,
    currentPoint: MutableState<LatLng>,
    scope: CoroutineScope,
    modalState: ModalBottomSheetState,
    pointViewModel: PointViewModel,
    points: List<PointValue>?,
    perimeterPoints: ArrayList<LatLng>
) {
    val context = LocalContext.current

    if(points.isNullOrEmpty()) {
        sortVerticies(perimeterPoints)
        /* Gets all the perimeter's points */
        val builder: LatLngBounds.Builder = LatLngBounds.builder()
        for (latLng in perimeterPoints.toList()) {
            builder.include(latLng)
        }

        /* Gets the top-right vertex */
        val northeast: LatLng = builder.build().northeast
        /* Gets the bottom left vertex */
        val southwest: LatLng = builder.build().southwest
        /* Gets top-left vertex */
        val northwest = LatLng(northeast.latitude, southwest.longitude)
        /* Gets bottom-right vertex */
        val southeast = LatLng(southwest.latitude, northeast.longitude)

        val shortDistance = northwest.sphericalDistance(northeast)
        val longDistance = northwest.sphericalDistance(southwest)
        val shortStep = shortDistance.div(900000)
        val longStep = longDistance.div(1900000)

        var left: LatLng = southwest
        var right: LatLng = southeast

        while(left.latitude < northwest.latitude){
            DrawLine(
                date = date,
                left = left,
                right = right,
                shortStep = shortStep,
                coordinates = perimeterPoints,
                currentPoint = currentPoint,
                modalState =  modalState,
                scope = scope,
                pointViewModel = pointViewModel,
                map = map,
                context = context
            )
            left = LatLng(left.latitude + longStep, left.longitude)
            right = LatLng(right.latitude + longStep, right.longitude)
        }
    } else {
        for(point in points){
            val sample = samples.value.find { sample -> sample.latitude == point.latitude && sample.longitude == point.longitude && sample.date == date }

            if(sample!=null)
                Marker(
                    state = rememberMarkerState(position = LatLng(point.latitude, point.longitude)),
                    onClick = {
                            marker ->
                        currentPoint.value = marker.position
                        scope.launch { modalState.show() }
                        true
                    },
                    icon =
                        when {
                            sample.toBeAnalyzed -> {
                                bitmapDescriptorFromVector(context, R.drawable.yellow_round_shape)
                            }

                            (sample.ph > (-1.00)) && (sample.sar > (-1.00)) && (sample.ec > (-1.00)) && (sample.cec > (-1.00)) -> {
                                bitmapDescriptorFromVector(context, R.drawable.green_round_shape)
                            }
                            else -> {
                                bitmapDescriptorFromVector(context, R.drawable.red_round_shape)
                            }

                    }
                )
        }
    }
}

/**
 * Draws a line for 'Draw' mode
 *
 * @param left Aux value
 * @param right Aux value
 * @param shortStep Aux value
 * @param coordinates Map's coordinates
 * @param currentPoint Current point
 * @param modalState Modal bottom sheet state
 * @param scope Coroutine scope
 * @param pointViewModel Point's viewmodel
 * @param map Current map
 * @param context Current context
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DrawLine(
    date: String,
    left: LatLng,
    right: LatLng,
    shortStep: Double,
    coordinates: List<LatLng>,
    currentPoint: MutableState<LatLng>,
    modalState: ModalBottomSheetState,
    scope: CoroutineScope,
    pointViewModel: PointViewModel,
    map: Map,
    context: Context,
){
    var p : LatLng = left
    while (p.longitude < right.longitude) {
        if (PolyUtil.containsLocation(p, coordinates, false)) {
            Marker(
                state = rememberMarkerState(position = p),
                onClick = { marker ->
                    currentPoint.value = marker.position
                    scope.launch { modalState.show() }
                    true
                },
                icon = bitmapDescriptorFromVector(context, R.drawable.red_round_shape),
                draggable = true
            )
            pointViewModel.addPoint(p.latitude, p.longitude, map.name)

            pointViewModel.addSampling(p.latitude, p.longitude, (-1.0), (-1.0), (-1.0), (-1.0), date, map.name)

        }
        p = LatLng(p.latitude, p.longitude + shortStep)
    }
}
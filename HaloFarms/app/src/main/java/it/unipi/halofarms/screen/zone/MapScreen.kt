package it.unipi.halofarms.screen.zone

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.*
import com.google.maps.android.ktx.utils.sphericalDistance
import it.unipi.halofarms.R
import it.unipi.halofarms.navigation.DefaultNavigation
import it.unipi.halofarms.navigation.Dialog
import it.unipi.halofarms.navigation.Navigator
import it.unipi.halofarms.screen.point.Point
import it.unipi.halofarms.screen.point.PointViewModel
import it.unipi.halofarms.ui.theme.HaloFarmsTheme
import it.unipi.halofarms.ui.theme.LightYellow
import it.unipi.halofarms.ui.theme.MediumGreen
import it.unipi.halofarms.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*


private const val TAG = "MapScreen"


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapScreen(
    mapName: String,
    onBackPressed: () -> Unit,
    points: State<List<Point>>,
    map: Map,
    mapsViewModel: MapViewModel,
    pointViewModel: PointViewModel,
    navigateTo: (String) -> Unit,
)  {
    // Coroutine's scope
    val scope = rememberCoroutineScope()
    // Scaffold's state
    val scaffoldState = rememberScaffoldState()
    // Current clicked point
    val currentPoint = remember { mutableStateOf("") }
    // BottomSheetScaffold's state
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()


    Scaffold(
       bottomBar = { BottomAppBarCompose(scope = scope, scaffoldState = scaffoldState, navigateTo = navigateTo, mapName =  mapName) },
       scaffoldState = scaffoldState,
       drawerContent = { PointsList(points = points, mapName = mapName, scope = scope, scaffoldState = scaffoldState, currentPoint = currentPoint, bottomSheetScaffoldState = bottomSheetScaffoldState) },
       drawerGesturesEnabled = true
   ) { padding ->
       BottomSheetScaffold(
           topBar = {
               Row {
                   // Back button
                   Up(onBackPressed)
                   // Decides if the switch should be seen
                   if (map.perimeterPoints == null || map.points == null)
                       SwitchMode(map, mapsViewModel)
               }
           },
           floatingActionButton = {
               Box {
                   if (map.done == "false" || map.done == null) {
                       DoneButton(map.name!!, mapsViewModel, padding, map.mode!!)
                   }
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
           sheetBackgroundColor = White,
           backgroundColor = Color.Transparent,
           content = {
               Body(
                   map,
                   padding,
                   bottomSheetScaffoldState,
                   scope,
                   pointViewModel,
                   points.value.filter { point -> point.zoneName == mapName },
                   currentPoint,
               )
           }
       )
   }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PointsList(
    points: State<List<Point>>,
    mapName: String,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    currentPoint: MutableState<String>,
    bottomSheetScaffoldState: BottomSheetScaffoldState
) {
    // Title
    Text(
        text = "Points",
        fontSize = TextUnit(36F, TextUnitType.Sp),
        modifier = Modifier.padding(18.dp)
    )

    val lazyListState = rememberLazyListState()

    // Points' list
    LazyColumn(state = lazyListState, userScrollEnabled = true) {
        points.value
            .filter { point -> point.zoneName == mapName }
            .forEach { point ->
                // Single point
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
}

@Composable
fun PointRow(
    name: String?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = LightYellow,
        elevation = 0.dp,
        border = BorderStroke(1.dp, MediumGreen)
    ) {
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            Text("Point")
            Text(name!!)
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SheetContent(
    padding: PaddingValues = PaddingValues(),
    scope: CoroutineScope,
    navigateTo: (String) -> Unit,
    scaffoldState: BottomSheetScaffoldState,
    pointViewModel: PointViewModel,
    currentPoint: Point?
) {
    if(currentPoint?.name != null) {
        Box {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    // Title and icon
                    Row {
                        // Title
                        Text("Point", style = MaterialTheme.typography.h4)

                        // Current info point
                        IconButton(
                            onClick = {
                                DefaultNavigation.Point.route(currentPoint.name!!.removePrefix("lat/lng: "))
                                    .let { navigateTo(it) }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.iconchart),
                                contentDescription = stringResource(R.string.ec_chart),
                                tint = HaloFarmsTheme.colors.iconPrimary,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        // Spacer
                        Spacer(
                            modifier = Modifier
                                .padding(horizontal = 83.dp)
                                .padding(padding)
                        )


                        // Close button
                        IconButton(
                            onClick = {
                                scope.launch {
                                    scaffoldState.bottomSheetState.collapse()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = stringResource(R.string.close_the_sheet),
                                tint = HaloFarmsTheme.colors.iconPrimary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    Text(currentPoint.name!!)
                }

                // Divider
                Divider(modifier = Modifier.padding(4.dp), thickness = 1.dp)

                // Options buttons
                Row(modifier = Modifier.padding(4.dp)) {

                    Button(
                        onClick = {
                            pointViewModel.updateAnalyze(currentPoint.name!!, "false")
                            pointViewModel.updateToBeAnalyzed(currentPoint.name!!, "true")
                        },
                        shape = RoundedCornerShape(16.dp),
                        content = {
                            Text(stringResource(R.string.to_be_analyzed),
                                color = White)
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = HaloFarmsTheme.colors.iconPrimary),
                    )

                    // Spacer
                    Spacer(Modifier.padding(8.dp))

                    Button(
                        onClick = {
                            pointViewModel.updateToBeAnalyzed(currentPoint.name!!, "false")
                            pointViewModel.updateAnalyze(currentPoint.name!!, "false")
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = White),
                        border = BorderStroke(2.dp, HaloFarmsTheme.colors.iconPrimary),
                        content = {
                            Text(stringResource(R.string.do_not_analyze),
                                color = HaloFarmsTheme.colors.iconPrimary)
                        }
                    )
                }

                // Spacer
                Spacer(Modifier.padding(8.dp))

                // Divider
                Divider(modifier = Modifier.padding(4.dp), thickness = 1.dp)

                // Spacer
                Spacer(Modifier.padding(8.dp))

                Button(
                    onClick = {
                        Dialog.PointOptions.route(currentPoint.name!!.removePrefix("lat/lng: "))
                            .let { navigateTo(it) }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = HaloFarmsTheme.colors.iconPrimary),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    content = { Text(stringResource(R.string.add_new_values), color = White) }
                )

                // Spacer
                Spacer(Modifier.padding(8.dp))

                // Divider
                Divider(modifier = Modifier.padding(4.dp), thickness = 1.dp)

                // Spacer
                Spacer(Modifier.padding(8.dp))

                // QR code
                //todo: non mi pare che serva questo in un qr point, dovrebbe dare le ultime analisi ? non lo so
                Box {
                    Image(
                        bitmap = textToImage(currentPoint.toString())!!.asImageBitmap(),
                        contentDescription = "QR code",
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Body(
    map: Map,
    padding: PaddingValues,
    scaffoldState: BottomSheetScaffoldState,
    scope: CoroutineScope,
    pointViewModel: PointViewModel,
    points: List<Point>,
    currentPoint: MutableState<String>,
) {
    // Aux value
    val latLng = remember { mutableStateOf("") }
    // Destination
    val destination = LatLng(map.latitude!!.toDouble(), map.longitude!!.toDouble())
    // Camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(destination, 18f)
    }
    val area = remember { mutableStateOf(0.0) }
    val perimeterPoints:MutableList<LatLng> = mutableListOf()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = MapProperties(isMyLocationEnabled = true, mapType = MapType.HYBRID),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(indoorLevelPickerEnabled = true),
            onMapClick = { latlng ->
                scope.launch {
                    latLng.value = latlng.toString().removePrefix("lat/lng: ")
                    if (map.done == "false" || map.done == null) {
                        pointViewModel.addPerimeterPoint(latLng.value, map.name!!)
                    } else {
                        pointViewModel.addPoint(latLng.value, map.name!!)
                    }
                }
            }
        ) {

            DrawPerimeter(map = map, currentPoint = currentPoint, scope = scope, area = area, done = map.done, perimeterPoints = perimeterPoints)

            if(map.done == "true") {
                if (map.mode == "1") {
                    HandfreeSamplingPoints(points, scope, scaffoldState, currentPoint, perimeterPoints)
                } else {
                    DrawSamplingPoints(map, currentPoint, area, scope, scaffoldState, pointViewModel, points, perimeterPoints)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DrawSamplingPoints(
    map: Map,
    currentPoint: MutableState<String>,
    area: MutableState<Double>,
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    pointViewModel: PointViewModel,
    points: List<Point>,
    perimeterPoints: MutableList<LatLng>
) {
    val context = LocalContext.current

    if(map.points == null) {
        /* Gets all the perimeter's points */
        val builder: LatLngBounds.Builder = LatLngBounds.builder()
        for (latLng in perimeterPoints) {
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
            drawLine(
                left = left,
                right = right,
                shortStep = shortStep,
                coordinates = perimeterPoints,
                currentPoint = currentPoint,
                scaffoldState =  scaffoldState,
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
            val latlngAux = LatLng(
                point.name!!.substringAfter("(").substringBefore(",").toDouble(),
                point.name!!.substringAfter(",").substringBefore(")").toDouble()
            )
            Marker(
                state = rememberMarkerState(position = latlngAux),
                onClick = {
                        marker ->
                            scope.launch {
                                currentPoint.value =
                                    marker.position.toString().removePrefix("lat/lng: ")
                                scaffoldState.bottomSheetState.expand()
                            }
                            true
                          },
                icon =
                    if (point.analyzed != null && (point.analyzed!! == "true")) {
                        bitmapDescriptorFromVector(context, R.drawable.green_round_shape)
                    } else if (point.analyzed != null && (point.analyzed!! == "false" && point.toBeAnalyzed!! == "true")) {
                        bitmapDescriptorFromVector(context, R.drawable.yellow_round_shape)
                    } else {
                        bitmapDescriptorFromVector(context, R.drawable.red_round_shape)
                    },
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun drawLine(
    left: LatLng,
    right: LatLng,
    shortStep: Double,
    coordinates: List<LatLng>,
    currentPoint: MutableState<String>,
    scaffoldState: BottomSheetScaffoldState,
    scope: CoroutineScope,
    pointViewModel: PointViewModel,
    map: Map,
    context: Context
){
    var p : LatLng = left
    while (p.longitude < right.longitude) {
        if (PolyUtil.containsLocation(p, coordinates, false)) {
            pointViewModel.addPoint(p.toString().removePrefix("lat/lng: "), map.name!!)
            Marker(
                state = rememberMarkerState(position = p),
                onClick = { marker ->
                    scope.launch {
                        currentPoint.value = marker.position.toString().removePrefix("lat/lng: ")
                        scaffoldState.bottomSheetState.expand()
                    }
                    true
                          },
                icon = bitmapDescriptorFromVector(context, R.drawable.red_round_shape)
                )
        }
        p = LatLng(p.latitude, p.longitude + shortStep)
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HandfreeSamplingPoints(
    points: List<Point>,
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    currentPoint: MutableState<String>,
    perimeterPoints: MutableList<LatLng>
) {
    val latlngArray: MutableList<LatLng> = mutableListOf()

    for(p in points) {
        val latlngAux = LatLng(
            p.name!!.substringAfter("(").substringBefore(",").toDouble(),
            p.name!!.substringAfter(",").substringBefore(")").toDouble()
        )

        val context = LocalContext.current

        if(PolyUtil.containsLocation(latlngAux, perimeterPoints, false)) {
            Marker(
                state = rememberMarkerState(position = latlngAux),
                icon =
                if (p.analyzed != null && (p.analyzed!! == "true")) {
                    bitmapDescriptorFromVector(context, R.drawable.green_round_shape)
                } else if (p.analyzed != null && (p.analyzed!! == "false" && p.toBeAnalyzed!! == "true")) {
                    bitmapDescriptorFromVector(context, R.drawable.yellow_round_shape)
                } else {
                    bitmapDescriptorFromVector(context, R.drawable.red_round_shape)
                },
                onClick = { marker ->
                    scope.launch {
                        currentPoint.value = marker.position.toString().removePrefix("lat/lng: ")
                        scaffoldState.bottomSheetState.expand()
                    }
                    true
                }
            )
            latlngArray.add(latlngAux)
        }
    }
}

/**
 *
 * @param map
 * @param currentPoint
 * @param scope
 * @param area
 * @param done
 * @param perimeterPoints
 */
@Composable
fun DrawPerimeter(
    map: Map,
    currentPoint: MutableState<String>,
    scope: CoroutineScope,
    area: MutableState<Double>,
    done: String?,
    perimeterPoints: MutableList<LatLng>,
) {
    if(map.perimeterPoints != null) {
        for (p in map.perimeterPoints!!) {
            val latlngAux = LatLng(
                p.substringAfter("(").substringBefore(",").toDouble(),
                p.substringAfter(",").substringBefore(")").toDouble()
            )
            Marker(
                state = rememberMarkerState(position = latlngAux),
                onClick = { marker ->
                    scope.launch {
                        currentPoint.value =
                            marker.position.toString().removePrefix("lat/lng: ")
                    }
                    true
                },
                visible = done == "false" || done == null
            )

            perimeterPoints.add(latlngAux)
        }
    }
    if(perimeterPoints.size > 0) {
        area.value = SphericalUtil.computeArea(perimeterPoints) / 10000
        sortVerticies(perimeterPoints)
        Polygon(points = perimeterPoints, fillColor = Color.Transparent, visible = true)
    }
}

/**
 *
 * @param scope
 * @param scaffoldState
 * @param navigateTo
 * @param mapName
 */
@Composable
fun BottomAppBarCompose(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navigateTo: (String) -> Unit,
    mapName: String,
) {
    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0xFFF0EDDD),
        cutoutShape = CircleShape,
        content =
        {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                    IconButton(
                        onClick = {
                            scope.launch { scaffoldState.drawerState.open() }
                        }
                    ) {
                        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.List,
                                contentDescription = "List icon"
                            )
                            Text(stringResource(R.string.points))
                        }
                    }

                val map = painterResource(id = R.drawable.mappa)
                IconButton(
                    onClick = {
                        scope.launch { scaffoldState.drawerState.close() }
                        Navigator.Map.route(mapName).let { navigateTo(it) }
                    }
                ) {
                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = map,
                            modifier = Modifier.size(20.dp),
                            contentDescription = null
                        )
                        Text(stringResource(R.string.Map))
                    }
                }

                val heatmap = painterResource(id = R.drawable.heatmap)
                IconButton(onClick = {
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                    Dialog.ChooseHeatmap.route(mapName). let { navigateTo(it) }
                }) {
                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

                    Icon(
                        painter = heatmap,
                        modifier = Modifier.size(20.dp),
                        contentDescription = "Heatmap icon"
                    )
                        Text(stringResource(R.string.heatmap))
                    }
                }
            }
        }
    )
}

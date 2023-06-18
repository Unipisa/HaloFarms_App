package it.unipi.halofarms.screen

import android.graphics.PointF
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import it.unipi.halofarms.R
import it.unipi.halofarms.data.FirestoreProxy
import it.unipi.halofarms.navigation.DefaultNavigation
import it.unipi.halofarms.navigation.Dialog
import it.unipi.halofarms.navigation.Navigator
import it.unipi.halofarms.screen.home.HomeScreen
import it.unipi.halofarms.screen.point.Point
import it.unipi.halofarms.screen.point.PointViewModel
import it.unipi.halofarms.screen.zone.HeatMapScreen
import it.unipi.halofarms.screen.zone.MapScreen
import it.unipi.halofarms.screen.zone.MapViewModel
import it.unipi.halofarms.ui.theme.HaloFarmsTheme
import it.unipi.halofarms.ui.theme.Neutral0
import it.unipi.halofarms.ui.theme.Neutral2
import it.unipi.halofarms.util.locationPermissionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
@ExperimentalMaterialApi
fun HaloFarmsApp() {
    HaloFarmsTheme {
        Surface(color = MaterialTheme.colors.background) {
            val navController = rememberNavController()
            // Proxy
            val proxy = FirestoreProxy()
            // Gets map's viewmodel
            val mapsViewModel = MapViewModel(proxy)
            // Gets the maps in firestore
            val maps = mapsViewModel.maps.collectAsStateWithLifecycle(initialValue = emptyList())
            // Checks and gets location permissions
            val locationPermission: MultiplePermissionsState = locationPermissionState()
            // Coroutine's scope
            val scope = rememberCoroutineScope()
            // Gets point's viewmodel
            val pointViewModel = PointViewModel(proxy)
            val points = pointViewModel.points.collectAsStateWithLifecycle(initialValue = emptyList())

            NavHost(navController, startDestination = DefaultNavigation.Home.route) {
                /* Brings to the home screen */
                composable(DefaultNavigation.Home.route) {
                    HomeScreen(locationPermission, maps) { route, clearBackStack ->
                        navController.navigate(route = route) {
                            if (clearBackStack) popUpTo(route) { inclusive = false }
                        }
                    }
                }

                /* Brings to the map screen */
                composable(Navigator.Map.route) {
                    val mapName = it.arguments?.getString(Navigator.Map.ARG_MAP_ID).orEmpty()
                    MapScreen(
                        mapName = mapName,
                        onBackPressed = { navController.navigateUp() },
                        pointViewModel = pointViewModel,
                        mapsViewModel = mapsViewModel,
                        points = points,
                        map = maps.value.find { map -> map.name == mapName }!!,
                    ){ route ->
                        navController.navigate(route = route) { popUpTo(route) }
                    }
                }

                composable(Navigator.HeatMap.route) {
                    val mapName = it.arguments?.getString(Navigator.HeatMap.ARG_MAP_ID).orEmpty()
                    val mode = it.arguments?.getString(Navigator.HeatMap.ARG_MODE_ID).orEmpty()
                    HeatMapScreen(
                        maps.value.find { map -> map.name == mapName }!!,
                        mode,
                        mapName,
                        {navController.navigateUp()},
                        points,
                        mapsViewModel,
                        pointViewModel
                    ){ route ->
                        navController.navigate(route = route) { popUpTo(route) }
                    }
                }


                dialog(
                    Dialog.ChooseHeatmap.route,
                    dialogProperties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                    )
                ) {
                    val mapName = it.arguments?.getString(Dialog.ChooseHeatmap.ARG_MAP_ID).orEmpty()

                    ChooseHeatMap(
                        mapName,
                    ) { route ->
                        navController.navigate(route = route) { popUpTo(route) }
                    }
                }

                /* Brings to the point screen */
                dialog(
                    DefaultNavigation.Point.route,
                    dialogProperties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                    )
                ) {
                    val pointName = it.arguments?.getString(DefaultNavigation.Point.ARG_POINT_ID).orEmpty()

                    PointsChart(points.value, pointName, navController)
                }

                /* Brings to the 'DeleteMap' dialog */
                dialog(
                    Dialog.LongMap.route,
                    dialogProperties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                    )
                ){
                    // Gets the zone name
                    val mapName = it.arguments?.getString(Dialog.LongMap.ARG_MAP_ID).orEmpty()

                    // Deletes a map
                    DeleteMap(mapName, mapsViewModel, navController)
                }

                /* Brings to the 'DeletePoint' dialog */
                dialog(
                    Dialog.LongPoint.route,
                    dialogProperties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                    )){
                    // Gets the point name
                    val pointName = it.arguments?.getString(Dialog.LongPoint.ARG_POINT_ID).orEmpty()
                    // Gets the map name
                    val mapName = it.arguments?.getString(Dialog.LongPoint.ARG_MAP_ID).orEmpty()

                    // Deletes a point
                    DeletePoint(pointName, mapName, pointViewModel, navController)
                }

                /* Brings to the 'AddMap' dialog */
                dialog(
                    Dialog.AddMap.route,
                    dialogProperties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                    )){

                    // Adds a map
                    AddDialog(mapsViewModel, navController, locationPermission)
                }

                /* Brings to the 'PointOptions' dialog */
                dialog(
                    Dialog.PointOptions.route,
                    dialogProperties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                    )){
                    // Point's name
                    val pointName = it.arguments?.getString(Dialog.PointOptions.ARG_POINT_ID).orEmpty()

                    // Adds new values for the current point
                    NewValuesForPoint(pointViewModel, pointName, navController, scope)
                }
            }
        }
    }
}

@Composable
fun ChooseHeatMap(
    mapName: String,
    navigateTo: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .padding(65.dp)
                .border(20.dp, Color.Transparent, MaterialTheme.shapes.small)
                .align(Alignment.Center),
            shape = androidx.compose.material3.MaterialTheme.shapes.extraLarge,
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = { Navigator.HeatMap.route(mapName, "ec").let { navigateTo(it) } }
                ) {
                    Text(text = "EC")
                }
                // Spacer
                Spacer(modifier = Modifier.padding(4.dp))
                Button(onClick = {
                    Navigator.HeatMap.route(mapName, "cec").let { navigateTo(it) }
                }) {
                    Text(text = "CEC")
                }
                // Spacer
                Spacer(modifier = Modifier.padding(4.dp))
                Button(onClick = {
                    Navigator.HeatMap.route(mapName, "ph").let { navigateTo(it) }
                }) {
                    Text(text = "PH")
                }
                // Spacer
                Spacer(modifier = Modifier.padding(4.dp))
                Button(onClick = {
                    Navigator.HeatMap.route(mapName, "sar").let { navigateTo(it) }
                }) {
                    Text(text = "SAR")
                }
                // Spacer
                Spacer(modifier = Modifier.padding(4.dp))

                Text(stringResource(R.string.warning_choose_menu), fontStyle = FontStyle.Italic)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PointsChart(points: List<Point>, pointName: String, navController: NavHostController) {
    Column(modifier = Modifier.padding(vertical = 166.dp)) {
        Box(
            Modifier
                .background(Neutral0, RoundedCornerShape(10.dp))
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {

            Row(modifier = Modifier.align(Alignment.TopEnd)) {
                // Close button
                IconButton(
                    onClick = {
                        navController.navigateUp()
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
            val lazyListState = rememberLazyListState()
            points.forEach { point ->
                if (point.name == pointName) {
                    LazyColumn(
                        modifier = Modifier.padding(vertical = 36.dp, horizontal = 20.dp),
                        state = lazyListState,
                        userScrollEnabled = true
                    ) {
                        item {
                            Card(
                                modifier = Modifier
                                    .background(
                                        Neutral2,
                                        RoundedCornerShape(15.dp)
                                    )
                            ) {
                                Column {
                                    // History of ec values
                                    Text(
                                        text = "EC values history",
                                        fontSize = TextUnit(16.0F, TextUnitType.Sp),
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(16.dp)
                                    )

                                    if (point.ecList != null && point.dateList != null) {
                                        Graph(point.ecList!!, point.dateList!!)
                                    } else {
                                        // Error message
                                        Text(
                                            text = "Values are null",
                                            fontSize = TextUnit(12.0F, TextUnitType.Sp),
                                            textAlign = TextAlign.Center,
                                            fontStyle = FontStyle.Italic)
                                    }

                                    // History of ph values
                                    Text(
                                        text = "PH values history",
                                        fontSize = TextUnit(16.0F, TextUnitType.Sp),
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(16.dp)
                                    )
                                    if(point.phList != null && point.dateList != null) {
                                        Graph(point.phList!!, point.dateList!!)
                                    } else {
                                        // Error message
                                        Text(
                                            text = "Values are null",
                                            fontSize = TextUnit(12.0F, TextUnitType.Sp),
                                            textAlign = TextAlign.Center,
                                            fontStyle = FontStyle.Italic)
                                    }


                                    // History of cec values
                                    Text(
                                        text = "CEC values history",
                                        fontSize = TextUnit(16.0F, TextUnitType.Sp),
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(16.dp)
                                    )
                                    if(point.cecList != null && point.dateList != null) {
                                        Graph(point.cecList!!, point.dateList!!)
                                    } else {
                                        // Error message
                                        Text(
                                            text = "Values are null",
                                            fontSize = TextUnit(12.0F, TextUnitType.Sp),
                                            textAlign = TextAlign.Center,
                                            fontStyle = FontStyle.Italic)
                                    }

                                    // History of sar values
                                    Text(
                                        text = "SAR values history",
                                        fontSize = TextUnit(16.0F, TextUnitType.Sp),
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(16.dp)
                                    )

                                    if(point.sarList != null && point.dateList != null) {
                                        Graph(point.sarList!!, point.dateList!!)
                                    } else {
                                        // Error message
                                        Text(
                                            text = "Values are null",
                                            fontSize = TextUnit(12.0F, TextUnitType.Sp),
                                            textAlign = TextAlign.Center,
                                            fontStyle = FontStyle.Italic
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Graph(list: List<String>, dateList: List<String>) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val floatElementList: ArrayList<Float> = arrayListOf()
    val floatDateList: ArrayList<Float> = arrayListOf()

    for(e in list){
        for(d in dateList){
            floatElementList.add(e.toFloat())
            floatDateList.add(LocalDate.parse(d, formatter).dayOfMonth.toFloat())
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .safeDrawingPadding()
    ) {
        val distance = size.width / (floatElementList.size + 1)
        var currentX = 0F
        val maxValue = floatElementList.maxOrNull() ?: 0F
        val points = mutableListOf<PointF>()

        floatElementList.forEachIndexed { index, data ->
            if (floatElementList.size >= index + 2) {
                val y0 = (maxValue - data) * (size.height / maxValue)
                val x0 = currentX + distance
                points.add(PointF(x0, y0))
                currentX += distance
            }
        }

        for (i in 0 until points.size - 1) {
            drawLine(
                start = Offset(points[i].x, points[i].y),
                end = Offset(points[i + 1].x, points[i + 1].y),
                color = Color(0xFF3F51B5),
                strokeWidth = 8f,
                cap = StrokeCap.Butt
            )
        }
    }
}

@Composable
fun NewValuesForPoint(
    viewmodel: PointViewModel,
    pointName: String,
    navController: NavHostController,
    scope: CoroutineScope,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Neutral0, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(stringResource(R.string.add_new_values))
                // Inserts new value for ph
                OutlinedTextField(
                    value = viewmodel.newPh,
                    onValueChange = { newPh -> viewmodel.updateNewPh(newPh) },
                    placeholder = { Text("PH") },
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(24.dp)
                )
                // Inserts new value for cec
                OutlinedTextField(
                    value = viewmodel.newCec,
                    onValueChange = { newCec -> viewmodel.updateNewCec(newCec) },
                    placeholder = { Text("CEC") },
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(24.dp)
                )
                // Inserts new value for ec
                OutlinedTextField(
                    value = viewmodel.newEc,
                    onValueChange = { newEc -> viewmodel.updateNewEc(newEc) },
                    placeholder = { Text("EC") },
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(24.dp)
                )
                // Inserts new value for sar
                OutlinedTextField(
                    value = viewmodel.newSar,
                    onValueChange = { newSar -> viewmodel.updateNewSar(newSar) },
                    placeholder = { Text("SAR") },
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(24.dp)
                )

                // Inserts new value for date
                OutlinedTextField(
                    value = viewmodel.newDate,
                    onValueChange = { newDate -> viewmodel.updateNewdate(newDate) },
                    placeholder = { Text("dd/mm/yyyy") },
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(24.dp)
                )
            }
            // Dismiss button
            Row(modifier = Modifier.align(Alignment.End)) {
                Button(
                    onClick = {
                        scope.launch {
                            viewmodel.addSampling(
                                pointName,
                                viewmodel.newPh,
                                viewmodel.newCec,
                                viewmodel.newEc,
                                viewmodel.newSar,
                                viewmodel.newDate)
                            viewmodel.updateAnalyze(pointName, "true")
                            viewmodel.updateNewCec("")
                            viewmodel.updateNewPh("")
                            viewmodel.updateNewEc("")
                            viewmodel.updateNewSar("")
                            viewmodel.updateNewdate("")
                        }
                        navController.navigateUp()
                    },
                    content = { Text(text = "OK") }
                )
            }
        }
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddDialog(
    viewmodel: MapViewModel,
    navController: NavHostController,
    locationPermission: MultiplePermissionsState
) {
    // Coroutine's scope
    val scope = rememberCoroutineScope()
    // Context
    val context = LocalContext.current

    var plc by remember { mutableStateOf("") }

    // Intent for places
    /*val startAutocomplete =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)

                    plc = place.latLng?.toString()?.removePrefix("lat/lng: ") ?: ""
                    viewmodel.addNewMap(viewmodel.newName, plc.substringAfter("(").substringBefore(","), plc.substringAfter(",").substringBefore(")"), context)
                    viewmodel.updateNewName("")
                    navController.navigateUp()
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {

            }
        }

    if (!Places.isInitialized()) {
        Places.initialize(context, "AIzaSyCHpTWIZBF87BIbSY4CZSCnmWJjnYAgktg", Locale.ROOT)
    }*/

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .padding(2.dp)
                .border(16.dp, Color.Transparent, MaterialTheme.shapes.small),
            shape = androidx.compose.material3.MaterialTheme.shapes.extraLarge,
        ) {

            Column(modifier = Modifier.padding(24.dp)) {
                // Title
                Text(text = stringResource(R.string.new_map), fontSize = TextUnit(18.0F, TextUnitType.Sp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))

                // Spacer
                Spacer(modifier = Modifier.padding(4.dp))

                // Form to fill
                OutlinedTextField(
                    value = viewmodel.newName,
                    onValueChange = { newValue -> viewmodel.updateNewName(newValue) },
                    placeholder = { Text( stringResource(R.string.name))},
                    modifier = Modifier.padding(16.dp)
                )

                // Spacer
                Spacer(modifier = Modifier.padding(2.dp))

                Text("From", modifier = Modifier.padding(horizontal = 16.dp).align(Alignment.CenterHorizontally))

                Spacer(modifier = Modifier.padding(2.dp))

                // Position button
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        viewmodel.addNewMap(viewmodel.newName, null, null, context)

                        if(!locationPermission.allPermissionsGranted){
                            locationPermission.launchMultiplePermissionRequest()
                        }
                        viewmodel.updateNewName("")
                        navController.navigateUp()
                    },
                    content = { Text(stringResource(R.string.my_position)) }
                )

                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        val fields = listOf(Place.Field.LAT_LNG)

                        // Start the autocomplete intent.
                        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(context)
                        //startAutocomplete.launch(intent)
                    }
                ) {
                    Text(stringResource(R.string.address))
                }

                // Spacer
                Spacer(modifier = Modifier.padding(8.dp))
            }
        }
    }
}

/**
 * Creates the DeleteMap dialog
 *
 * @param mapName Map's name
 * @param viewmodel Map's viewmodel
 * @param navController Navigation controller
 */
@Composable
fun DeleteMap(mapName: String, viewmodel: MapViewModel, navController: NavHostController) {
    // Coroutine's scope
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .padding(2.dp)
                .border(16.dp, Color.Transparent, MaterialTheme.shapes.small)
                .align(Alignment.Center),
            shape = androidx.compose.material3.MaterialTheme.shapes.extraLarge,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.do_you_want_to_delete))

                Spacer(modifier = Modifier.padding(8.dp))

                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Button(
                        onClick = {
                            scope.launch {
                                viewmodel.deleteMap(mapName)
                            }
                            navController.navigateUp()
                        }
                    ) {
                        Text(text = "Yes")
                    }

                    Spacer(modifier = Modifier.padding(32.dp))

                    Button(
                        onClick = { navController.navigateUp() }
                    ) {
                        Text(text = "No")
                    }

                }
            }
        }
    }
}

/**
 * Creates the DeletePoint dialog
 *
 * @param pointName Point's name
 * @param viewmodel Point's viewmodel
 * @param navController Navigation controller
 */
@Composable
fun DeletePoint(
    pointName: String,
    mapName: String,
    viewmodel: PointViewModel,
    navController: NavHostController
) {
    // Coroutine's scope
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .border(16.dp, Color.Transparent, MaterialTheme.shapes.small)
                .align(Alignment.Center),
            shape = androidx.compose.material3.MaterialTheme.shapes.extraLarge,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                //Title
                Text(stringResource(R.string.do_you_want_to_delete) + "${pointName}?")

                // Spacer
                Spacer(modifier = Modifier.padding(8.dp))

                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    // Positive button
                    Button(
                        onClick = {
                            scope.launch {
                                viewmodel.deletePoint(pointName, mapName)
                            }
                            navController.navigateUp()
                        }
                    ) {
                        Text(text = "Yes")
                    }

                    // Spacer
                    Spacer(modifier = Modifier.padding(32.dp))

                    // Negative button
                    Button(
                        onClick = { navController.navigateUp() }
                    ) {
                        Text(text = "No")
                    }

                }
            }
        }
    }
}

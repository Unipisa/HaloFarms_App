package it.unipi.halofarms.util

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Build
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import it.unipi.halofarms.BottomIcons
import it.unipi.halofarms.R
import it.unipi.halofarms.data.perimeterPoint.PerimeterPoint
import it.unipi.halofarms.navigation.Dialog
import it.unipi.halofarms.navigation.ScreenNavigator
import it.unipi.halofarms.screen.map.MapViewModel
import it.unipi.halofarms.screen.map.PointViewModel
import it.unipi.halofarms.ui.theme.Neutral0
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

/**
 * Displays the History dialog, where the user can go through the old samples
 *
 * @param mapName Map's name
 * @param pointViewModel Point's viewmodel
 * @param navigateTo Navigation function
 */
@Composable
fun ChooseDate(mapName: String, pointViewModel: PointViewModel, navigateTo: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .padding(2.dp)
                .border(16.dp, Color.Transparent, MaterialTheme.shapes.small),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Title
                Text(text = stringResource(R.string.choose_your_timestamp), fontSize = TextUnit(18.0F, TextUnitType.Sp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, modifier = Modifier.align(
                    Alignment.CenterHorizontally))

                Spacer(modifier = Modifier.padding(4.dp))

                val context = LocalContext.current
                val calendar = Calendar.getInstance()

                // Fetching current year Month and day
                val year = calendar[Calendar.YEAR]
                val month = calendar[Calendar.MONTH]
                val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]

                var selectedDateText by remember { mutableStateOf("$dayOfMonth-${month + 1}-$year") }

                val datePicker = DatePickerDialog(
                    context,
                    { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                        selectedDateText = "$selectedDayOfMonth-${selectedMonth + 1}-$selectedYear"
                    }, year, month, dayOfMonth
                )

                Box(modifier = Modifier) {
                    OutlinedTextField(value = selectedDateText, onValueChange = {}, readOnly = true, label = { Text("dd-m-yyyy") })
                    if (selectedDateText.isNotEmpty()) {
                        pointViewModel.updateNewdate(selectedDateText)
                    }
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .alpha(0f)
                            .clickable(onClick = { datePicker.show() }),
                    )
                }

                // Dismiss button
                Row(modifier = Modifier.align(Alignment.End)) {
                    Button(
                        onClick = {
                            ScreenNavigator.Map.route(mapName,selectedDateText).let { navigateTo(it) }

                                  },
                        content = { Text(text = "OK") }
                    )
                }
            }
        }
    }
}

/**
 * Displays the Heatmap dialog, where the user can choose which heatmap will be shown
 *
 * @param mapName Map's name
 * @param date Current date
 * @param navigateTo Navigation function
 */
@Composable
fun ChooseHeatMap(
    mapName: String,
    date: String,
    navigateTo: (String) -> Unit
) {
    Box{
        Surface(
            modifier = Modifier
                .border(20.dp, Color.Transparent, MaterialTheme.shapes.small)
                .align(Alignment.Center),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.choose_your_heatmap), fontSize = TextUnit(18.0F, TextUnitType.Sp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, modifier = Modifier.align(
                    Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.padding(4.dp))
                Button(
                    onClick = { ScreenNavigator.HeatMap.route(mapName, "ec", date).let { navigateTo(it) } }
                ) {
                    Text(text = "EC")
                }
                // Spacer
                Spacer(modifier = Modifier.padding(4.dp))
                Button(onClick = {
                    ScreenNavigator.HeatMap.route(mapName, "cec", date).let { navigateTo(it) }
                }) {
                    Text(text = "CEC")
                }
                // Spacer
                Spacer(modifier = Modifier.padding(4.dp))
                Button(onClick = {
                    ScreenNavigator.HeatMap.route(mapName, "ph", date).let { navigateTo(it) }
                }) {
                    Text(text = "PH")
                }
                // Spacer
                Spacer(modifier = Modifier.padding(4.dp))
                Button(onClick = {
                    ScreenNavigator.HeatMap.route(mapName, "sar", date).let { navigateTo(it) }
                }) {
                    Text(text = "SAR")
                }
                // Spacer
                Spacer(modifier = Modifier.padding(4.dp))

            }
        }
    }
}

/**
 * Creates the 'AddNewValuesForPoint' dialog
 *
 * @param viewmodel Point's viewmodel
 * @param pointName Point's name
 * @param mapName Map's name
 * @param scope Coroutine scope
 * @param navController
 */
@Composable
fun NewValuesForPoint(
    viewmodel: PointViewModel,
    pointName: String,
    mapName: String,
    scope: CoroutineScope,
    navController: NavHostController,
) {
    val context = LocalContext.current
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

                Text(stringResource(R.string.add_new_values), textAlign = TextAlign.Start, fontSize = TextUnit(24F, TextUnitType.Sp))

                // Inserts new value for ph
                OutlinedTextField(
                    value = viewmodel.newPh,
                    label = { Text("PH") },
                    onValueChange = { newPh -> viewmodel.updateNewPh(newPh) },
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(8.dp)
                )
                // Inserts new value for cec
                OutlinedTextField(
                    value = viewmodel.newCec,
                    onValueChange = { newCec -> viewmodel.updateNewCec(newCec) },
                    label = { Text("CEC") },
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(8.dp)
                )
                // Inserts new value for ec
                OutlinedTextField(
                    value = viewmodel.newEc,
                    onValueChange = { newEc -> viewmodel.updateNewEc(newEc) },
                    label = { Text("EC") },
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(8.dp)
                )
                // Inserts new value for sar
                OutlinedTextField(
                    value = viewmodel.newSar,
                    onValueChange = { newSar -> viewmodel.updateNewSar(newSar) },
                    label = { Text("SAR") },
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(8.dp)
                )
                // Inserts new value for the date
                DatePicker(modifier = Modifier.padding(16.dp), pointViewModel = viewmodel)
            }
            // Dismiss button
            Row(modifier = Modifier.align(Alignment.End)) {
                Button(
                    onClick = {
                        viewmodel.addSampling(
                            pointName.substringBefore(",").toDouble(),
                            pointName.substringAfter(",").toDouble(),
                            viewmodel.newPh.toDouble(),
                            viewmodel.newCec.toDouble(),
                            viewmodel.newEc.toDouble(),
                            viewmodel.newSar.toDouble(),
                            viewmodel.newDate,
                            mapName
                        )

                        scope.launch {
                            viewmodel.updateNewCec("")
                            viewmodel.updateNewPh("")
                            viewmodel.updateNewEc("")
                            viewmodel.updateNewSar("")
                            viewmodel.updateNewdate("")
                        }

                        navController.navigateUp()

                        toaster(scope, context, R.string.new_values_added)
                    },
                    content = { Text(text = "OK") }
                )
            }
        }
    }

}

/**
 * Creates the AddMap dialog
 *
 * @param viewmodel Map's viewmodel
 * @param navController Navigator helper
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddDialog(
    viewmodel: MapViewModel,
    navController: NavHostController
)  {
    // Coroutine's scope
    val scope = rememberCoroutineScope()
    // Context
    val context = LocalContext.current

    var plc by remember { mutableStateOf("") }

    // Intent for places
    val startAutocomplete =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)

                    plc = place.latLng?.toString()?.removePrefix("lat/lng: ") ?: ""

                    viewmodel.addNewMap(
                        scope,
                        viewmodel.newName,
                        plc.substringAfter("(").substringBefore(",").toDouble(),
                        plc.substringAfter(",").substringBefore(")").toDouble(),
                        context
                    )
                    toaster(scope, context, R.string.added_new_map)
                    viewmodel.updateNewName("")

                    scope.launch {
                        navController.navigateUp()
                    }
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                toaster(scope, context, R.string.cannot_find_this_address)
                viewmodel.updateNewName("")
            }
        }

    if (!Places.isInitialized()) {
        Places.initialize(context, stringResource(id = R.string.maps_api_key), Locale.ROOT)
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .padding(2.dp)
                .border(16.dp, Color.Transparent, MaterialTheme.shapes.small),
            shape = MaterialTheme.shapes.extraLarge,
        ) {

            Column(modifier = Modifier.padding(24.dp)) {
                // Title
                Text(text = stringResource(R.string.new_map), fontSize = TextUnit(18.0F, TextUnitType.Sp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, modifier = Modifier.align(
                    Alignment.CenterHorizontally))

                // Spacer
                Spacer(modifier = Modifier.padding(4.dp))

                // Form to fill
                OutlinedTextField(
                    value = viewmodel.newName,
                    onValueChange = { newValue -> viewmodel.updateNewName(newValue) },
                    placeholder = { Text( stringResource(R.string.name)) },
                    modifier = Modifier.padding(16.dp)
                )

                // Spacer
                Spacer(modifier = Modifier.padding(2.dp))

                Text("From", modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally))

                Spacer(modifier = Modifier.padding(2.dp))

                // Position button
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        if(viewmodel.newName != "") {
                            viewmodel.addNewMap(scope, viewmodel.newName, null, null, context)
                            viewmodel.updateNewName("")
                            scope.launch {
                                navController.navigateUp()
                            }
                        } else {
                            toaster(scope, context, R.string.cannot_find_this_address)
                            viewmodel.updateNewName("")
                        }

                    },
                    content = { Text(stringResource(R.string.my_position)) }
                )

                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        if(viewmodel.newName != "") {
                            val fields = listOf(Place.Field.LAT_LNG)

                            // Start the autocomplete intent.
                            val intent = Autocomplete.IntentBuilder(
                                AutocompleteActivityMode.FULLSCREEN,
                                fields
                            )
                                .build(context)
                            startAutocomplete.launch(intent)
                        } else {
                            toaster(scope, context, R.string.cannot_add_name_is_null)
                            viewmodel.updateNewName("")
                        }
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
 * @param perimeterPoints Perimeter points
 */
@Composable
fun DeleteMap(mapName: String, viewmodel: MapViewModel, navController: NavHostController, perimeterPoints: List<PerimeterPoint>) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .padding(2.dp)
                .border(16.dp, Color.Transparent, MaterialTheme.shapes.small)
                .align(Alignment.Center),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.do_you_want_to_delete) + " $mapName?")

                Spacer(modifier = Modifier.padding(8.dp))

                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Button(
                        onClick = {
                            viewmodel.deleteMap(mapName, perimeterPoints)
                            
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
 * Shows bottom bar in map screen
 *
 * @param scope Coroutine scope
 * @param navigateTo Navigation function
 * @param mapName Map's name
 * @param date Current date
 * @param selected Selected button
 */
@Composable
fun BottomAppBarCompose(
    scope: CoroutineScope,
    navigateTo: (String) -> Unit,
    mapName: String,
    date: String,
    selected: MutableState<BottomIcons>
) {
    androidx.compose.material.BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.White,
        cutoutShape = CircleShape,
        elevation = 20.dp,
        content =
        {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                androidx.compose.material.IconButton(
                    onClick = {
                        selected.value = BottomIcons.HISTORY
                        scope.launch {
                            ScreenNavigator.History.route(mapName).let { navigateTo(it) }
                        }
                    },

                    ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        androidx.compose.material.Icon(
                            painter = painterResource(id = R.drawable.history_icon),
                            contentDescription = "History icon",
                            modifier = Modifier.size(20.dp),
                            tint = if (selected.value == BottomIcons.HISTORY) Color.Black else Color.Gray
                        )
                        androidx.compose.material.Text(stringResource(R.string.history))
                    }
                }

                val map = painterResource(id = R.drawable.mappa)
                androidx.compose.material.IconButton(
                    onClick = {
                        selected.value = BottomIcons.MAP
                        ScreenNavigator.Map.route(mapName, date).let { navigateTo(it) }
                    }

                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        androidx.compose.material.Icon(
                            painter = map,
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Map Icon",
                            tint = if (selected.value == BottomIcons.MAP) Color.Black else Color.Gray
                        )
                        androidx.compose.material.Text(stringResource(R.string.Map))
                    }
                }

                val heatmap = painterResource(id = R.drawable.heatmap)
                androidx.compose.material.IconButton(onClick = {
                    selected.value = BottomIcons.HEATMAP
                    Dialog.ChooseHeatmap.route(mapName, date).let { navigateTo(it) }
                }) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        androidx.compose.material.Icon(
                            painter = heatmap,
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Heatmap icon",
                            tint = if (selected.value == BottomIcons.HEATMAP) Color.Black else Color.Gray
                        )
                        androidx.compose.material.Text(stringResource(R.string.heatmap))
                    }
                }
            }
        }
    )
}

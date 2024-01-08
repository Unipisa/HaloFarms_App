package it.unipi.halofarms.util

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import it.unipi.halofarms.R
import it.unipi.halofarms.data.map.Map
import it.unipi.halofarms.data.point.PointValue
import it.unipi.halofarms.data.sample.Sample
import it.unipi.halofarms.navigation.Dialog
import it.unipi.halofarms.screen.map.MapViewModel
import it.unipi.halofarms.screen.map.PointViewModel
import it.unipi.halofarms.ui.theme.Neutral8
import it.unipi.halofarms.ui.theme.orangeA100
import it.unipi.halofarms.ui.theme.orangeA700
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * Return a bitmap from a vector
 *
 * @param context Current context
 * @param vectorResId Id of the current resource
 *
 * @return BitmapDescriptor? A bitmap or null
 */
fun bitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {

    // Retrieves the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    // Draws it onto the bitmap
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}

/**
 * Displays point's values
 *
 * @param sample Current sample
 */
@Composable
fun ValuesForPoint(sample: Sample?) {

    Column (
        modifier = Modifier.padding(20.dp)
            ) {
        if (sample != null && sample.sar != (-1.00))
            Text(text = "SAR: ${sample.sar}", fontSize = TextUnit(16F, TextUnitType.Sp))
        else
            Text(text = stringResource(R.string.sar_null))
        // Spacer
        Spacer(Modifier.padding(4.dp))

        if (sample != null && sample.ec != (-1.00))
            Text(text = "EC: ${sample.ec}", fontSize = TextUnit(16F, TextUnitType.Sp))
        else
            Text(text = stringResource(R.string.ec_null))
        // Spacer
        Spacer(Modifier.padding(4.dp))

        if (sample != null && sample.cec != (-1.00))
            Text(text = "CEC: ${sample.cec}", fontSize = TextUnit(16F, TextUnitType.Sp))
        else
            Text(text = stringResource(R.string.cec_null))
        // Spacer
        Spacer(Modifier.padding(4.dp))

        if (sample != null && sample.ph != (-1.00))
            Text(text = "PH: ${sample.ph}", fontSize = TextUnit(16F, TextUnitType.Sp))
        else
            Text(text = stringResource(R.string.ph_null))
        // Spacer
        Spacer(Modifier.padding(4.dp))
    }
}

/**
 * Displays map's area
 *
 * @param map Current map
 */
@Composable
fun MapArea(map: Map) {
    Box(modifier = Modifier.padding(12.dp).background(Color.White, shape = RoundedCornerShape(5.dp))) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Area: ${map.area}",
                textAlign = TextAlign.Right,
                fontSize = TextUnit(16F, TextUnitType.Sp)
            )
        }
    }
}

/**
 * Goes back in the stack
 *
 * @param upPress Goes back in the stack
 */
@Composable
fun BackButton(
    upPress: () -> Unit
) {
    IconButton(
        onClick = upPress,
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .size(36.dp)
            .background(
                color = Neutral8.copy(alpha = 0.32f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = Icons.Outlined.ArrowBack,
            contentDescription = "back button",
            tint = Color.White
        )
    }
}

/**
 * Shows switch button
 *
 * @param mapViewModel Map's viewmodel
 * @param map Current map
 */
@Composable
fun SwitchMode(
    mapViewModel: MapViewModel,
    map: Map,
) {
    // Switch state
    val switchState: MutableState<Boolean> = remember { mutableStateOf(map.mode == "0") }

    Column(
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Switch(
            checked = switchState.value,
            onCheckedChange = { switchState.value = it ; mapViewModel.deleteMapPoints(map.name) }, //todo: deve eliminare anche i campionamenti,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedBorderColor = Color.Black,
                checkedTrackColor = Color.LightGray,
                uncheckedThumbColor = Color.Red,
                uncheckedBorderColor = Color.Red,
                uncheckedTrackColor = orangeA100

            )
        )

        if (switchState.value) {
            Text(text = "Draw")
            mapViewModel.setMode(map.name, "0")
        } else {
            Text(text = "Handfree")
            mapViewModel.setMode(map.name, "1")
        }
    }
}

/***
 * Adds a point where the user is
 *
 * @param mapName Map's name
 * @param pointViewModel Point's view model
 */
@SuppressLint("MissingPermission")
@Composable
fun MarkerButton(mapName: String, pointViewModel: PointViewModel){
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val auxPosition = remember { mutableStateOf(LatLng(0.0, 0.0)) }
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        auxPosition.value = LatLng(location.latitude, location.longitude)
    }

    Button(
        modifier = Modifier.padding(horizontal = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
        onClick = {
            toaster(CoroutineScope(Dispatchers.Main), context, R.string.doing_science)

            pointViewModel.addPoint(auxPosition.value.latitude, auxPosition.value.longitude, mapName)
        },
        border = BorderStroke(2.dp, Color.DarkGray),
    ) {
        Column (verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally){
            Icon(
                Icons.Filled.Place,
                contentDescription = "Add marker",
                tint = Color.Black
            )

            Text("Marker", color = Color.Black)
        }
    }
}

/**
 * Shows done button
 *
 * @param mapName Map's name
 * @param mapsViewModel Map's viewmodel
 * @param mode Current map's mode
 * @param scope Coroutine scope
 */
@Composable
fun DoneButton(mapName: String, mapsViewModel: MapViewModel, mode: String, scope: CoroutineScope){
    val context = LocalContext.current
    FloatingActionButton(
        onClick = {
            if(mode == "0") toaster(scope, context, R.string.doing_science)
            mapsViewModel.updateDone(mapName, true)
        },
        modifier = Modifier.border(3.dp, Color.Black, CircleShape),
        shape = CircleShape,
        containerColor = Color.White,
    ) {
        // End perimeter button
        Icon(
            Icons.Filled.Check,
            contentDescription = "Perimeter completed",
        )
    }
}

/**
 * Shows app's logo
 *
 * @param modifier Modifier
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTitle (
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Row {
                Image(
                    painter = painterResource(R.drawable.halofarms_transp),
                    contentDescription = "Halofarms logo",
                    modifier = Modifier
                        .background(Color.Transparent)
                        .size(140.dp),
                    colorFilter = ColorFilter.tint(Color.Black)
                )
            }
        },
        modifier = modifier.background(Color.Transparent)
    )
}

/**
 * Take a string and convert it to a QR code
 *
 * @param text PointValue's identifier
 * @return bitmap QR code
 */
fun textToImage(text: String): Bitmap {
    val bitMatrix: BitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE,
        500, 500, null)
    val bitMatrixWidth: Int = bitMatrix.width
    val bitMatrixHeight: Int = bitMatrix.height
    val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
    val colorWhite = -0x1
    val colorBlack = -0x1000000
    for (y in 0 until bitMatrixHeight) {
        val offset = y * bitMatrixWidth
        for (x in 0 until bitMatrixWidth) {
            pixels[offset + x] = if (bitMatrix.get(x, y)) colorBlack else colorWhite
        }
    }
    val bitmap = Bitmap
        .createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)
    return bitmap
}

/**
 * DatePicker's dialog
 *
 * @param modifier Modifier
 * @param pointViewModel Point's viewmodel
 */
@Composable
fun DatePicker(modifier: Modifier, pointViewModel: PointViewModel){
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Fetching current year, month and day
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

    Box(modifier = modifier) {
        OutlinedTextField(value = selectedDateText, onValueChange = {}, readOnly = true, label = { Text("dd-m-yyyy") })
        if (selectedDateText.isNotEmpty()) {
            pointViewModel.updateNewdate(selectedDateText)
        }
        Box(
            modifier = modifier
                .matchParentSize()
                .alpha(0f)
                .clickable(onClick = { datePicker.show() }),
        )
    }
}

/**
 * Graphic divider
 */
@Composable
fun Divider() {
    Divider(
        modifier = Modifier
            .padding(horizontal = 155.dp)
            .padding(vertical = 8.dp),
        thickness = 2.dp,
        color = Color.DarkGray
    )
}

/**
 * Displays a list of buttons
 *
 * @param currentPoint Current point
 * @param navigateTo Navigation function
 * @param pointViewModel Point's viewmodel
 * @param scope Coroutine scope
 * @param context Current context
 * @param date Current date
 * @param samples Point's samples
 */
@Composable
fun ButtonsRow(
    currentPoint: PointValue,
    navigateTo: (String) -> Unit,
    pointViewModel: PointViewModel,
    scope: CoroutineScope,
    context: Context,
    date: String,
    samples: List<Sample>?
){
    if (samples == null || samples.find {sample -> sample.latitude == currentPoint.latitude && sample.longitude == currentPoint.longitude && sample.sar > (-1.0)} == null) {
        Button(
            onClick = {
                Dialog
                    .PointOptions
                    .route("${currentPoint.latitude},${currentPoint.longitude}", currentPoint.zoneName)
                    .let { navigateTo(it) }
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            border = BorderStroke(2.dp, Color.Black),
        ) {
            Text(
                text = stringResource(id = R.string.add_new_values),
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.padding(4.dp))

        Button(
            onClick = {
                pointViewModel.updateToBeAnalyzed(
                    currentPoint.latitude, currentPoint.longitude,
                    true
                )
                toaster(scope, context, R.string.to_be_analyzed_current_point)
            },
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, Color.Black),
            content = {
                Text(
                    stringResource(R.string.to_be_analyzed),
                    color = Color.Black
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        )

        Spacer(modifier = Modifier.padding(4.dp))

        Button(
            onClick = {
                pointViewModel.updateToBeAnalyzed(
                    currentPoint.latitude,
                    currentPoint.longitude,
                    false
                )
                //pointViewModel.updateDate(currentPoint.latitude CurrentPoint.longitude, date)
                toaster(scope, context, R.string.do_not_analyze_current_point)
            },
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, Color.Black),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            content = {
                Text(
                    stringResource(R.string.do_not_analyze),
                    color = Color.Black
                )
            }
        )
    } else {
        Button(
            onClick = {
                pointViewModel.updateToBeAnalyzed(
                    currentPoint.latitude,
                    currentPoint.longitude,
                    false
                )
                pointViewModel.deleteSamples(currentPoint.latitude, currentPoint.longitude, date)
                toaster(scope, context, R.string.remove_values)
            },
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, orangeA700),
            content = {
                Text(
                    stringResource(R.string.remove_values),
                    color = orangeA700
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        )
    }
}

/**
 * Displays the current point state
 *
 * @param currentPoint Current point
 */
@Composable
fun PointState(currentPoint: PointValue, samples: List<Sample>) {
    val sample = samples.find { sample -> sample.latitude == currentPoint.latitude && sample.longitude == currentPoint.longitude }

    when {
        sample == null -> {
            Text(text = stringResource(R.string.not_analyzed))
        }
        (sample.ph > (-1.00)) && (sample.sar > (-1.00)) && (sample.ec > (-1.00)) && (sample.cec > (-1.00)) -> {
            Text(
                text = stringResource(R.string.analyzed_),
                color = Color.Green,
                textDecoration = TextDecoration.Underline
            )
        }
        sample.toBeAnalyzed -> {
            Text(text = stringResource(R.string.to_be_analyzed_))
        }
        else -> {
            Text(text = stringResource(R.string.not_analyzed))
        }

    }
}

/**
 * Shows a toaster
 *
 * @param scope Coroutine scope
 * @param context Current context,
 * @param notifications String that will be displayed
 */
fun toaster(scope: CoroutineScope, context: Context, notifications: Int){
    scope.launch {
        Toast.makeText(
            context,
            notifications,
            Toast.LENGTH_LONG
        ).show()
    }
}

/**
 * Displays current point's value
 *
 * @param currentPoint Current point
 * @param date Current date
 * @param samples List of currentPoint's samples
 */
@Composable
fun SheetTabs(currentPoint: PointValue, date: String, samples: List<Sample>) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(stringResource(R.string.values), "QR")
    
    // Spacer
    Spacer(Modifier.padding(16.dp))

    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(text = { Text(text = title)},
                selected = selectedTabIndex == index,
                onClick = { selectedTabIndex = index },
            )
        }
    }
    val sample = samples.find { sample -> sample.latitude == currentPoint.latitude && sample.longitude == currentPoint.longitude && sample.date == date}
    when (selectedTabIndex) {
        0 -> ValuesForPoint(sample)
        1 -> {
            // QR code
            Box {
                Image(
                    bitmap = textToImage(currentPoint.toString()).asImageBitmap(),
                    contentDescription = "QR code",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}
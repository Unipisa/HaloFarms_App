package it.unipi.halofarms.util

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import it.unipi.halofarms.R
import it.unipi.halofarms.screen.zone.MapViewModel
import it.unipi.halofarms.ui.theme.Neutral8
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
 * Goes back in the stack
 *
 * @param upPress Goes back in the stack
 */
@Composable
fun Up(
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

@Composable
fun SwitchMode(
    map: it.unipi.halofarms.screen.zone.Map,
    mapViewModel: MapViewModel,
) {
    // Switch state
    val switchState: MutableState<Boolean> = remember { mutableStateOf(map.mode == "0") }
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Switch(
            checked = switchState.value,
            onCheckedChange = { switchState.value = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = Color.Black,
                uncheckedThumbColor = Color.Red,
                uncheckedTrackColor = Color.Red
            )
        )
        // Sets mode if there are no sampling points
        if (map.points?.isNotEmpty() != false) {
            if (switchState.value) {
                Text(text = "Draw")
                mapViewModel.setMode(map.name, "0")
                if(map.points != null) {
                    mapViewModel.deleteMapPoints(map.name!!)
                }
            } else {
                Text(text = "Handfree")
                mapViewModel.setMode(map.name, "1")
                if(map.points != null) {
                    mapViewModel.deleteMapPoints(map.name!!)
                }
            }
        }
    }
}

@Composable
fun DoneButton(mapName: String, mapsViewModel: MapViewModel, padding: PaddingValues, mode: String){
    val context = LocalContext.current
    FloatingActionButton(
        onClick = {
            if(mode == "0") Toast.makeText(context, "Doing science...", Toast.LENGTH_LONG).show()
            mapsViewModel.updateDone(mapName, "true")
        },
        modifier = Modifier
            .padding(padding)
            .padding(horizontal = 40.dp),
        backgroundColor = Color.White
    ) {
        // End perimeter button
        androidx.compose.material3.Icon(
            Icons.Outlined.CheckCircle,
            contentDescription = "Perimeter completed"
        )
    }
}

/**
 * Displays the title
 *
 * @param title Map's name
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
 * @param text Point's identifier
 * @return bitmap QR code
 */
fun textToImage(text: String): Bitmap? {
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
        .createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
    bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)
    return bitmap
}
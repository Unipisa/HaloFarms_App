package it.unipi.halofarms.screen.home

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import it.unipi.halofarms.R
import it.unipi.halofarms.navigation.Dialog
import it.unipi.halofarms.navigation.Navigator
import it.unipi.halofarms.screen.zone.Map
import it.unipi.halofarms.ui.theme.MediumGreen
import it.unipi.halofarms.util.HomeTitle

/**
 * Implements the Home Screen, which includes the whole field's list
 *
 * @param navigateTo, function that brings the user to a new dialog or a new screen
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    locationPermission: MultiplePermissionsState,
    maps: State<List<Map>>,
    navigateTo: (String, Boolean) -> Unit,
) {
    // Scaffold state
    val scaffoldState = rememberScaffoldState()

    androidx.compose.material.Scaffold(
        scaffoldState = scaffoldState,
        // Displays the title
        topBar = {
            Column {
                HomeTitle()
                Text(
                    text = stringResource(id = R.string.Fields),
                    fontSize = 24.sp,
                    modifier = Modifier.padding(horizontal = 34.dp),
                    fontWeight = FontWeight.Bold,
                )
                Divider(
                    modifier = Modifier.padding(vertical = 14.dp, horizontal = 34.dp),
                    thickness = 1.dp
                )
            }
        },
        // Adds a field
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(34.dp),
                onClick = { Dialog.AddMap.route.let { navigateTo(it, false) } },
                content = {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add a zone"
                    )
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        backgroundColor = Color.White
    ) { padding ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(padding)
                .padding(8.dp)){
            items(maps.value.size){
                Card(
                    modifier = Modifier
                        .padding(28.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                // Brings to deleting the zone
                                onLongPress = { _ ->
                                    Dialog.LongMap
                                        .route(maps.value[it].name)
                                        .let { navigateTo(it, false) }
                                },
                                // Brings to the zone
                                onTap = { _ ->
                                    if (!locationPermission.allPermissionsGranted) {
                                        locationPermission.launchMultiplePermissionRequest()
                                    } else {
                                        Navigator.Map
                                            .route(maps.value[it].name)
                                            .let { navigateTo(it, false) }
                                    }
                                }
                            )
                        },
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(5.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        MapView(
                            map = maps.value[it]
                        )
                    }
                }
            }
        }
    }
}

/**
 * Map view in home screen
 *
 * @param map: current map
 * @param modifier, graphics helper
 */
@Composable
fun MapView(
    map: Map,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
    ) {
        // Zone's icon
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_eco_24),
            contentDescription = stringResource(R.string.zone_icon),
            modifier = modifier
                .align(Alignment.CenterHorizontally)
                .size(32.dp),
            tint = MediumGreen
        )
        // Zone's name
        androidx.compose.material.Text(
            map.name!!,
            style = MaterialTheme.typography.body1,
            modifier = modifier.padding(8.dp)
        )
    }
}

package it.unipi.halofarms.util

import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.*

/**
 * Observes the multiple permissions' states and returns it
 *
 * @return MultiplePermissionsState The permissions' states
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun locationPermissionState() : MultiplePermissionsState {
    return rememberMultiplePermissionsState(listOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION))
}
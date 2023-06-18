package it.unipi.halofarms

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material.ExperimentalMaterialApi
import it.unipi.halofarms.screen.HaloFarmsApp

class HaloFarmsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HaloFarmsApp()
        }
    }
}

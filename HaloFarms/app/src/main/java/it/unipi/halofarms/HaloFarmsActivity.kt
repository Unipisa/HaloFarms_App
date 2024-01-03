package it.unipi.halofarms

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.*
import it.unipi.halofarms.data.cloud.FirestoreProxy

class HaloFarmsActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterialApi::class)
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isFirstRun = FirestoreProxy.sharedPreferences.getBoolean("isFirstRun", true)

            if(isFirstRun){
                FirestoreProxy().FromCloud()
                FirestoreProxy.sharedPreferences.edit().putBoolean("isFirstRun", false).apply()
            }

            HaloFarmsApp()
        }
    }
}

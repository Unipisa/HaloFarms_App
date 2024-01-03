package it.unipi.halofarms.register

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import it.unipi.halofarms.HaloFarmsActivity
import it.unipi.halofarms.R

// Tag used while interacting with the LOG
private const val TAG = "RegisterActivity"

//chiave play integrity: c8:f7:8d:31:09:4b:79:5e:26:9a:fd:ed:32:c7:40:2c:6e:0e:ce:42:c4:e7:67:4c:ad:36:55:ff:5b:5b:84:a6

/**
 * This class manages the registration/login of the user. It uses a default view.
 */
class RegisterActivity: ComponentActivity() {

    companion object {
        lateinit var username: String
        // SharedPreferences file where are stored username and password of the user
        lateinit var preferences: SharedPreferences
    }

    // The sign-in launcher
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        preferences = applicationContext
            .getSharedPreferences("file_key", Context.MODE_PRIVATE)

        try {

            createSignInIntent()
        } catch (e: Exception) {
            Log.e(TAG, "Cannot complete login", e)
        }
    }

    /**
     * Creates and launches sign-in intent
     */
    private fun createSignInIntent() {
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    /**
     * Checks the authentication result and writes it down
     * on the SharedPreferences' file. It also gives the user some feedbacks about it.
     */
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if(response == null) Log.e(TAG, "Cannot complete sign in, since response is null")

        // Successfully signed in
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                preferences.edit()?.putString("USERNAME", user.email)?.apply()
                try {
                    username = user.email!!
                } catch(e: Exception) {
                    Log.e(TAG, "User's email is null", e)
                }
            }

            val intent = Intent(this, HaloFarmsActivity::class.java)
            startActivity(intent)
        }
        // Cannot sign in
        else {
            Log.e(TAG, "Cannot complete sign in")
            Toast.makeText(this, getString(R.string.try_again),
                Toast.LENGTH_LONG).show()
        }
    }
}
package com.example.deliveryprototype

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.deliveryprototype.ui.theme.DeliveryPrototypeTheme
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = com.example.deliveryprototype.data.AppRepository(this)
        repository.insertSampleData()
        enableEdgeToEdge()
        setContent {
            DeliveryPrototypeTheme {
                val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                var loggedInUser by remember { mutableStateOf<com.example.deliveryprototype.model.UserEntity?>(null) }
                var loginError by remember { mutableStateOf<String?>(null) }

                // Check for saved user on app start
                LaunchedEffect(Unit) {
                    val savedUserId = sharedPrefs.getInt("user_id", -1)
                    if (savedUserId != -1) {
                        val user = runBlocking { repository.getUserById(savedUserId) }
                        if (user != null) loggedInUser = user
                    }
                }

                if (loggedInUser == null) {
                    com.example.deliveryprototype.ui.screens.LoginScreen(
                        onLogin = { username, password ->
                            // Validar usuario y contraseña usando la base de datos
                            val user = runBlocking {
                                repository.getUserByUsernameAndPassword(username, password)
                            }
                            if (user != null) {
                                loggedInUser = user
                                loginError = null
                                sharedPrefs.edit().putInt("user_id", user.id).apply()
                            } else {
                                loginError = "Usuario o contraseña incorrectos"
                            }
                        },
                        error = loginError
                    )
                } else {
                    val onLogout = {
                        loggedInUser = null
                        sharedPrefs.edit().remove("user_id").apply()
                    }
                    when (loggedInUser!!.role) {
                        "tendero" -> com.example.deliveryprototype.ui.nav.TenderoNavScaffold(onLogout = onLogout)
                        "cliente" -> com.example.deliveryprototype.ui.nav.ClienteNavScaffold(onLogout = onLogout, loggedInUser = loggedInUser!!)
                        "repartidor" -> com.example.deliveryprototype.ui.nav.RepartidorNavScaffold(onLogout = onLogout)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DeliveryPrototypeTheme {
        // Vista previa del login
        com.example.deliveryprototype.ui.screens.LoginScreen(onLogin = { _, _ -> }, error = null)
    }
}
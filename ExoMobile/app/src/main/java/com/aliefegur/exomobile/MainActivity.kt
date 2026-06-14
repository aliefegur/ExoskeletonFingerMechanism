package com.aliefegur.exomobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aliefegur.exomobile.communication.ApiClient
import com.aliefegur.exomobile.communication.Repository
import com.aliefegur.exomobile.ui.MainScreen
import com.aliefegur.exomobile.ui.theme.ExoMobileTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val apiClient = ApiClient("http://192.168.1.50")
    private val repository = Repository(apiClient)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExoMobileTheme {
                MainScreen(
                    onRun = { mode, duration ->

                        CoroutineScope(Dispatchers.IO).launch {

                            val success = repository.sendMotionCommand(
                                mode = mode.name,
                                duration = duration
                            )

                            println("Command sent: $success")
                        }
                    }
                )
            }
        }
    }
}

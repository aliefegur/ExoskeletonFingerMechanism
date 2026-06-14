package com.aliefegur.exomobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aliefegur.exomobile.communication.*
import com.aliefegur.exomobile.ui.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // -------------------------
        // DEPENDENCY GRAPH
        // -------------------------

        val apiClient = ApiClient("http://192.168.1.50")
        val sessionManager = SessionManager(apiClient)
        val repository = Repository(sessionManager)
        val commandQueue = CommandQueue(repository)
        val viewModel = MainViewModel(commandQueue)

        setContent {
            MainScreen(viewModel = viewModel)
        }
    }
}

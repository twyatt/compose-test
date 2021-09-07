package com.example.composetest

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay

class MainViewModel : ViewModel() {

    suspend fun fetch() {
        delay(30_000L) // Emulate slow network request.
    }
}

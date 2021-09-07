/*
 * Copyright 2021 JUUL Labs, Inc.
 */

package com.example.composetest

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope

/** Empty [ViewModel] which exists as the receiver for [viewModelScope]. */
class ArtificialViewModel : ViewModel()

/** [CoroutineScope] tied to an arbitrary [ViewModel] with the default lifecycle. */
@Composable
fun viewModelScope(): CoroutineScope =
    viewModel<ArtificialViewModel>().viewModelScope

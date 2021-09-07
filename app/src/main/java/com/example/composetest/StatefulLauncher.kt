/*
 * Copyright 2021 JUUL Labs, Inc.
 */

package com.example.composetest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

class StatefulLauncher internal constructor(
    private val action: () -> Unit,
    val isRunning: State<Boolean>,
) {
    operator fun invoke() { action() }
}

/**
 * Syntax sugar for creating a lazy coroutine scoped to [viewModelScope].
 *
 * @see CoroutineScope.statefulLauncher
 */
@Composable
fun statefulLauncher(
    action: suspend () -> Unit,
): StatefulLauncher = viewModelScope().statefulLauncher(action)

/**
 * Wraps a suspend lambda with a non-suspending lambda that [launch]es in a [viewModelScope] upon the returned
 * [StatefulLauncher] being invoked:
 *
 * ```
 * val action = suspend { doThings() }.asLazyLaunch()
 * action() // launch the action
 * action.isRunning.value // query the state of the action
 * ```
 *
 * @returns [StatefulLauncher] action that can be invoked to launch, and queried for its run state.
 */
@Composable
fun (suspend () -> Unit).withStatefulLauncher(): StatefulLauncher = viewModelScope().statefulLauncher(this)

/**
 * Wraps the suspend [action] in a [lazy coroutine][StatefulLauncher] that is a child of the receiver [CoroutineScope] and is
 * launched upon invocation, and can be queried for run state:
 *
 * ```
 * val action = viewModelScope().lazyLaunch { doThings() }
 * action() // launch the action
 * action.isRunning.value // query the state of the action
 * ```
 *
 * @returns [StatefulLauncher] action that can be invoked to launch, and queried for its run state.
 */
@Composable
fun CoroutineScope.statefulLauncher(
    action: suspend () -> Unit
): StatefulLauncher {
    val count = remember { MutableStateFlow(0) }
    val isRunning = count.map { it > 0 }.collectAsState(initial = false)
    val block: () -> Unit = {
        count.increment().also { println("count: $it") }
        launch { action() }
            .invokeOnCompletion { count.decrement().also { println("count: $it") } }
    }
    return StatefulLauncher(block, isRunning)
}

private fun MutableStateFlow<Int>.increment() = updateAndGet { value -> value + 1 }
private fun MutableStateFlow<Int>.decrement() = updateAndGet { value -> value - 1 }

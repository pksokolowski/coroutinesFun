package com.github.pksokolowski.coroutinesfun.features.flows

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FlowsViewModel @ViewModelInject constructor(
    private val usersProvider: UsersProvider
) : ViewModel() {
    val currentUser = usersProvider.user.asLiveData()

    private val _singleEvent = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 0)
    val singleEvent = _singleEvent.asSharedFlow()

    private val _event =
        MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 0)
    val event = _event.asLiveData()

    private val _state = MutableStateFlow("initial state")
    val state = _state.asLiveData()

    fun sendSingleEvent(content: String) {
        viewModelScope.launch {
            _singleEvent.emit(content)
        }
    }

    fun sendEvent(content: String) {
        viewModelScope.launch {
            _event.emit(content)
        }
    }

    fun setState(content: String) {
        _state.value = content
    }

    fun loadNextUser() {
        viewModelScope.launch {
            usersProvider.loadNextUser()
        }
    }
}
package com.github.pksokolowski.coroutinesfun.features.flows

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FlowsViewModel @ViewModelInject constructor(
    private val usersProvider: usersProvider
) : ViewModel() {
    val currentUser = usersProvider.user.asLiveData()

    fun loadNextUser() {
        viewModelScope.launch {
            usersProvider.loadNextUser()
        }
    }
}
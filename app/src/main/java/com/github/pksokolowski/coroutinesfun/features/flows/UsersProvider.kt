package com.github.pksokolowski.coroutinesfun.features.flows

import com.github.pksokolowski.coroutinesfun.utils.toCyclicIterator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class UsersProvider @Inject constructor(

) {
    private val _user = MutableStateFlow<User>(Unknown)
    val user: StateFlow<User> = _user

    private val users = listOf(
        LoggedIn("Edmund", "mchristo@example.com", "Champs Elisees 30"),
        LoggedIn("Zygmunt", "kzygmunt@example.com", "Warszawa"),
    ).toCyclicIterator()

    fun loadNextUser() {
        _user.value = users.next()
    }
}
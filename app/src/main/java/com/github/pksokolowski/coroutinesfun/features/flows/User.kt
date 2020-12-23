package com.github.pksokolowski.coroutinesfun.features.flows

sealed class User
object Unknown : User()
data class LoggedIn(val name: String, val email: String, val address: String) : User()
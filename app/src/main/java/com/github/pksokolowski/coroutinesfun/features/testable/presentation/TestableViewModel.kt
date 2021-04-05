package com.github.pksokolowski.coroutinesfun.features.testable.presentation

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.github.pksokolowski.coroutinesfun.features.testable.db.IStoreRepository

class TestableViewModel @ViewModelInject constructor(
    private val storeRepository: IStoreRepository
) : ViewModel() {

}
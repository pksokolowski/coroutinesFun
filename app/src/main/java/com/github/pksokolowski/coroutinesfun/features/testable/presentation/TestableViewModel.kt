package com.github.pksokolowski.coroutinesfun.features.testable.presentation

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.github.pksokolowski.coroutinesfun.features.testable.db.IItemsRepository

class TestableViewModel @ViewModelInject constructor(
    private val itemsRepository: IItemsRepository
) : ViewModel() {

}
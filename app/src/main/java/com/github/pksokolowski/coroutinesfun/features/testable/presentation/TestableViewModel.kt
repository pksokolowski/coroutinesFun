package com.github.pksokolowski.coroutinesfun.features.testable.presentation

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.github.pksokolowski.coroutinesfun.features.testable.db.IItemsRepository
import kotlinx.coroutines.CoroutineDispatcher

class TestableViewModel @ViewModelInject constructor(
    private val backGroundDispatcher: CoroutineDispatcher,
    private val itemsRepository: IItemsRepository
) : ViewModel() {

}
package com.github.pksokolowski.coroutinesfun.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.pksokolowski.coroutinesfun.features.start.StartScreenFragment
import javax.inject.Inject

class MainFragmentFactory @Inject constructor(
    private val someDependency: String
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            StartScreenFragment::class.java.name -> {
                StartScreenFragment(someDependency)
            }
            else ->
                super.instantiate(classLoader, className)
        }
    }
}
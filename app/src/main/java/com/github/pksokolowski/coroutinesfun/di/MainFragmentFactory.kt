package com.github.pksokolowski.coroutinesfun.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.pksokolowski.coroutinesfun.features.downloads.DownloadsFragment
import com.github.pksokolowski.coroutinesfun.features.standalones.StandAlonesFragment
import com.github.pksokolowski.coroutinesfun.features.start.StartScreenFragment
import javax.inject.Inject

class MainFragmentFactory @Inject constructor(
    private val someDependency: String
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            StartScreenFragment::class.java.name -> StartScreenFragment(someDependency)
            StandAlonesFragment::class.java.name -> StandAlonesFragment()
            DownloadsFragment::class.java.name -> DownloadsFragment()
            else -> super.instantiate(classLoader, className)
        }
    }
}
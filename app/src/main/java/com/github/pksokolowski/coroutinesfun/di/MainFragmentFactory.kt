package com.github.pksokolowski.coroutinesfun.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.pksokolowski.coroutinesfun.features.downloads.DownloadsFragment
import com.github.pksokolowski.coroutinesfun.features.flows.FlowsFragment
import com.github.pksokolowski.coroutinesfun.features.standalones.StandAlonesFragment
import com.github.pksokolowski.coroutinesfun.features.start.StartScreenFragment
import com.github.pksokolowski.coroutinesfun.features.work.WorkFragment
import javax.inject.Inject

class MainFragmentFactory @Inject constructor(
    private val someDependency: String
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            StartScreenFragment::class.java.name -> StartScreenFragment(someDependency)
            FlowsFragment::class.java.name -> FlowsFragment()
            DownloadsFragment::class.java.name -> DownloadsFragment()
            StandAlonesFragment::class.java.name -> StandAlonesFragment()
            WorkFragment::class.java.name -> WorkFragment()

            else -> super.instantiate(classLoader, className)
        }
    }
}
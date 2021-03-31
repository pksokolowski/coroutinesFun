package com.github.pksokolowski.coroutinesfun.features.testable.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.pksokolowski.coroutinesfun.databinding.FragmentTestableBinding
import com.github.pksokolowski.coroutinesfun.utils.autoCleared

class TestableFragment : Fragment() {
    private var binding by autoCleared<FragmentTestableBinding>()
    private val viewmodel: TestableViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTestableBinding.inflate(inflater, container, false)

        return binding.root
    }

}
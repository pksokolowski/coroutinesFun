package com.github.pksokolowski.coroutinesfun.features.work

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.pksokolowski.coroutinesfun.databinding.FragmentWorkBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkFragment(

) : Fragment() {
    private val workViewModel: WorkViewModel by viewModels()

    private var _binding: FragmentWorkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}
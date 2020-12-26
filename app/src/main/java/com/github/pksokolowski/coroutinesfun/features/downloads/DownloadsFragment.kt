package com.github.pksokolowski.coroutinesfun.features.downloads

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.pksokolowski.coroutinesfun.R
import com.github.pksokolowski.coroutinesfun.databinding.FragmentDownloadsBinding
import com.github.pksokolowski.coroutinesfun.utils.CAT_PICTURE_URL
import com.github.pksokolowski.coroutinesfun.utils.filterOutColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.FileNotFoundException
import java.lang.Exception
import java.net.URL

class DownloadsFragment : Fragment() {
    private var _binding: FragmentDownloadsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.downloadFileNotFound.setOnClickListener {
            downloadImage(CAT_PICTURE_URL.replace("cat", "dog"))
        }

        binding.downloadCatPicture.setOnClickListener {
            downloadImage(CAT_PICTURE_URL)
        }
    }

    private fun downloadImage(url: String) {
        lifecycleScope.launchWhenStarted {
            val picDeferred = lifecycleScope.async(Dispatchers.IO) { getImage(url) }

            binding.progressBar.isVisible = true
            try {
                val pic = picDeferred.await()

                if (binding.grayOnly.isChecked) {
                    val grayVersionDeferred =
                        lifecycleScope.async(Dispatchers.Default) { pic.filterOutColors() }

                    showImage(grayVersionDeferred.await())
                } else {
                    showImage(pic)
                }
            } catch (e: FileNotFoundException) {
                setDrawable(R.drawable.ic_baseline_broken_image_24)
            } catch (e: Exception) {
                setDrawable(R.drawable.ic_baseline_error_outline_24)
            }
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun setDrawable(@DrawableRes drawable: Int) {
        binding.imageView.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                drawable
            )
        )
    }

    private fun getImage(url: String) = URL(url).openStream().use {
        BitmapFactory.decodeStream(it)
    }

    private fun showImage(bitmap: Bitmap) {
        binding.imageView.setImageBitmap(bitmap)
    }
}
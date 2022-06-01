package vu.pham.runningappseminar.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.databinding.FragmentFullScreenImageBinding
import vu.pham.runningappseminar.utils.Constants

class FullScreenImageFragment: Fragment() {
    private lateinit var binding: FragmentFullScreenImageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFullScreenImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getImageData()
        binding.imageViewBackFullScreen.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getImageData() {
        val bundle = arguments
        bundle?.let {
            val urlImage = it.getString(Constants.URL_IMAGE)
            Picasso.get().load(urlImage)
                .error(R.drawable.ic_error_gif)
                .placeholder(R.drawable.ic_loading_gif)
                .into(binding.imageViewFullScreen)
        }
    }
}
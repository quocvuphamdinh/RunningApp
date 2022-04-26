package vu.pham.runningappseminar.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import vu.pham.runningappseminar.databinding.FragmentResultExerciseRunBinding

class ResultExerciseRunFragment : Fragment() {
    private lateinit var binding : FragmentResultExerciseRunBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResultExerciseRunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageCloseResultExerciseRunFragment.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
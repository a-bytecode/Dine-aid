package com.example.dine_aid.UI

import BottomSheetAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.dine_aid.R
import com.example.dine_aid.databinding.BottomSheetLayoutBinding
import com.example.dine_aid.model.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModalBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetLayoutBinding

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val bottomSheetAdapter = BottomSheetAdapter(viewModel.repo,requireContext())

        binding.ingridientswidgetRecycler.adapter = bottomSheetAdapter

        viewModel.repo.recipeInfo.observe(viewLifecycleOwner) {
            if (it != null) {
                bottomSheetAdapter.submitRecipeInfo(it)
            } else {
                Log.d("recipeInfoIsNull2","recipeInfo -> ${it}")
            }
        }
    }
}
package com.example.dine_aid.data

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
        binding = initializeBinding(inflater, container)
        return binding.root
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    private fun initializeBinding(inflater: LayoutInflater, container: ViewGroup?): BottomSheetLayoutBinding {
        return BottomSheetLayoutBinding.inflate(inflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val bottomSheetAdapter = BottomSheetAdapter()

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
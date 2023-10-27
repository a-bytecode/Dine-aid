package com.example.dine_aid.UI

import BottomSheetAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dine_aid.databinding.BottomSheetLayoutBinding
import com.example.dine_aid.model.MainViewModel

class BottomSheetFragment : Fragment() {

    private lateinit var binding : BottomSheetLayoutBinding

    private val viewModel : MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = BottomSheetLayoutBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val bottomSheetAdapter = BottomSheetAdapter()

        binding.ingridientswidgetRecycler.adapter = bottomSheetAdapter

        viewModel.repo.recipesID.observe(viewLifecycleOwner) { recipeID ->
            if (recipeID != null) {
                bottomSheetAdapter.submitID(
                    recipeID
                )
            }
        }
    }
}
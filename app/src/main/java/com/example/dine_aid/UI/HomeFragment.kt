package com.example.dine_aid.UI

import android.graphics.drawable.AnimatedImageDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.constraintlayout.widget.Constraints
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.dine_aid.R
import com.example.dine_aid.adapter.RecipeResultAdapter
import com.example.dine_aid.databinding.HomeFragmentBinding
import com.example.dine_aid.model.MainViewModel

class HomeFragment : Fragment() {

    private lateinit var binding : HomeFragmentBinding

    private val viewModel : MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = HomeFragmentBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val recipeResultAdapter = RecipeResultAdapter(
            requireContext(),
            parentFragmentManager,
            viewModel
        )

        binding.recyclerView.adapter = recipeResultAdapter

        viewModel.slideInFromLeftAnimationTV(
                binding.bottomTV,
                requireContext()
        )


        val searchView = view.findViewById<SearchView>(R.id.searchView)

        viewModel.repo.recipes.observe(viewLifecycleOwner) { recipes ->
            recipeResultAdapter.submitList(recipes)
            Log.d("Check Recipe ID", "RecipeID -> ${recipes[0].id}")
        }


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (!query.isNullOrBlank()) {
                    viewModel.getRecipes(query)
                    Log.d("QueryTextTest", "Text -> ${query}")
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle text changes
                return true
            }
        })
    }


}

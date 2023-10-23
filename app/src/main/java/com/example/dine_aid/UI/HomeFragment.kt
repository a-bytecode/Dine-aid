package com.example.dine_aid.UI

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
        // TODO: Homefragment

        val recipeResultAdapter = RecipeResultAdapter(requireContext())

        binding.recyclerView.adapter = recipeResultAdapter


        val searchView = view.findViewById<SearchView>(R.id.searchView)

        viewModel.repo.recipes.observe(viewLifecycleOwner) { recipes ->
            recipeResultAdapter.submitList(recipes)
        }

        // Set an OnQueryTextListener to the SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Here, you can use the 'query' parameter to make your API request
                if (!query.isNullOrBlank()) {
                    viewModel.getRecipes(query)
                    Log.d("QueryTextTest", "Text -> ${query}")
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle text changes as needed
                return true
            }
        })
    }


}

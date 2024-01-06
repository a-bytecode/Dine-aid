package com.example.dine_aid.UI

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dine_aid.R
import com.example.dine_aid.adapter.LastResultAdapter
import com.example.dine_aid.adapter.RecipeResultAdapter
import com.example.dine_aid.databinding.HomeFragmentBinding
import com.example.dine_aid.model.FirebaseViewModel
import com.example.dine_aid.model.MainViewModel

class HomeFragment : Fragment() {

    private lateinit var binding : HomeFragmentBinding

    private val viewModel : MainViewModel by activityViewModels()

    private val firebaseViewModel : FirebaseViewModel by activityViewModels()

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
            viewModel,
            firebaseViewModel
        )

        val lastWatchedAdapter = LastResultAdapter(
            requireContext(),
            parentFragmentManager,
            viewModel,
            firebaseViewModel
        )

        binding.recipeResultRecycler.adapter = recipeResultAdapter

        viewModel.slideInFromLeftAnimationTV(
                binding.bottomTV,
                requireContext()
        )

        firebaseViewModel.fetchLastWatchedResults()

        val searchView = view.findViewById<SearchView>(R.id.searchView)

        firebaseViewModel.lastWatchedLiveData.observe(viewLifecycleOwner) { lastWatchedResults ->
            if (lastWatchedResults.isEmpty()) {
                binding.latestResultsTV.alpha = 0f
                Log.d("lastwatchedTester","nomral recipes kategory -> Kategory")
                viewModel.repo.recipes.observe(viewLifecycleOwner) { recipes ->
                    recipeResultAdapter.submitList(recipes) }
                } else {
                    binding.latestResultsTV.alpha = 1f
                Log.d("lastwatchedTester","lastWatched -> Kategory")
                recipeResultAdapter.submitList(lastWatchedResults)
                }
            }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (!query.isNullOrBlank()) {
                    viewModel.getRecipes(query)
                    //Hier wird die Eingabe des Nutzers wieder gelöscht.
                    searchView.setQuery("",false)
                    Log.d("QueryTextTest", "Text -> ${query}")

                    val inputMethodManager =
                        context?.getSystemService(
                            Context.INPUT_METHOD_SERVICE
                        ) as InputMethodManager?
                    inputMethodManager?.hideSoftInputFromWindow(searchView.windowToken, 0)
                    // Hier wird die SearchView wieder zurückgesetzt.
                    searchView.isIconified = true
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

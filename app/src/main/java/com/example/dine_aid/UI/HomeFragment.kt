package com.example.dine_aid.UI

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dine_aid.R
import com.example.dine_aid.adapter.LastResultAdapter
import com.example.dine_aid.adapter.RecipeResultAdapter
import com.example.dine_aid.databinding.HomeFragmentBinding
import com.example.dine_aid.model.FirebaseViewModel
import com.example.dine_aid.model.MainViewModel
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams

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

        viewModel.slideInFromLeftAnimationTV(
            binding.bottomTV,
            requireContext()
        )

        viewModel.toggleSearchState(false)

        viewModel.isSearching.observe(viewLifecycleOwner) { isSearching ->
            if (isSearching == false) {
                firebaseViewModel.fetchLastWatchedResults()
                binding.latestResultsTV.alpha = 1f
                binding.recipeResultRecycler.adapter = lastWatchedAdapter

                firebaseViewModel.lastWatchedLiveData.observe(viewLifecycleOwner) {
                    lastWatchedAdapter.submitList(it)
                    Log.d("lastWatchedListCheck1", "lastWatched Size -> ${it.size}")
                }
            } else {
                binding.latestResultsTV.alpha = 0f
                binding.recipeResultRecycler.adapter = recipeResultAdapter

                viewModel.repo.recipes.observe(viewLifecycleOwner) {
                    recipeResultAdapter.submitList(it)
                }
        }
    }

        val searchView = view.findViewById<SearchView>(R.id.searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (!query.isNullOrBlank()) {
                    viewModel.toggleSearchState(true)
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




        binding.arrowUpIV.setOnClickListener {

            binding.animCardView.visibility = View.GONE
            binding.arrowDownIV.visibility = View.VISIBLE

            //Hier wird der obere Constraint der RecyclerView mit Margin 0dp instanziiert.
            val layoutParams = binding.recipeResultRecycler.layoutParams as LayoutParams
            layoutParams.topMargin = 0

            val constraintSet = ConstraintSet()

            // Hier Clonen wir alle Elemente die in der ActivityMainContraint liegen.
            constraintSet.clone(binding.activityMainConstrain)

            // Hier weisen wir den Constraint der RecyclerView zu der ImageView zu.
            constraintSet.connect(
                binding.recipeResultRecycler.id,
                ConstraintSet.TOP,
                binding.arrowDownIV.id,
                ConstraintSet.BOTTOM
            )

            constraintSet.applyTo(binding.activityMainConstrain)

            binding.recipeResultRecycler.layoutParams = layoutParams
        }

        binding.arrowDownIV.setOnClickListener {

            binding.animCardView.visibility = View.VISIBLE
            binding.arrowDownIV.visibility = View.GONE

            val layoutParams = binding.recipeResultRecycler.layoutParams as LayoutParams
            layoutParams.topMargin = 0

            val constraintSet = ConstraintSet()

            constraintSet.clone(binding.activityMainConstrain)

            // Hier weisen wir den Constraint wieder der RecyclerView zu der animCardView zu.
            constraintSet.connect(
                binding.recipeResultRecycler.id,
                ConstraintSet.TOP,
                binding.animCardView.id,
                ConstraintSet.BOTTOM
            )

            constraintSet.applyTo(binding.activityMainConstrain)

            binding.recipeResultRecycler.layoutParams = layoutParams
        }
    }
}

package com.example.dine_aid.UI

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.dine_aid.MainActivity

class HomeFragment : Fragment() {

    private lateinit var binding : HomeFragmentBinding

    private val viewModel : MainViewModel by activityViewModels()

    private val firebaseViewModel : FirebaseViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        navController = findNavController()

        binding = HomeFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val firebaseViewModel = FirebaseViewModel(Application())

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

        binding.menuIV.setOnClickListener {
            // TODO: Hier brauchen wir das Pop Up Menu.
            showPopUpMenu(it)
        }

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

    @RequiresApi(Build.VERSION_CODES.Q)
    fun showPopUpMenu(view: View) {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {

            val popupMenu = PopupMenu(requireContext(), view)

            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {

                when (it.itemId) {

                    R.id.pop_up_fav_home -> {
                        // TODO: Favoriten Screen Verbinden
                        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(
                            R.id.fragmentContainerView) as NavHostFragment
                        val navController = navHostFragment.navController
                        navController.navigate(R.id.test_Fragment)

                    }

                    R.id.pop_up_deleteAll_home -> {
                        // TODO: Delete Funktion aus der Firebase Datenank löschen.

                    }

                    R.id.pop_up_end_home -> {
                        // TODO: Alert Dialog aufrufen und App Beenden Funktion implementierencheck_navigation
                    }

                    R.id.pop_up_logout_home -> {
                        firebaseViewModel.logoutAccount()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK)
                        requireContext().startActivity(intent)
                        requireActivity().finish()
                    }
                }
                true
            }
            popupMenu.show()
        } else {

            val popupMenu = PopupMenu(requireContext(), view)

            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.menu.findItem(R.id.pop_up_fav_home).setIcon(R.drawable.baseline_favorite_24)
            popupMenu.menu.findItem(R.id.pop_up_deleteAll_home).setIcon(R.drawable.baseline_delete_24)
            popupMenu.menu.findItem(R.id.pop_up_logout_home).setIcon(R.drawable.baseline_dangerous_24)
            popupMenu.menu.findItem(R.id.pop_up_end_home).setIcon(R.drawable.baseline_exit_to_app_24)

            popupMenu.setOnMenuItemClickListener { menuItem ->

                when (menuItem.itemId) {

                    R.id.pop_up_fav_home -> {
                        // TODO: Favoriten Screen Verbinden
                        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(
                            R.id.fragmentContainerView) as NavHostFragment
                        val navController = navHostFragment.navController
                        navController.navigate(R.id.test_Fragment)

                    }

                    R.id.pop_up_deleteAll_home -> {
                        // TODO: Delete Funktion aus der Firebase Datenank löschen.

                    }

                    R.id.pop_up_end_home -> {
                        // TODO: Alert Dialog aufrufen und App Beenden Funktion implementierencheck_navigation
                    }

                    R.id.pop_up_logout_home -> {
                        firebaseViewModel.logoutAccount()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK)
                        requireContext().startActivity(intent)
                        requireActivity().finish()
                    }
                }
                true
            }
            popupMenu.show()
        }
    }
}

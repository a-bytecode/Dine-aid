package com.example.dine_aid.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.example.dine_aid.R
import com.example.dine_aid.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {

    private lateinit var binding : HomeFragmentBinding

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

        val searchView = view.findViewById<SearchView>(R.id.searchView)
    }
}
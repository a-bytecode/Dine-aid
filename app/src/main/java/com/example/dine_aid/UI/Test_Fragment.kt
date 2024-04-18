package com.example.dine_aid.UI

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dine_aid.databinding.TestactivityBinding

class Test_Fragment : Fragment() {

    private lateinit var binding: TestactivityBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TestactivityBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var counter = 0

        binding.textView2.setOnClickListener {

            counter += 1
            if (counter == 1) {
                binding.textView2.setTextColor(Color.RED)
                Log.d("Counter", "$counter")
            } else if (counter > 1) {
                binding.textView2.setTextColor(Color.WHITE)
                counter = 0
            }
        }
    }
}
package com.example.dine_aid.model

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dine_aid.R
import com.example.dine_aid.databinding.CustomDialogBinding

class CustomDialog(context: Context, activity: Activity) {

    private val binding: CustomDialogBinding

    private val dialog : AlertDialog

    private val _viewModelObserver = MutableLiveData<() -> Unit>()
            val viewModelObserver: LiveData<() -> Unit>
                get() = _viewModelObserver

    var doINeedExitApp = false

    init {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.custom_dialog,
            null,
            false)

        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)
        dialog = builder.create()

        binding.answerYes.setOnClickListener {
            setExitApp(doINeedExitApp,activity)
            viewModelObserver.value?.invoke()
            dialog.dismiss()
        }

        binding.answerNo.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun setExitApp(boolean: Boolean,activity: Activity) {
        if (boolean) {
            activity.finish()
        } else {
            dialog.dismiss()
        }
    }

    fun setIcon(id:Int) {
        binding.iconDialog.setImageResource(id)
    }

    fun setAnswerYesAction(action: () -> Unit) {
        _viewModelObserver.value = action // Hier wird die Funktion innerhalb des _ViewModel Observers durch das .invoke() aufgerufen.
    }

    fun setTextDialog(text: String) {
        binding.titleTextDialog.text = text
    }

    fun showDialog() {
        dialog.show()
    }
}
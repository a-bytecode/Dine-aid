package com.example.dine_aid.adapter

import android.content.Context
import android.graphics.drawable.AnimatedImageDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dine_aid.R
import com.example.dine_aid.data.RecipeResult

class RecipeResultAdapter(val context: Context) : RecyclerView.Adapter<RecipeResultAdapter.ItemViewHolder>() {

    private var dataset = listOf<RecipeResult>()

    fun submitList(recipeResults: List<RecipeResult>) {
        dataset = recipeResults
        notifyDataSetChanged()
    }

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val title = view.findViewById<TextView>(R.id.titleTV_item)
        val image = view.findViewById<ImageView>(R.id.imageIV_item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.reciperesult_item, parent, false)
        return ItemViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val recipeData = dataset[position]

        holder.title.text = recipeData.title

        val gif = ContextCompat.getDrawable(context, R.drawable.thai_gif) as AnimatedImageDrawable

        gif.start()
        Glide.with(context).load(recipeData.image).placeholder(gif).into(holder.image)

    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}
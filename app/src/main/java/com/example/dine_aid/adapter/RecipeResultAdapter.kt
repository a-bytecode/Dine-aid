package com.example.dine_aid.adapter

import BottomSheetAdapter
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.dine_aid.R
import com.example.dine_aid.data.RecipeResult
import com.example.dine_aid.model.FirebaseViewModel
import com.example.dine_aid.model.MainViewModel

class RecipeResultAdapter(
    val context: Context,
    val supportFragmentManager: FragmentManager,
    val viewModel: MainViewModel,
    val firebaseViewModel: FirebaseViewModel
) : RecyclerView.Adapter<RecipeResultAdapter.ItemViewHolder>() {

    var dataset = listOf<RecipeResult>()

    fun submitList(recipeResults: List<RecipeResult>) {
        dataset = recipeResults
        notifyDataSetChanged()
    }

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val title = view.findViewById<TextView>(R.id.titleTV_item)
        val image = view.findViewById<ImageView>(R.id.imageIV_item)
        val secondCardView = view.findViewById<CardView>(R.id.secondCardView)
        val clickHereCarView = view.findViewById<CardView>(R.id.clickHereToSeeMoreCardView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemLayout =
            LayoutInflater.from(
                parent.context).inflate(R.layout.reciperesult_item,
                parent, false
                )
        return ItemViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val recipeData = dataset[position]

        holder.title.text = recipeData.title
        holder.secondCardView.visibility = View.GONE

        holder.image.load(recipeData.image) {
            crossfade(true)
            crossfade(2000)
            transformations(RoundedCornersTransformation(10f))
            error(R.drawable.broken_img)
            listener { _, _ ->
                holder.secondCardView.visibility = View.VISIBLE
            }
        }

        holder.clickHereCarView.visibility = View.GONE

        holder.image.setOnClickListener {
            holder.clickHereCarView.visibility = View.VISIBLE
            holder.clickHereCarView.animation =
                AnimationUtils.loadAnimation(
                    context,R.anim.slide_up_animation
            )
        }

        holder.clickHereCarView.setOnClickListener {
                viewModel.useBottomSheet(supportFragmentManager)
                viewModel.loadRecipeInfo(recipeData.id!!)
                viewModel.getImageUrlForRecipeId(recipeData.id)
                viewModel.repo.loadRecipeNutritionWidgetByID(recipeData.id)
                firebaseViewModel.saveLastWatchedResult(recipeData)
            firebaseViewModel.updateLastWatchedForRecipe(recipeData.id)
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}
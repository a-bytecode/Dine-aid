package com.example.dine_aid.adapter

import android.content.Context
import android.graphics.drawable.AnimatedImageDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.bumptech.glide.Glide
import com.example.dine_aid.R
import com.example.dine_aid.data.ModalBottomSheet
import com.example.dine_aid.data.RecipeResult
import com.example.dine_aid.model.MainViewModel

class RecipeResultAdapter(val context: Context, val supportFragmentManager: FragmentManager,
) : RecyclerView.Adapter<RecipeResultAdapter.ItemViewHolder>() {

    private var dataset = listOf<RecipeResult>()

    fun submitList(recipeResults: List<RecipeResult>) {
        dataset = recipeResults
        notifyDataSetChanged()
    }

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val title = view.findViewById<TextView>(R.id.titleTV_item)
        val image = view.findViewById<ImageView>(R.id.imageIV_item)
        val clickToSeeMoreTV = view.findViewById<TextView>(R.id.clickToSeeMoreTV)
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

        val gif = ContextCompat.getDrawable(context, R.drawable.donuts_gif) as AnimatedImageDrawable

    // Glide Funktions Notiz:
    // -> Context / Der Bezug auf welches Fragment bzw. Ort sich die Funktion wenden soll.
    // -> load / das Laden des Eigentlichen Hauptbildes.
    // -> placeholder / Platzhalter ersatzbilf (falls er das Bild nicht laden kann).
    // -> into / Den Platz auf den er das Bild laden soll.
//        gif.start()
//        Glide.with(context).load(recipeData.image)
//            .placeholder(gif)
//            .into(holder.image)

        holder.image.load(recipeData.image) {
            crossfade(true)
            crossfade(2000)
            transformations(RoundedCornersTransformation(10f))
            error(R.drawable.broken_img)
        }

        holder.clickToSeeMoreTV.visibility = View.GONE

        holder.image.setOnClickListener {
            holder.clickToSeeMoreTV.visibility = View.VISIBLE
            holder.clickToSeeMoreTV.animation = AnimationUtils.loadAnimation(context,R.anim.slide_up_animation)
        }

        holder.clickToSeeMoreTV.setOnClickListener {

            val modalBottomSheet = ModalBottomSheet()

            modalBottomSheet.show(supportFragmentManager,ModalBottomSheet.TAG)
        }

    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}
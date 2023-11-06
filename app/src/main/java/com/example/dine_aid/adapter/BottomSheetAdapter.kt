import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimatedImageDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.example.dine_aid.FullScreenImageActivity
import com.example.dine_aid.R
import com.example.dine_aid.data.recipeInfo.RecipeInfo
import com.example.dine_aid.remote.Repository

class BottomSheetAdapter(
    val repo : Repository,
    val context: Context,
    ) : RecyclerView.Adapter<BottomSheetAdapter.ItemViewHolder>() {

    var dataset : RecipeInfo? = null

    fun submitRecipeInfo(recipeInfo:RecipeInfo) {
        dataset = recipeInfo
        notifyDataSetChanged()
    }

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val tv_title_item = view.findViewById<TextView>(R.id.tv_title_item)
        val recipeImageView = view.findViewById<ImageView>(R.id.recipeIV_item)
        val minutes_TV_Item = view.findViewById<TextView>(R.id.minutes_TV_item)
        val servingsNumberTVitem = view.findViewById<TextView>(R.id.servingsNumber_item)
        val instructionsTV = view.findViewById<TextView>(R.id.instructionsTVInfo_item)
        val ingriedientsWidgetIV = view.findViewById<ImageView>(R.id.ingriedientsWidgetIV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemLayout =
            LayoutInflater.from(
                parent.context).inflate(R.layout.recipeinformation_item,
                parent, false
            )
        return ItemViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val recipeInfoData = dataset

        if (recipeInfoData != null) {
            holder.tv_title_item.text = recipeInfoData.title
            holder.recipeImageView.load(recipeInfoData.image) {
                error(R.drawable.broken_img)
            }
            holder.minutes_TV_Item.text = recipeInfoData.readyInMinutes.toString()
            holder.servingsNumberTVitem.text = recipeInfoData.servings.toString()
            holder.instructionsTV.text = recipeInfoData.instructions
        } else {
            Log.d("recipeInfoData",
                  "recipeInfoData is null ${recipeInfoData}")
        }

        val url = repo.nutritionWidgetImage.value

        val gif = ContextCompat
            .getDrawable(
                context, R.drawable._04error
            ) as AnimatedImageDrawable
        Log.d("IsImageLoaded", "loaded Image URL ${url}")

        if (url != null) {
            gif.start()
            Glide
                .with(context)
                .load(url)
                .placeholder(gif)
                .into(holder.ingriedientsWidgetIV)

        }

        holder.ingriedientsWidgetIV.setOnClickListener {
            openFullScreenImage()
        }
    }

    private fun openFullScreenImage() {
        val fullScreenIntent = Intent(context, FullScreenImageActivity::class.java)

        val imageURL = repo.nutritionWidgetImage.value

        if (imageURL != null) {
            fullScreenIntent.putExtra("image_url", imageURL)
            context.startActivity(fullScreenIntent)
        } else {
            Toast.makeText(context,
                "Bild-URL nicht verfügbar",
                Toast.LENGTH_SHORT)
                .show()

        }
    }

    override fun getItemCount(): Int {

        return if (dataset != null) return 1 else 0
    }
}

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimatedImageDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.example.dine_aid.FullScreenImageActivity
import com.example.dine_aid.R
import com.example.dine_aid.data.recipeInfo.RecipeInfo
import com.example.dine_aid.model.MainViewModel
import com.example.dine_aid.remote.Repository
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetAdapter(
    val repo : Repository,
    val context: Context,
    val viewModel: MainViewModel,
    val supportFragmentManager: FragmentManager
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
        val recipeInfoCardView1 = view.findViewById<CardView>(R.id.recipeInfo_cardView1_item)
        val recipeInfoCardView2 = view.findViewById<CardView>(R.id.recipeInfo_cardView2_item)
        val nutritionStatisticsCardView = view.findViewById<CardView>(R.id.nutrionInfo_cardView1_item)
        val closeSheetIV = view.findViewById<ImageView>(R.id.closeSheetIV)
        val okbtng = view.findViewById<Button>(R.id.okbtng_item)

        val rootView = view
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

        holder.recipeInfoCardView1.setOnClickListener {
            holder.recipeInfoCardView2.visibility = View.VISIBLE
            holder.recipeInfoCardView2.animation = AnimationUtils
                .loadAnimation(
                    context,R.anim.slide_up_animation
                )
            holder.okbtng.setOnClickListener {
                holder.recipeInfoCardView2.animation = AnimationUtils
                    .loadAnimation(
                        context,R.anim.slide_down_animation
                    )
                holder.recipeInfoCardView2.visibility = View.GONE
            }
            if (recipeInfoData != null) {
                holder.minutes_TV_Item.text = recipeInfoData.readyInMinutes.toString()
                holder.servingsNumberTVitem.text = recipeInfoData.servings.toString()
                holder.instructionsTV.text = recipeInfoData.instructions
            }
            else {
                Log.d("recipeInfoData",
                    "recipeInfoData is null ${recipeInfoData}")
            }
        }

        holder.closeSheetIV.setOnClickListener {
            viewModel.closeBottomSheet(supportFragmentManager)
            viewModel.hideKeyboard(context,holder.rootView)
        }

        var countCardView = 0
        holder.nutritionStatisticsCardView.setOnClickListener {

             countCardView ++

                holder.ingriedientsWidgetIV.visibility = View.VISIBLE
                holder.ingriedientsWidgetIV.animation = AnimationUtils.
                loadAnimation(
                    context,R.anim.slide_up_animation
                )
            if (countCardView == 2) {
                holder.ingriedientsWidgetIV.visibility = View.GONE
                holder.ingriedientsWidgetIV.animation = AnimationUtils.
                loadAnimation(
                    context,R.anim.slide_down_animation
                )
                countCardView = 0
            }

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

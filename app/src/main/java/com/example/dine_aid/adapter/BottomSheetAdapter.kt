import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.dine_aid.R
import com.example.dine_aid.data.RecipeResult

class BottomSheetAdapter : RecyclerView.Adapter<BottomSheetAdapter.ViewHolder>() {

    private val data: MutableList<Int> = mutableListOf() // Hier speichern wir die IDs

    fun submitList(recipeResults: List<RecipeResult>) {
        // Fügen Sie die IDs aus den RecipeResults der Liste hinzu
        data.clear() // Löschen Sie die vorhandenen IDs
        data.addAll(recipeResults.map { it.id })
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ingriedientsWidet = itemView.findViewById<ImageView>(R.id.ingriedientsWidgetIV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Hier wird das Layout für die einzelnen Elemente in Ihrem Bottom Sheet festgelegt
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.ingriedientswidget_item,
            parent,
            false
        )
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val id = data[position]

        holder.ingriedientsWidet.id = id
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

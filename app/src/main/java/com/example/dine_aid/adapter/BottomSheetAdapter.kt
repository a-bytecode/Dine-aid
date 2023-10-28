import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.dine_aid.R

class BottomSheetAdapter : RecyclerView.Adapter<BottomSheetAdapter.ViewHolder>() {

    private var data: Int = -1

    fun submitID(recipeIDList: Int) {
        data = recipeIDList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ingriedientsWidet = itemView.findViewById<ImageView>(R.id.ingriedientsWidgetIV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Hier wird das Layout für die einzelnen Elemente in Ihrem Bottom Sheet festgelegt
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.recipeinformation_item,
            parent,
            false
        )
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val id = data

        holder.ingriedientsWidet.setImageResource(id)

    }

    override fun getItemCount(): Int {
        return 1
    }
}

package castofo.com.co.nower.views.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import castofo.com.co.nower.R
import castofo.com.co.nower.models.Promo

/**
 * Created by Santiago Vanegas Gil on 12/9/17.
 */
class BranchPromosAdapter(val promos: List<Promo>, private val listener: (Promo) -> Unit) :
        RecyclerView.Adapter<BranchPromosAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.promo_item, parent, false)
        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
            = holder.bind(promos[position])

    override fun getItemCount() = promos.size

    class ViewHolder(view: View, private val listener: (Promo) -> Unit) :
            RecyclerView.ViewHolder(view) {

        fun bind(promo: Promo) = with(promo) {
            (itemView.findViewById(R.id.promo_title) as TextView).text = name
            itemView.setOnClickListener { listener(this) }
        }
    }
}

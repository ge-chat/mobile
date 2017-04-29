package by.tarnenok.geofy

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.tarnenok.geofy.services.api.ChartReadModelShort

class ChartRVAdapter(var items: Array<ChartReadModelShort>)
    : RecyclerView.Adapter<ChartRVAdapter.ChartViewHolder>() {

    override fun onBindViewHolder(holder: ChartViewHolder?, position: Int) {

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChartViewHolder? {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_chart, parent, false)
        return ChartViewHolder(view)
    }

    class ChartViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    }
}

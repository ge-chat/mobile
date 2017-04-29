package by.tarnenok.geofy

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import by.tarnenok.geofy.services.api.ChartReadModelShort
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import java.text.SimpleDateFormat

class ChartRVAdapter(var items: Array<ChartReadModelShort>)
    : RecyclerView.Adapter<ChartRVAdapter.ChartViewHolder>() {

    val df = SimpleDateFormat("HH:mm");

    override fun onBindViewHolder(holder: ChartViewHolder?, position: Int) {
        if(holder == null) return

        holder.textTitle.text = items[position].title
        val message = items[position].lastMessage
        if(message != null){
            holder.textLastMessage.text = message.content
            holder.textTime.text = df.format(message.createdDate)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChartViewHolder? {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_chart, parent, false)
        return ChartViewHolder(view)
    }

    class ChartViewHolder : RecyclerView.ViewHolder{
        val textTitle: TextView
        val textLastMessage: TextView
        val textTime: TextView

        constructor(itemView: View) : super(itemView) {
            textTitle = itemView.find<TextView>(R.id.textview_title)
            textLastMessage = itemView.find<TextView>(R.id.textview_last_message)
            textTime = itemView.find<TextView>(R.id.textview_title)

            itemView.onClick {
                val intent = Intent(itemView.context, ChartActivity::class.java)
                itemView.context.startActivity(intent)
            }
        }
    }
}

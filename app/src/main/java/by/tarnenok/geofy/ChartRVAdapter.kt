package by.tarnenok.geofy

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import by.tarnenok.geofy.services.api.ApiService
import by.tarnenok.geofy.services.api.ChartReadModel
import by.tarnenok.geofy.services.api.ChartReadModelShort
import com.google.gson.Gson
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

        holder.itemView.onClick {
            ApiService.chart.getChart(items[position].id).enqueue(object : Callback<ChartReadModel>{
                override fun onFailure(call: Call<ChartReadModel>?, t: Throwable?) {
                    holder.itemView.context.toast(R.string.bad_connection)
                }

                override fun onResponse(call: Call<ChartReadModel>?, response: Response<ChartReadModel>?) {
                    if(response!!.isSuccessful){
                        val intent = Intent(holder.itemView.context, ChartActivity::class.java)
                        val dataStr = Gson().toJson(response.body())
                        intent.putExtra(ChartActivity.CONSTANTS.KEY_CHART, dataStr)
                        holder.itemView.context.startActivity(intent)
                    }else{
                        holder.itemView.context.toast(R.string.bad_connection)
                    }
                }
            })
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
        }
    }
}

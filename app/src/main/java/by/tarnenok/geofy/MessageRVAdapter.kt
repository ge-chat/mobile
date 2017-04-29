package by.tarnenok.geofy

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import by.tarnenok.geofy.services.api.MessageReadModel
import org.jetbrains.anko.find

/**
 * Created by tarne on 22.05.16.
 */
class MessageRVAdapter(var items: Array<MessageReadModel>, val ownerId: String, val chartId: String, val userId: String)
    : RecyclerView.Adapter<MessageRVAdapter.MessageViewHolder>(){

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MessageViewHolder?, position: Int) {
        holder!!.textMessage.text = items[position].message
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MessageViewHolder? {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_message, parent, false)
        return MessageRVAdapter.MessageViewHolder(view)
    }

    class MessageViewHolder : RecyclerView.ViewHolder{
        val textMessage: TextView

        constructor(itemView: View) : super(itemView) {
            textMessage = itemView.find<TextView>(R.id.textview_message)
        }
    }
}
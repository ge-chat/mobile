package by.tarnenok.geofy

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import by.tarnenok.geofy.services.api.ChartReadModel
import by.tarnenok.geofy.services.api.MessageReadModel
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.find

/**
 * Created by tarne on 22.05.16.
 */
class MessageRVAdapter(var chart: ChartReadModel, val userId: String)
    : RecyclerView.Adapter<MessageRVAdapter.MessageViewHolder>(){

    private val items = chart.messages

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MessageViewHolder?, position: Int) {
        if(holder == null) return
        holder.textMessage.text = items[position].message
        holder.textUsername.text = chart.participants.find { it.userId == items[position].userId }!!.userName
        val params = holder.layoutMessage.layoutParams as ViewGroup.MarginLayoutParams;
        val marginInPixes = holder.itemView.resources.getDimension(R.dimen.layout_message_margin)
        if(items[position].userId == userId){
            holder.imageUser.visibility = View.GONE
            holder.textUsername.visibility = View.GONE
            params.leftMargin = marginInPixes.toInt()
            params.rightMargin = 0
            holder.textMessage.backgroundColor = ContextCompat.getColor(holder.itemView.context, R.color.colorYourMessage)
        }else{
            params.rightMargin = marginInPixes.toInt()
            params.leftMargin = 0
            holder.textMessage.backgroundColor = ContextCompat.getColor(holder.itemView.context, R.color.colorMessage)
            holder.imageUser.setImageResource(R.drawable.default_logo)
            holder.imageUser.visibility = View.VISIBLE
            holder.textUsername.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MessageViewHolder? {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_message, parent, false)
        return MessageRVAdapter.MessageViewHolder(view)
    }

    class MessageViewHolder : RecyclerView.ViewHolder{
        val textMessage: TextView
        val textUsername: TextView
        val imageUser: CircleImageView
        val layoutMessage: LinearLayout

        constructor(itemView: View) : super(itemView) {
            textMessage = itemView.find<TextView>(R.id.textview_message)
            textUsername = itemView.find<TextView>(R.id.textview_username)
            imageUser = itemView.find<CircleImageView>(R.id.image_user)
            layoutMessage = itemView.find<LinearLayout>(R.id.layout_message)
        }
    }
}
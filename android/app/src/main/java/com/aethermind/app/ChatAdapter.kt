package com.aethermind.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_USER   = 0
        private const val VIEW_AETHER = 1
    }

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.msgText)
    }

    inner class AetherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.msgText)
    }

    override fun getItemViewType(position: Int) =
        if (messages[position].sender == ChatMessage.Sender.USER) VIEW_USER else VIEW_AETHER

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_USER) {
            UserViewHolder(inflater.inflate(R.layout.item_msg_user, parent, false))
        } else {
            AetherViewHolder(inflater.inflate(R.layout.item_msg_aether, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        when (holder) {
            is UserViewHolder   -> holder.text.text = msg.text
            is AetherViewHolder -> holder.text.text = msg.text
        }
    }

    override fun getItemCount() = messages.size
}

package com.example.messangerapp.recycleview

import android.content.Context
import android.text.format.DateFormat
import com.example.messangerapp.R
import com.example.messangerapp.model.TextMessage
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.sender_item_text_message.*

class SenderTextMessageItems(private val textMessage: TextMessage,
                             private val messageId: String,
                             val context: Context)
    : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.text_message.text = textMessage.text
        viewHolder.text_time_message.text = DateFormat.format("hh:mm a", textMessage.date).toString()
    }

    override fun getLayout() =  R.layout.sender_item_text_message
}
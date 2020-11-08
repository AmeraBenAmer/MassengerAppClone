package com.example.messangerapp.recycleview

import android.content.Context
import android.text.format.DateFormat
import com.bumptech.glide.Glide
import com.example.messangerapp.R
import com.example.messangerapp.model.ImageMessage
import com.example.messangerapp.model.TextMessage
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.sender_item_image_message.*
import kotlinx.android.synthetic.main.sender_item_text_message.*

class SenderImageMessageItem (private val imageMessage: ImageMessage,
                        private val messageId: String,
                        val context: Context)
    : Item() {

    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.text_view_message_time.text = DateFormat.format("hh:mm a", imageMessage.date).toString()

        if (imageMessage.imagePath.isNotEmpty()){
            Glide.with(context)
                .load(storageInstance.getReference(imageMessage.imagePath))
                .placeholder(R.drawable.ic_image)
                .into(viewHolder.image_view_message_image)
        }
    }

    override fun getLayout() =  R.layout.sender_item_image_message
}
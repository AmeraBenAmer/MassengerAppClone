package com.example.messangerapp.recycleview

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.example.messangerapp.R
import com.example.messangerapp.model.Users
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.recycler_view_item.*


class ChatItemRecycleView(
    val uid: String,
    val user: Users,
    val context: Context
): Item() {

    private val fireStoreInstant: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val currentUserDocRef: DocumentReference
    get() = fireStoreInstant.document("users/$uid")

    private val fireStorageInstance:FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.item_message_time_rv.text = "12:20"
        viewHolder.item_message_user_rv.text = "last message..."
//        getCurrentUser{user ->


//
        Log.i("name is ", user.name)
        getCurrentUser {
            viewHolder.item_name_user_rv.text = user.name
            if (user.profileImage.isNotEmpty()){
                Glide.with(context)
                    .load(fireStorageInstance.getReference(user.profileImage))
                    .into(viewHolder.item_image_user_rv)
            }else{
                viewHolder.item_image_user_rv.setImageResource(R.drawable.ic_account_circle)
            }
        }


    }

    private fun getCurrentUser(onComplete: (Users)-> Unit) {

        currentUserDocRef.get().addOnSuccessListener {
            onComplete(it.toObject(Users::class.java)!!)
        }
    }

    override fun getLayout(): Int {

        return R.layout.recycler_view_item
    }
}
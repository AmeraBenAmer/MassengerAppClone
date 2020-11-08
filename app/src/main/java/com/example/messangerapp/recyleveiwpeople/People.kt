package com.example.messangerapp.recyleveiwpeople

import android.content.Context
import com.bumptech.glide.Glide
import com.example.messangerapp.R
import com.example.messangerapp.model.Users
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.recycler_view_item.*
import kotlinx.android.synthetic.main.recycler_view_users.*

class People(
    val  user: Users,
    val context: Context):Item() {

    private val fireStorageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.name_user_fr_people.text = user.name

        if (user.profileImage.isNotEmpty()){
            Glide.with(context)
                .load(fireStorageInstance.getReference(user.profileImage))
                .into(viewHolder.image_user_fr_people)
        }else{
            viewHolder.image_user_fr_people.setImageResource(R.drawable.ic_account_circle)
        }
    }

    override fun getLayout(): Int {
        return R.layout.recycler_view_users
    }
}
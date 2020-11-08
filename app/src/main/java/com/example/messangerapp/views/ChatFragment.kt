package com.example.messangerapp.views


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messangerapp.ChatActivity
import com.example.messangerapp.ProfileActivity

import com.example.messangerapp.R
import com.example.messangerapp.model.Users
import com.example.messangerapp.recycleview.ChatItemRecycleView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_chat.*

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {

    private val fireStoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var chatSection: Section

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val titleToolbar = activity?.findViewById<TextView>(R.id.title_toolbar)
        titleToolbar?.text = getString(R.string.chats)

        val circleImageProfile = activity?.findViewById<ImageView>(R.id.image_main_profile_user)
        circleImageProfile?.setOnClickListener{
            startActivity(Intent(activity, ProfileActivity::class.java))
            activity!! .finish()
        }


       // listening Of Chats
        addChatListener(::initRecycleView)


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

//    into function onListen get param type List type Item no return data
    private fun addChatListener(onListen: (List<Item>) -> Unit): ListenerRegistration {

        return fireStoreInstance.collection("users")
//                for get people chat with you bas amera
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("sharedChat")
            .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->

            if (firebaseFirestoreException != null){
                return@addSnapshotListener
            }

            val items = mutableListOf<Item>()

            querySnapshot!!.documents.forEach {document ->
                // all loop get one document
                // convert document to users call
//                /every document .id is uid to user
                if (document.exists()){
                    items.add(ChatItemRecycleView(document.id, document.toObject(Users::class.java)!!, activity!!))

                }
            }

            onListen(items)
        }
    }

    private fun initRecycleView(item:List<Item>){
        chat_recycle_view_message!!.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = GroupAdapter<GroupieViewHolder>().apply {

//                Section class use to get data item and put in adapter
                  chatSection = Section(item)

                 add(chatSection)
                    setOnItemClickListener(onItemClick)
            }
        }
    }

    private val onItemClick = OnItemClickListener{item, view ->


        if (item is ChatItemRecycleView){


            val intentChatActivity = Intent(activity, ChatActivity::class.java)
            intentChatActivity.putExtra("user_name", item.user.name)
            intentChatActivity.putExtra("profile_image", item.user.profileImage)
            intentChatActivity.putExtra("uid", item.uid)

            activity!!.startActivity(intentChatActivity)
        }



    }
}

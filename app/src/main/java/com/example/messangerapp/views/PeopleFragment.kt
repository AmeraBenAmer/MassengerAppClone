package com.example.messangerapp.views


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.messangerapp.R
import com.example.messangerapp.model.Users
import com.example.messangerapp.recyleveiwpeople.People
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_people.*

/**
 * A simple [Fragment] subclass.
 */
class PeopleFragment : Fragment() {

    private val firestoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private lateinit var peopleSection :Section

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val titleToolbar = activity?.findViewById(R.id.title_toolbar) as TextView
        titleToolbar.text = getString(R.string.people)

        addUsers(::initRecycleView)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    private fun addUsers(onListen: (List<Item>) -> Unit):ListenerRegistration {

        return firestoreInstance.collection("users").addSnapshotListener { querySnapshot, firebaseFirestoreException ->

            if (firebaseFirestoreException != null){
                return@addSnapshotListener
            }
            val items = mutableListOf<Item>()

            querySnapshot!!.documents.forEach{ user->
                items.add(People(user.toObject(Users::class.java)!!, activity!!))
            }
            onListen(items)
        }
    }

    private fun initRecycleView(item: List<Item>){
        people_recycle_view!!.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = GroupAdapter<GroupieViewHolder>().apply {
                peopleSection = Section(item)
                add(peopleSection)
            }
        }
    }


}

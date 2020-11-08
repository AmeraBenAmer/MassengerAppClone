package com.example.messangerapp.views


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.messangerapp.R

/**
 * A simple [Fragment] subclass.
 */
class MoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val titleToolbar = activity?.findViewById(R.id.title_toolbar) as TextView
        titleToolbar.text = getString(R.string.discover)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false)
    }


}

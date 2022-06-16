package com.example.blchatclone.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blchatclone.R
import com.example.blchatclone.adapters.UserChatAdapter
import com.example.blchatclone.databinding.FragmentChatBinding
import com.example.blchatclone.models.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private var userList = ArrayList<Users>()
    private lateinit var db: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentChatBinding.inflate(inflater, container, false)
        db = FirebaseDatabase.getInstance()

        val adapter = context?.let { UserChatAdapter(userList, it) }

        binding.rcvChat.adapter = adapter
        binding.rcvChat.layoutManager = LinearLayoutManager(this.context)

        db.reference.child("Users").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(data in snapshot.children){
                    val user = data.getValue(Users::class.java)
                    user?.userId = data.key.toString()
                    if (user != null) {
                        userList.add(user)
                    }
                }
                adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()


    }
}
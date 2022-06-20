package com.example.blchatclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blchatclone.R
import com.example.blchatclone.models.Users
import de.hdodenhof.circleimageview.CircleImageView

class UserChatAdapter(userList: ArrayList<Users>,context: Context): RecyclerView.Adapter<UserChatAdapter.UserChatHolder>() {

    private var userList = ArrayList<Users>()
    private var context: Context
    private lateinit var activity: AppCompatActivity

    init {
        this.userList = userList
        this.context = context
    }


    class UserChatHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val profilePic: CircleImageView = itemView.findViewById(R.id.ivUserProfilePic)
        val userName: TextView = itemView.findViewById(R.id.tvChatUserName)
        val userLastText: TextView = itemView.findViewById(R.id.tvChatLastText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChatHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_chat, parent, false)
        return UserChatHolder(view)
    }

    override fun onBindViewHolder(holder: UserChatHolder, position: Int) {
        val currentItem = userList[position]

        Glide.with(context)
            .load(currentItem.profilePic)
            .centerCrop()
            .placeholder(R.drawable.avatar3)
            .into(holder.profilePic)

        holder.userName.text = currentItem.userName
        holder.userLastText.text = currentItem.lastMessage

//        holder.itemView.setOnClickListener {
//            activity = context as AppCompatActivity
//            activity.supportFragmentManager.beginTransaction()
//                .replace(R.id.flFragment, ChatFragment1())
//                .addToBackStack(null)
//                .commit()
//        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}
package com.example.blchatclone.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.blchatclone.fragments.CallsFragment
import com.example.blchatclone.fragments.ChatUsersFragment
import com.example.blchatclone.fragments.StatusFragment

class FragmentAdapter(private val fm: FragmentManager, private val lifecycle: Lifecycle): FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> ChatUsersFragment()
            1 -> StatusFragment()
            2 -> CallsFragment()
            else -> ChatUsersFragment()
        }
    }

}
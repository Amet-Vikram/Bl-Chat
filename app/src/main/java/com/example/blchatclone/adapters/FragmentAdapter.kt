package com.example.blchatclone.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.blchatclone.fragments.CallsFragment
import com.example.blchatclone.fragments.ChatFragment
import com.example.blchatclone.fragments.StatusFragment

class FragmentAdapter(private val fm: FragmentManager, private val lifecycle: Lifecycle): FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> ChatFragment()
            1 -> StatusFragment()
            2 -> CallsFragment()
            else -> ChatFragment()
        }
    }

}
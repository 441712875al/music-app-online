package com.example.music.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.music.MainActivity
import com.example.music.R
import com.example.music.adapter.MusicRecycleViewAdapter
import com.example.music.pojo.Music
import com.google.android.material.bottomnavigation.BottomNavigationView


class MusicListFragment(val musicList:List<Music>,resId:Int):BaseFragment(resId) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        configRecycleListView(view!!)
        return view
    }

    /*配置显示音乐列表的ListView*/
    private fun configRecycleListView(view: View){
        val musicListView = view.findViewById<RecyclerView>(R.id.musicListView)
        musicListView.layoutManager = LinearLayoutManager(context)
        val adapter = MusicRecycleViewAdapter(musicList)
        musicListView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

}
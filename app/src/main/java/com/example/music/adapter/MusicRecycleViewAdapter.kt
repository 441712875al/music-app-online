package com.example.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.music.R
import com.example.music.entity.Music

class MusicRecycleViewAdapter(val musicList:List<Music>):RecyclerView.Adapter<MusicRecycleViewAdapter.ViewHolder>() {

    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val nameTxt: TextView = view.findViewById<TextView>(R.id.music_name)
        val singerTxt: TextView = view.findViewById<TextView>(R.id.music_author)
        val musicNum: TextView = view.findViewById<TextView>(R.id.playlist_cover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.music_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val music = musicList[position]
        holder.musicNum.text = "${position+1}"
        holder.nameTxt.text = music.name
        holder.singerTxt.text = music.author

    }

    override fun getItemCount() = musicList.size

}
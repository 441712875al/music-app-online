package com.example.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.music.R
import com.example.music.entity.Playlist
import com.example.music.utils.LocalFileUtil
import com.makeramen.roundedimageview.RoundedImageView

class RecomPlaylistAdapter(val playlists: List<Playlist>, val context: AppCompatActivity):RecyclerView.Adapter<RecomPlaylistAdapter.ViewHolder>() {
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val playlistCover: RoundedImageView = view.findViewById<RoundedImageView>(R.id.recom_playlist_cover)
        val playCount: TextView = view.findViewById<TextView>(R.id.play_count)
        val name: TextView = view.findViewById<TextView>(R.id.playlist_desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recommend_playlist_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.playlistCover.setImageBitmap(LocalFileUtil.loadImage(playlist.coverImgUrl,context))
        holder.playCount.text = "播放量: ${playlist.playCout/10000}万"
        holder.name.text = playlist.name
    }

    override fun getItemCount() = playlists.size
}
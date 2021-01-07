package com.example.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.music.R
import com.example.music.entity.Playlist
import com.example.music.utils.LocalFileUtil

class PlaylistAdapter(private val playlists: List<Playlist>, val context:AppCompatActivity):RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val playlistCover: ImageView = view.findViewById<ImageView>(R.id.playlist_cover)
        val playlistName: TextView = view.findViewById<TextView>(R.id.playlist_name)
        val trackCount: TextView = view.findViewById<TextView>(R.id.track_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.playlistCover.setImageBitmap(LocalFileUtil.loadImage(playlist.coverImgUrl,context))
        holder.playlistName.text = playlist.name
        holder.trackCount.text = playlist.trackCount.toString().plus("首")
    }

    override fun getItemCount() = playlists.size
}
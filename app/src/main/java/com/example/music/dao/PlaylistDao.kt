package com.example.music.dao

import androidx.room.*
import com.example.music.entity.Playlist


@Dao
interface PlaylistDao {
    @Insert
    fun insertPlaylist(playlist:Playlist):Long

    @Update
    fun updatePlaylist(playlist: Playlist)

    @Delete
    fun deletePlaylist(playlist: Playlist)

    @Query("select * from Playlist")
    fun loadAll():List<Playlist>

    @Query("select * from Playlist where isLove = 1")
    fun getLove():Playlist

    @Query("delete from playlist")
    fun deleteAll()
}
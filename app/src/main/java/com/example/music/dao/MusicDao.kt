package com.example.music.dao

import androidx.room.*
import com.example.music.entity.Music

@Dao
interface MusicDao {

    @Insert
    fun insertMusic(music:Music):Long

    @Update
    fun updateMusic(music:Music)

    @Delete
    fun deleteMusic(music: Music)


    @Query("select * from Music where playListId = :playListId ORDER BY id desc")
    fun queryByPlayListId(playListId:Long):List<Music>

    @Query("delete from Music where musicId = :musicId and playListId = :playListId")
    fun deleteMusicByMusicId(musicId: Long,playListId: Long)

    @Query("delete from Music")
    fun deleteAll()
}
package com.example.music.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * @author along
 * @desc 歌单信息
 * @param playListId
 * @param name
 * @param coverImgUrl
 * @param trackCount
 * @param playCout
 * @param isLove
 */
@Entity
data class Playlist(@SerializedName("id")val playListId: Long, val name: String,
                    val coverImgUrl: String, var trackCount:Long, val playCout:Long, val isLove: Boolean = false){
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
}
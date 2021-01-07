package com.example.music.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


/**
 * @author along
 * @param name 歌名
 * @param author 歌手
 * @param imageId 音乐封面文件的资源号
 * @param resName 音乐资源文件名称
 */
@Parcelize
@Entity
data class Music(val musicId:Long, val name:String, val author:String, val imageURL: String?,var playListId:Long = 0):Parcelable{
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
}
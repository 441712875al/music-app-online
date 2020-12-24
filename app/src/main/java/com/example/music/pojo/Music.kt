package com.example.music.pojo

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.net.URI
import java.net.URL


/**
 * @author along
 * @param name 歌名
 * @param author 歌手
 * @param imageId 音乐封面文件的资源号
 * @param resName 音乐资源文件名称
 */
@Parcelize
data class Music(val musicId:Long, val name:String, val author:String, val imageURL: String?):Parcelable
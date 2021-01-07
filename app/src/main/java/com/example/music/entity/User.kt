package com.example.music.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * @author along
 * @desc 用户的信息
 * @param userId
 * @param gender
 * @param birthday
 * @param avatarUrl
 * @param createTime
 * @param nickname
 * @param signature
 */
@Parcelize
@Entity
data class User(val userId:String,var gender:Int,var birthday:Long,
           var avatarUrl:String,val createTime:Long,var nickname:String,
           var signature:String):Parcelable{
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
}
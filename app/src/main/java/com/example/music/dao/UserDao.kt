package com.example.music.dao

import androidx.room.*
import com.example.music.entity.User


@Dao
interface UserDao {
    @Insert
    fun insertUser(user: User):Long

    @Update
    fun updateUser(newUser:User)

    @Query("delete from User")
    fun deleteUser():Int

    /*永远只保存一条数据*/
    @Query("select * from User")
    fun getUser():List<User>
}
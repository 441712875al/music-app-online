package com.example.music

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.music.dao.MusicDao
import com.example.music.dao.PlaylistDao
import com.example.music.dao.UserDao
import com.example.music.entity.Music
import com.example.music.entity.Playlist
import com.example.music.entity.User

@Database(version = 1,entities = [User::class,Music::class,Playlist::class])
abstract class AppDatabase : RoomDatabase(){
    abstract fun userDao():UserDao
    abstract fun musicDao():MusicDao
    abstract fun playlistDao():PlaylistDao


    companion object{
        private var instance:AppDatabase? = null

        @Synchronized
        fun getDatabase(context: Context):AppDatabase{
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java,"music_database")
                .build().apply {
                instance = this
            }
        }
    }
}
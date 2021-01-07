package com.example.music.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.ImageView
import com.example.music.activity.MusicPlayActivity
import com.example.music.R


/**
 * 处理歌曲切换和开始暂停的标准广播
 * @author along
 * @param activity 主活动
 */
class MusicSwitchBroacast(val activity: MusicPlayActivity):BroadcastReceiver() {
    /**
     * action
     */
    companion object{
        @JvmStatic
        val START_MUSIC = "com.example.music_app.START_MUSIC"

        @JvmStatic
        val STOP_MUSIC = "com.example.music_app.STOP_MUSIC"

        @JvmStatic
        val NEXT_MUSIC = "com.example.music_app.NEXT_MUSIC"

        @JvmStatic
        val PRECIOUS_MUSIC = "com.example.music_app.PRECIOUS_MUSIC"
    }


    /**
     * @param context
     * @param intent
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            START_MUSIC -> activity.findViewById<ImageView>(R.id.playBtn)
                ?.callOnClick()

            STOP_MUSIC -> activity.findViewById<ImageView>(R.id.playBtn)
                ?.callOnClick()

            NEXT_MUSIC ->activity.findViewById<ImageView>(R.id.nextBtn)
                ?.callOnClick()

            PRECIOUS_MUSIC ->activity.findViewById<ImageView>(R.id.preciousBtn)
                ?.callOnClick()
        }
    }
}
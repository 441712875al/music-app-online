package com.example.music.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Message
import android.os.SystemClock.sleep
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.music.MainActivity
import com.example.music.pojo.Music
import com.example.music.MusicPlayActivity
import com.example.music.R
import com.example.music.broadcasts.MusicSwitchBroacast
import com.example.music.json.LyricResponse
import com.example.music.json.MusicUrlReponse
import com.example.music.pojo.LrcInfo
import com.example.music.utils.LocalFileUtil
import com.example.music.utils.LrcParser
import com.example.music.utils.ServiceCreator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MusicService : Service() {

    private lateinit var musicPlayBinder : MusicPlayBinder

    override fun onBind(intent: Intent): IBinder {
        return musicPlayBinder
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("MusicService -> ","onCreate()")
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val remoteViews = configRemoteViews()


        /*是否需要创建通知渠道*/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel("my_service","前台通知", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }


        /*创建点击通知栏的意图，回到主活动*/
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this,0,intent,0)


        /*创建通知栏*/
        val notification = NotificationCompat.Builder(this,"my_service")
            .setContentTitle("网抑云音乐")
            .setContentText("内容")
            .setSmallIcon(R.mipmap.music_app_icon)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.avatar))
            .setContent(remoteViews)
            .setContentIntent(pi)
            .build()

        startForeground(MusicPlayBinder.notificationID,notification)
        musicPlayBinder = MusicPlayBinder(manager,notification)
    }



    /**
     * 注册通知栏的音乐切换的监听器
     * @return 返回一个加载好通知栏视图资源的RemoteViews对象
     */
    private fun configRemoteViews() : RemoteViews{

        val remoteViews = RemoteViews(packageName, R.layout.music_notification)

        /*创建音乐切换的意图*/
        val intentTStart = Intent(MusicSwitchBroacast.START_MUSIC)
        val intentStop = Intent(MusicSwitchBroacast.STOP_MUSIC)
        val intentNext = Intent(MusicSwitchBroacast.NEXT_MUSIC)
        val intentPrecious = Intent(MusicSwitchBroacast.PRECIOUS_MUSIC)

        /*实例化广播*/
        val piStart = PendingIntent.getBroadcast(this,0,intentTStart,0)
        val piStop = PendingIntent.getBroadcast(this,0,intentStop,0)
        val piNext = PendingIntent.getBroadcast(this,0,intentNext,0)
        val piPrecious = PendingIntent.getBroadcast(this,0,intentPrecious,0)

        /*创建监听器，实现当点击通知栏的音乐切换按钮后发送广播*/
        remoteViews.setOnClickPendingIntent(R.id.notifyStart,piStart)
        remoteViews.setOnClickPendingIntent(R.id.notifyStop,piStop)
        remoteViews.setOnClickPendingIntent(R.id.notifyPrecious,piPrecious)
        remoteViews.setOnClickPendingIntent(R.id.notifyNext,piNext)
        return remoteViews
    }



    override fun onDestroy() {
        super.onDestroy()
        Log.e("MusicService ->","onDestroy()")
    }



    /**
     * 处理音乐播放的Binder
     * @author along
     * @param notificationManager 通知管理者
     * @param notification 需要创建的通知体
     */
    class MusicPlayBinder(val notificationManager: NotificationManager,
    val notification: Notification) : Binder(){

        var mediaPlayer = MediaPlayer()
        var retrofit = ServiceCreator.create(MusicHttpService::class.java)

        private val lrcParser = LrcParser()

        lateinit var musicList : List<Music>

        lateinit var activity: MusicPlayActivity

        var lrcInfo = LrcInfo()

        var musicIx = 0

        companion object{
            @JvmStatic
            var reflushTime = 1L
            @JvmStatic
            val notificationID = 1000
        }


        /**
         * 初始化参数
         * @param musicList 模拟音乐数据库
         * @param musicIx 当前选择的音乐id
         * @param assetManager 资源管理器，用来加载asset文件夹的资源
         */
        fun init(musicList:List<Music>,musicIx:Int,activity: MusicPlayActivity){
            this.musicList = musicList
            this.musicIx = musicIx
            this.activity = activity
        }


        /**
         * 做一些准备播放的工作，如销毁前一个播放的mediaPlayer（如果存在的话）、
         * 加载MP3和lrc文件、重新定义通知栏的内容
         */
        fun preparePlay(){
            try{
                if(mediaPlayer.isPlaying){
                    mediaPlayer.stop()
                }
                activity.curThread?.interrupt()
                if(activity.curThread==null){
                    Log.e("Service","线程为null")
                }
            }catch (e: Exception){
                //什么也不做
                e.printStackTrace()
            }

            val music = musicList[musicIx]
            loadMusicFile(music)
            downloadLyric(music.musicId)

            val remoteViews = notification.contentView
            remoteViews.setTextViewText(R.id.notifyMusicName,music.name)
            remoteViews.setTextViewText(R.id.notifyMusicAuthor,music.author)

            /*需要下载图片*/
            try{
                val imageBitmap = LocalFileUtil.loadImage(music.imageURL,activity)
                remoteViews.setImageViewBitmap(R.id.notifyCover,imageBitmap)
            }catch (e:Exception){
                remoteViews.setImageViewResource(R.id.notifyCover, R.drawable.default_back)
            }
            notificationManager.notify(notificationID,notification)
        }


        /**
         * 开始在后台下载音乐文件
         */
        private fun loadMusicFile(music:Music){
            /*获取音乐的mp3文件和歌词文件*/
            retrofit.getSong(music.musicId.toString()).enqueue(object : Callback<MusicUrlReponse> {
                override fun onResponse(call: Call<MusicUrlReponse>, response: Response<MusicUrlReponse>) {
                    val musicUrlStr = response.body()?.data?.get((0))?.url
                    Log.e("Service","歌曲下载完毕${musicUrlStr}")
                    startPlay(musicUrlStr!!)
                }
                override fun onFailure(call: Call<MusicUrlReponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }


        private fun downloadLyric(musicId: Long){
                retrofit.getLyric(musicId.toString()).enqueue(object : Callback<LyricResponse> {
                    override fun onResponse(
                        call: Call<LyricResponse>,
                        response: Response<LyricResponse>
                    ) {
                        val lyricText = response.body()?.lrc?.lyric
                        /*到这里两个文件已经下载好了，发送消息给主线程，执行播放*/
                        Log.e("Main", "文件下载完毕")
                        lrcInfo = lrcParser.parse(lyricText)
                    }
                    override fun onFailure(call: Call<LyricResponse>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
        }

        
        /**
         * 开始播放音乐
         */
        fun startPlay(mp3fileUrl:String){
            try{
                mediaPlayer.reset()
                mediaPlayer.setDataSource(mp3fileUrl)
                mediaPlayer.prepare()
                mediaPlayer.start()

                /*配置进度条的属性*/
                activity.configPregressBar()
                /*实时刷新进度条*/
                activity.reflushProgress()
            }catch (e:Exception){
                mediaPlayer = MediaPlayer()
                startPlay(mp3fileUrl)
            }
        }




        /**
         * 恢复播放
         */
        fun resumePlay(){
            mediaPlayer.start()
        }

        /**
         * 停止播放音乐
         */
        fun pausePlay(){
            mediaPlayer.pause()
        }



        /**
         * 切换下一首音乐
         */
        fun nextMusic(){
            if(musicIx<musicList.size){
                ++musicIx
                preparePlay()
            }
        }



        /**
         * 切换上一首音乐
         */
        fun preciousMusic(){
            if(musicIx>0){
                --musicIx
                preparePlay()
            }
        }


    }
}

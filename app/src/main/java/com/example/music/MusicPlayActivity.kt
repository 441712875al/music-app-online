package com.example.music

import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.SeekBar
import android.widget.Toast
import com.example.music.broadcasts.MusicSwitchBroacast
import com.example.music.json.LoveResponse
import com.example.music.pojo.Music
import com.example.music.services.MusicHttpService
import com.example.music.services.MusicService
import com.example.music.utils.HttpDownloadUtil
import com.example.music.utils.LocalFileUtil
import com.example.music.utils.ServiceCreator
import kotlinx.android.synthetic.main.activity_music_play.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileInputStream

class MusicPlayActivity : AppCompatActivity() {

    private var musicIx:Int = 0//当前播放的音乐的索引

    var musicPlayBinder: MusicService.MusicPlayBinder? = null

    lateinit var myBroadcastReceiver : MusicSwitchBroacast

    lateinit var musicHttpService:MusicHttpService

    private val activity = this

    val updateProgress = 0x123

    var curThread:Thread? = null//歌曲播放线程

    var musicList = ArrayList<Music>()//主活动传来的待播放的歌曲

    var loveSongs = ArrayList<Music>()//记录收藏的歌曲



    /**
     * 消息处理器，用来处理进度条更新和歌词更新
     */
    val handler = object : Handler(){
        override fun handleMessage(msg: Message) {
            when(msg.what){
                updateProgress -> {
                    val currPos = musicPlayBinder?.mediaPlayer!!.currentPosition
                    seekBar.progress = currPos

                    /*更新歌词*/
                    musicPlayBinder?.lrcInfo?.infos?.let {
                        val lrcStr= it.get(currPos/1000)
                        if (lrcStr != null) {
                            if(lrcTxt!=null && !lrcStr.equals(lrcTxt.text)){
                                lrcTxt.text = lrcStr
                            }
                        }
                    }

                    /*更新时间*/
                    val timeTmp = seekBar.progress/1000
                    if(time!=null ){
                        time.text = "%02d:%02d".format(timeTmp/60,timeTmp%60)
                    }

                    /*播放完暂停*/
                    if(currPos == seekBar.max){
                        playBtn.setImageResource(R.drawable.stop)
                    }
                }
            }
        }
    }


    /**
     * service 连接，定义了连接后的处理事件，包括初始化音乐播放处理的Binder以及
     * 新建一个音乐播放的碎片musicFragment，然后将它推到主页面
     */
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicPlayBinder = service as MusicService.MusicPlayBinder
            musicPlayBinder!!.init(musicList,musicIx,activity)
            /*加载音乐*/
            loadMusic(musicIx)
        }


        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e("MusicService->","onServiceDisconnected()")
        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_play)
        supportActionBar?.hide()
        musicHttpService = ServiceCreator.create(MusicHttpService::class.java,this)

        musicIx = intent.getIntExtra("musicIx",0)
        musicList = intent.getParcelableArrayListExtra<Music>("musicList")!!
        loveSongs = intent.getParcelableArrayListExtra<Music>("loveSongs")!!

        /*开启并绑定服务，在服务连接后加载音乐*/
        val serviceIntent = Intent(this, MusicService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent,connection, Context.BIND_AUTO_CREATE)

        /*配置下控制音乐播放的按钮*/
        configMusicSwitchBtn()

        /*配置广播接收器*/
        configBroadcastReceiver()
    }


    /**
     * 加载页面的音乐文件布局内容和音乐资源
     * @param toReflushMusicIx 需要被加载的音乐文件的索引号
     */
    fun loadMusic(toReflushMusicIx: Int){

        musicIx = toReflushMusicIx
        val music = musicList[musicIx]

        /*重新设置音乐显示的相关控件*/
        music_cover.setImageBitmap(LocalFileUtil.loadImage(music.imageURL,this))
        music_name.text = music.name
        music_author.text = music.author
        playBtn.setImageResource(R.drawable.start)

        /*开始准备播放*/
        musicPlayBinder?.preparePlay()
    }



    /**
     * 实时向主线程发送刷新歌曲播放进度条的消息，从而更新UI
     */
    fun reflushProgress(){
        curThread = object : Thread() {
            override fun run() {
                while(musicPlayBinder?.mediaPlayer?.isPlaying!!){
                    SystemClock.sleep(MusicService.MusicPlayBinder.reflushTime)
                    val msg = Message.obtain()
                    msg.what = updateProgress
                    handler.sendMessage(msg)
                }
            }
        }
        curThread?.start()
    }


    /**
     * 配置SeekBar的相关属性和布局显示
     * @param mediaPlayer 已经加载了音乐文件的媒体播放者
     * @param view 本碎片的视图
     */
    fun configPregressBar(){

        val durationTime = musicPlayBinder?.mediaPlayer?.duration

        if (durationTime != null) {
            duration.text = "%02d:%02d".format(durationTime/1000/60,(durationTime/1000)%60)
            seekBar.max = durationTime
        }

        seekBar.progress = 0

        /*配置进度条拖动音乐随之改变*/
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                musicPlayBinder?.mediaPlayer?.seekTo(seekBar?.progress?:0)
            }
        })
    }


    /**
     * 配置音乐的切换按钮，实现上一首和下一首功能
     * @param view 本碎片的视图
     */
    private fun configMusicSwitchBtn(){

        preciousBtn.setOnClickListener{
            if(musicIx>0){
                musicPlayBinder?.preciousMusic()
                loadMusic(--musicIx)
            }else{
                Toast.makeText(this,"这是已经第一首歌曲", Toast.LENGTH_SHORT).show()
            }
        }

        nextBtn.setOnClickListener{
            if(musicIx<musicList.size){
                musicPlayBinder?.nextMusic()
                loadMusic(++musicIx)
            }else{
                Toast.makeText(this,"这是已经是一首歌曲", Toast.LENGTH_SHORT).show()
            }
        }
    }


    /**
     * 注册广播,监听通知栏中的点击事件
     */
    private fun configBroadcastReceiver(){
        myBroadcastReceiver = MusicSwitchBroacast(this)
        val intentFilter = IntentFilter(MusicSwitchBroacast.START_MUSIC)
        intentFilter.addAction(MusicSwitchBroacast.STOP_MUSIC)
        intentFilter.addAction(MusicSwitchBroacast.PRECIOUS_MUSIC)
        intentFilter.addAction(MusicSwitchBroacast.NEXT_MUSIC)

        registerReceiver(myBroadcastReceiver,intentFilter)
    }



    fun onClick(view: View){
        when(view.id){
            R.id.playBtn ->{
                if(!musicPlayBinder?.mediaPlayer?.isPlaying!!){
                    musicPlayBinder?.resumePlay()
                    reflushProgress()
                    playBtn.setImageResource(R.drawable.start)
                }else{
                    musicPlayBinder!!.pausePlay()
                    playBtn.setImageResource(R.drawable.stop)
                }
            }

            R.id.loveItem ->{
                val music = musicList[musicIx]
                if(loveSongs.contains(music)){
                    loveSongs.remove(music)
                    showMessage("已取消喜欢")
                }else{
                    musicHttpService.love(music.musicId.toString(),true).enqueue(object :
                        Callback<LoveResponse> {
                        override fun onFailure(call: Call<LoveResponse>, t: Throwable) {
                            t.printStackTrace()
                        }

                        override fun onResponse(
                            call: Call<LoveResponse>,
                            response: Response<LoveResponse>
                        ) {
                            val loveResponse = response.body()!!
                            if(loveResponse.code==200){
                                loveSongs.add(0,music)
                                showMessage("已加入喜欢")
                            }else{
                                showMessage(loveResponse.msg)
                            }
                        }
                    })
                }

            }


            R.id.downloadItem ->{
                showMessage("版权原因，歌曲不允许下载")
            }

            R.id.singItem ->{
                showMessage("该功能正在建设，敬请期待")
            }

            R.id.listItem ->{
                showMessage("该功能正在建设，敬请期待")
            }

            R.id.remarkItem ->{
                showMessage("该功能正在建设，敬请期待")
            }
        }
    }

    /**
     * 使用Toast显示提示信息
     * @param msg 需要显示的信息
     */
    private fun showMessage(msg:String){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        /*将收藏的歌曲返回给主活动*/
        val intent = Intent()
        intent.putParcelableArrayListExtra("loveSongs",ArrayList(loveSongs))
        setResult(RESULT_OK,intent)
        finish()
    }
}

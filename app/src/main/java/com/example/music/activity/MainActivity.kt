package com.example.music.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.music.AppDatabase
import com.example.music.R
import com.example.music.dao.MusicDao
import com.example.music.dao.PlaylistDao
import com.example.music.dao.UserDao
import com.example.music.fragment.*
import com.example.music.network.json.DailyRecomSongsResponse
import com.example.music.network.json.MusicTopListResponse
import com.example.music.network.json.PlaylistDetailResponse
import com.example.music.entity.Music
import com.example.music.entity.User
import com.example.music.network.service.MusicHttpService
import com.example.music.utils.LocalFileUtil
import com.example.music.services.ServiceCreator
import com.example.music.utils.ActivityUtil
import com.example.music.viewmodel.NavigationViewModel
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var homePageFragment:HomePageFragment
    lateinit var musicListFragment:MusicListFragment
    private lateinit var musicHomeFragment: MusicHomeFragment
    private val activity = this
    lateinit var musicHttpService: MusicHttpService
    private lateinit var user : User

    private val TAG = "Main"

    var themeId : Int? =null
    var userBitmap:Bitmap? = null

    private lateinit var userDao:UserDao
    private lateinit var playlistDao: PlaylistDao
    private lateinit var musicDao: MusicDao

    private lateinit var navigationViewModel: NavigationViewModel

    private fun initDao(){
        userDao = AppDatabase.getDatabase(this).userDao()
        playlistDao = AppDatabase.getDatabase(this).playlistDao()
        musicDao = AppDatabase.getDatabase(this).musicDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeId = intent.getIntExtra("theme", R.style.LightTheme)
        setTheme(themeId!!)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        musicHttpService = ServiceCreator.create(MusicHttpService::class.java,this)

        initDao()
        /*获取用户的基本数据*/
        thread {
            user = userDao.getUser()[0]
            userBitmap = LocalFileUtil.loadImage(user.avatarUrl,this)
            navigationViewModel.avatarBitmp.postValue(userBitmap)
            navigationViewModel.nickname.postValue(user.nickname)
            navigationViewModel.signature.postValue(user.signature)
        }

        /*提前创建需要用到的碎片*/
        homePageFragment = HomePageFragment(R.layout.home_framgment)
        musicHomeFragment = MusicHomeFragment()

        /*配置侧边栏*/
        configNavigationView()
        /*配置底部导航栏*/
        configBottomNavigation()
        /*加载主页碎片*/
        ActivityUtil.replaceFragment(this,R.id.mainFrag, homePageFragment,false)
    }



    /**
     * 获取侧边栏的视图，进行用户信息的配置
     */
    private fun configNavigationView(){
        navigationViewModel = ViewModelProvider(this)[NavigationViewModel::class.java]
        navigationViewModel.avatarBitmp.observe(this,{
            navigationView.findViewById<ImageView>(R.id.avatar).setImageBitmap(it)
        })
        navigationViewModel.nickname.observe(this,{
            navigationView.findViewById<TextView>(R.id.nickname).text = it
        })
        navigationViewModel.signature.observe(this,{
            navigationView.findViewById<TextView>(R.id.signature).text = it
        })


        navigationView.findViewById<ConstraintLayout>(R.id.themeItem).setOnClickListener {
            changeTheme()
        }

        navigationView.findViewById<ConstraintLayout>(R.id.teenagerModelItem).setOnClickListener {
            Toast.makeText(this,"功能正在建设！",Toast.LENGTH_SHORT).show()
        }

        navigationView.findViewById<ConstraintLayout>(R.id.closeInTimeItem).setOnClickListener {
            Toast.makeText(this,"功能正在建设！",Toast.LENGTH_SHORT).show()
        }

        navigationView.findViewById<ConstraintLayout>(R.id.helpItem).setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("音乐帮助")
                setMessage("app是基于网易云app实现，完全同步了网易云的数据，可以使用网易云的账号登录，" +
                        "特别声明：app仅用于学习使用，侵权请告知")
                setCancelable(false)
                setPositiveButton("确定"){dialog,which->}
                show()
            }
        }

        navigationView.findViewById<TextView>(R.id.exitBtn).setOnClickListener {
            AlertDialog.Builder(this).apply {
//                setMessage("确认退出当前账号？")
//                setNegativeButton("取消"){dialog,which->}
//                setPositiveButton("确定"){dialog,which->
//
//                }
//                show()
                thread {
                    userDao.deleteUser()
                    musicDao.deleteAll()
                    playlistDao.deleteAll()
                }

                val editor = getSharedPreferences("userConfig", Context.MODE_PRIVATE).edit()
                editor.putBoolean("isRemember", false)
                editor.apply()
                startActivity(Intent(activity,LoginActivity::class.java))
                finish()
            }
        }
    }


    /**
     * 改变当前的主题，light dark两种
     */
    private fun changeTheme(){
        finish()
        val intent = Intent(this,MainActivity::class.java)
        if(themeId == R.style.LightTheme){
            intent.putExtra("theme", R.style.DarkThem)
        }else{
            intent.putExtra("theme", R.style.LightTheme)
        }
        startActivity(intent)
    }

    /**
     * 配置底部导航栏
     */
    private fun configBottomNavigation() {
        /**创建选择之后的监听器*/
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.homePageItem -> {
                    ActivityUtil.replaceFragment(this,R.id.mainFrag,homePageFragment,false)
                }
                R.id.musicHomeItem -> ActivityUtil.replaceFragment(this,R.id.mainFrag,musicHomeFragment,false)
                R.id.userItem -> ActivityUtil.replaceFragment(this,R.id.mainFrag,ProfileFragment(user),false)
            }

            true
        }
    }



    fun onClick(view:View){
        when(view.id){
            /*获取排行榜歌曲信息*/
            R.id.rankList ->{
                showRankList()
            }

            /*获取每日推荐歌曲信息*/
            R.id.todayRecom ->{
                showDailySongs()
            }

            /*音乐点击后，跳转到播放页面*/
            R.id.music_item ->{
                musicListFragment.view.let{
                    val position = it?.findViewById<RecyclerView>(R.id.musicListView)?.getChildLayoutPosition(view)
                    val intent = Intent(this, MusicPlayActivity::class.java)
                    intent.putExtra("musicIx",position)
                    intent.putParcelableArrayListExtra("musicList", ArrayList(musicListFragment.viewModel.musicList.value))
                    intent.putExtra("theme",themeId)
                    /*开启音乐播放活动*/
                    startActivity(intent)
                }
            }

            /*点击自己创建的歌单，显示歌曲列表*/
            R.id.playlist_item ->{
                showPlaylistDetail(view)
            }

            /*获取喜欢的歌曲信息*/
            R.id.loveListLayout ->{
                thread {
                    val lovePlaylistId = playlistDao.getLove().playListId
                    val loveSongs = musicDao.queryByPlayListId(lovePlaylistId)
                    musicListFragment = MusicListFragment(loveSongs,R.layout.music_list_fragment)
                    ActivityUtil.replaceFragment(this,R.id.mainFrag,musicListFragment,true)
                }
            }


            /*点击推荐歌单，显示歌曲列表*/
            R.id.recom_playlist_item ->{
                musicHomeFragment.view?.let {
                    val position = it.findViewById<RecyclerView>(R.id.recom_playlist_view).getChildLayoutPosition(view)
                    val playlist = musicHomeFragment.recomPlaylists?.get(position)
                    musicHttpService.getPlaylistDetail(playlist?.playListId.toString()).enqueue(object : Callback<PlaylistDetailResponse>{
                        override fun onResponse(
                            call: Call<PlaylistDetailResponse>,
                            response: Response<PlaylistDetailResponse>
                        ) {
                            val playlistDetailResponse = response.body()!!
                            val musicList = ArrayList<Music>()
                            for(obj in playlistDetailResponse.playlist.tracks){
                                val id = obj.id
                                val name = obj.name
                                val authorList = obj.ar
                                var authors = ""
                                for(i in 0 until authorList.size){
                                    authors +=authorList[i].name
                                    if(i!=authorList.size-1){
                                        authors +="/"
                                    }
                                }
                                musicList.add(Music(id,name,authors,obj.al.picUrl))
                            }

                            musicListFragment = MusicListFragment(musicList,
                                R.layout.music_list_fragment
                            )
                            ActivityUtil.replaceFragment(activity,R.id.mainFrag,musicListFragment,true)
                        }

                        override fun onFailure(call: Call<PlaylistDetailResponse>, t: Throwable) {
                            t.printStackTrace()
                        }

                    })
                }
            }
        }
    }



    /**
     * 初始化音乐数据，包括实例化音乐列表碎片musicListFragment和musicList
     */
    private fun showRankList(){
        val musicList = ArrayList<Music>()
        /*获取了排行榜中歌曲信息*/
        musicHttpService.getTopList("19723756").enqueue(object : Callback<MusicTopListResponse> {
            override fun onResponse(call: Call<MusicTopListResponse>, response: Response<MusicTopListResponse>) {
                val data = response.body()
                for (obj in data?.playlist?.tracks!!){
                    val id = obj.id
                    val name = obj.name
                    val authorList = obj.ar
                    var authors = ""
                    for(i in 0 until authorList.size){
                        authors +=authorList[i].name
                        if(i!=authorList.size-1){
                            authors +="/"
                        }
                    }
                    musicList.add(Music(id,name,authors,obj.al.picUrl))
                }

                musicListFragment = MusicListFragment(musicList, R.layout.music_list_fragment)
                ActivityUtil.replaceFragment(activity,R.id.mainFrag,musicListFragment,true)
            }
            override fun onFailure(call: Call<MusicTopListResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    /**
     * 显示每日推荐歌曲列表
     */
    private fun showDailySongs(){
        val musicList = ArrayList<Music>()
        /*获取了排行榜中歌曲信息*/
        musicHttpService.getRecommendSongs().enqueue(object : Callback<DailyRecomSongsResponse> {
            override fun onResponse(call: Call<DailyRecomSongsResponse>, response: Response<DailyRecomSongsResponse>) {
                val data = response.body()?.data

                if(data?.dailySongs!=null){
                    for (obj in data.dailySongs){
                        val id = obj.id
                        val name = obj.name
                        val authorList = obj.ar
                        var authors = ""
                        for(i in 0 until authorList.size){
                            authors +=authorList[i].name
                            if(i!=authorList.size-1){
                                authors +="/"
                            }
                        }
                        musicList.add(Music(id,name,authors,obj.al.picUrl))
                    }

                    musicListFragment = MusicListFragment(musicList, R.layout.music_list_fragment)
                    ActivityUtil.replaceFragment(activity,R.id.mainFrag,musicListFragment,true)
                }
            }
            override fun onFailure(call: Call<DailyRecomSongsResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    /**
     * 显示歌单中的歌曲详情
     * @param view
     */
    private fun showPlaylistDetail(view:View){
        homePageFragment.view?.let {
            val position = it.findViewById<RecyclerView>(R.id.sheetListView).getChildLayoutPosition(view)
            val playlist = homePageFragment.viewModel.playlists.value?.get(position)

            thread {
                val musicList = musicDao.queryByPlayListId(playlist?.playListId!!)
                musicListFragment = MusicListFragment(musicList = musicList,resId = R.layout.music_list_fragment)
                ActivityUtil.replaceFragment(activity,R.id.mainFrag,musicListFragment,true)
            }
        }
    }

}
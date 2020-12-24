package com.example.music

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.music.fragment.*
import com.example.music.json.DailyRecomSongsResponse
import com.example.music.json.MusicTopListResponse
import com.example.music.json.PlaylistDetailResponse
import com.example.music.pojo.Music
import com.example.music.services.MusicHttpService
import com.example.music.utils.LocalFileUtil
import com.example.music.utils.ServiceCreator
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class MainActivity : AppCompatActivity() {

    private lateinit var homePageFragment:HomePageFragment
    private lateinit var musicListFragment:MusicListFragment
    private lateinit var musicHomeFragment: MusicHomeFragment

    lateinit var musicHttpService:MusicHttpService
    lateinit var uid :String
    var bitmap:Bitmap? = null
    private val PLAY_ACTIVITY_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        musicHttpService = ServiceCreator.create(MusicHttpService::class.java,this)

        /*获取用户的基本数据*/
        val profile = intent.getBundleExtra("profile")
        uid = profile?.getString("userId","")!!
        bitmap = LocalFileUtil.loadImage(profile.getString("avatarUrl"),this)

        /*提前创建需要用到的碎片*/
        homePageFragment = HomePageFragment(R.layout.home_framgment,profile)
        musicHomeFragment = MusicHomeFragment()

        /*配置侧边栏*/
        configNavigationView(profile)
        /*配置底部导航栏*/
        configBottomNavigation(profile)
        /*加载主页碎片*/
        replaceFragment(R.id.mainFrag, homePageFragment,false)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            PLAY_ACTIVITY_CODE ->if(resultCode == RESULT_OK){
                val loveSongsList = data?.getParcelableArrayListExtra<Music>("loveSongs")!!
                homePageFragment.loveSongs = loveSongsList
            }
        }
    }

    /**
     * 替换主布局文件中的FrameLayout，实现页面的动态加载功能
     *
     * @param fragmentId 需要被替换FrameLayout的id
     * @param fragment 已经加载了需要被显示的布局文件的Fragment类
     * @param push 是否将操作加入返回栈
     */
    fun replaceFragment(fragmentId:Int, fragment: Fragment, push:Boolean){
        val transition = supportFragmentManager.beginTransaction()
        transition.replace(fragmentId,fragment)

        /*是否将这次的操作加入栈中*/
        if(push)
            transition.addToBackStack(null)
        transition.commit()
    }

    /**
     * 获取侧边栏的视图，进行用户信息的配置
     */
    private fun configNavigationView(profile:Bundle?){
        navigationView.findViewById<ImageView>(R.id.avatar).setImageBitmap(bitmap)
        navigationView.findViewById<TextView>(R.id.nickname).text = profile?.getString("nickname")
        navigationView.findViewById<TextView>(R.id.signature).text = profile?.getString("signature")
    }


    /**
     * 配置底部导航栏
     */
    private fun configBottomNavigation(profile: Bundle?) {
        /**创建选择之后的监听器*/
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.homePageItem -> replaceFragment(R.id.mainFrag,homePageFragment,false)
                R.id.musicHomeItem -> replaceFragment(R.id.mainFrag,musicHomeFragment,false)
                R.id.userItem -> replaceFragment(R.id.mainFrag,ProfileFragment(profile),false)
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
                while(homePageFragment.loveSongs==null){
                    Toast.makeText(this,"请等待程序加载完毕",Toast.LENGTH_LONG).show()
                }
                musicListFragment.view.let{
                    val position = it?.findViewById<RecyclerView>(R.id.musicListView)?.getChildLayoutPosition(view)
                    val intent = Intent(this,MusicPlayActivity::class.java)
                    intent.putExtra("musicIx",position)
                    intent.putParcelableArrayListExtra("musicList", ArrayList(musicListFragment!!.musicList))
                    intent.putParcelableArrayListExtra("loveSongs",homePageFragment.loveSongs)
                    /*开启音乐播放活动*/
                    startActivityForResult(intent,PLAY_ACTIVITY_CODE)
                }
            }

            /*点击自己创建的歌单，显示歌曲列表*/
            R.id.playlist_item->{
                homePageFragment.view?.let {
                    val position = it.findViewById<RecyclerView>(R.id.sheetListView).getChildLayoutPosition(view)
                    val playlist = homePageFragment.playlists?.get(position)
                    musicHttpService.getPlaylistDetail(playlist?.id.toString()).enqueue(object : Callback<PlaylistDetailResponse>{
                        override fun onResponse(
                            call: Call<PlaylistDetailResponse>,
                            response: Response<PlaylistDetailResponse>
                        ) {
                            val playlistDetailResponse = response.body()!!
                            val musicList = ArrayList<Music>()
                            var i = 1//rank
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

                            musicListFragment = MusicListFragment(musicList,R.layout.music_list_fragment)
                            replaceFragment(R.id.mainFrag,musicListFragment,true)
                        }

                        override fun onFailure(call: Call<PlaylistDetailResponse>, t: Throwable) {
                            t.printStackTrace()
                        }

                    })
                }
            }

            /*获取喜欢的歌曲信息*/
            R.id.loveListLayout ->{
                if(homePageFragment.loveSongs == null){
                    musicListFragment = MusicListFragment(homePageFragment.loveSongs!!,R.layout.music_list_fragment)
                    replaceFragment(R.id.mainFrag,musicListFragment,true)
                }else{
                    musicListFragment = MusicListFragment(homePageFragment.loveSongs!!,R.layout.music_list_fragment)
                    replaceFragment(R.id.mainFrag,musicListFragment,true)
                }
            }

            /*点击推荐歌单，显示歌曲列表*/
            R.id.recom_playlist_item ->{
                musicHomeFragment.view?.let {
                    val position = it.findViewById<RecyclerView>(R.id.recom_playlist_view).getChildLayoutPosition(view)
                    val playlist = musicHomeFragment.recomPlaylists?.get(position)
                    musicHttpService.getPlaylistDetail(playlist?.id.toString()).enqueue(object : Callback<PlaylistDetailResponse>{
                        override fun onResponse(
                            call: Call<PlaylistDetailResponse>,
                            response: Response<PlaylistDetailResponse>
                        ) {
                            val playlistDetailResponse = response.body()!!
                            val musicList = ArrayList<Music>()
                            var i = 1//rank
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

                            musicListFragment = MusicListFragment(musicList,R.layout.music_list_fragment)
                            replaceFragment(R.id.mainFrag,musicListFragment,true)
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
                var i = 1//rank
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

                musicListFragment = MusicListFragment(musicList,R.layout.music_list_fragment)
                replaceFragment(R.id.mainFrag,musicListFragment,true)
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
                var i = 1//rank
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

                    musicListFragment = MusicListFragment(musicList,R.layout.music_list_fragment)
                    replaceFragment(R.id.mainFrag,musicListFragment,true)
                }
            }
            override fun onFailure(call: Call<DailyRecomSongsResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

}
package com.example.music.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.music.MainActivity
import com.example.music.R
import com.example.music.adapter.PlaylistAdapter
import com.example.music.json.PlaylistDetailResponse
import com.example.music.json.PlaylistResponse
import com.example.music.pojo.Music
import com.example.music.pojo.Playlist
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class HomePageFragment(resId:Int, val profile: Bundle?):BaseFragment(resId) {
    private lateinit var mainActivity:MainActivity
    var playlists:ArrayList<Playlist>? = null
    var lovePlayListId : Long? = null
    var loveSongs : ArrayList<Music>? = null//服务器提交修改有一定的时延，本地保存下

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(activity!=null){
            mainActivity = activity as MainActivity
        }
        val view  = super.onCreateView(inflater, container, savedInstanceState)!!


        view.run{
            /*配置用户的基本信息*/
            val avatarBitmap = (activity as MainActivity).bitmap
            findViewById<CircleImageView>(R.id.avatar).setImageBitmap(avatarBitmap)
            findViewById<TextView>(R.id.nickname_main).text = profile?.getString("nickname")
            findViewById<TextView>(R.id.signature).text = profile?.getString("signature")

            /*其他*/
            findViewById<ImageView>(R.id.addPlaylistBtn).setOnClickListener {
                Toast.makeText(context,"创建歌单的功能正在建设，敬请期待！",Toast.LENGTH_SHORT).show()
            }
        }

        /*加载用户的歌单*/
        if(playlists==null){
            loadPlaylist(view)
        }else{
            val sheetListView = view.findViewById<RecyclerView>(R.id.sheetListView)
            val adapter = PlaylistAdapter(playlists!!,mainActivity)
            sheetListView.adapter  = adapter
            sheetListView.layoutManager = LinearLayoutManager(mainActivity)
            adapter.notifyDataSetChanged()
        }
        return view
    }


    /**
     * 异步加载用户的歌单
     */
    private fun loadPlaylist(view:View){
        Log.e("Main","你好")
        mainActivity.musicHttpService.getPlaylist(mainActivity.uid).enqueue(object : Callback<PlaylistResponse>{
            override fun onResponse(
                call: Call<PlaylistResponse>,
                response: Response<PlaylistResponse>
            ) {
                val playlistArray = ArrayList<Playlist>()
                val result = response.body()!!
                if(result.code==200){
                    var playlist = result.playlist!!
                    for(obj in playlist){
                        playlistArray.add(Playlist(obj.id,obj.name,obj.coverImgUrl,obj.trackCount,obj.playCount))
                    }

                    val sheetListView = view.findViewById<RecyclerView>(R.id.sheetListView)
                    val adapter = PlaylistAdapter(playlistArray,mainActivity)
                    sheetListView.adapter  = adapter
                    sheetListView.layoutManager = LinearLayoutManager(mainActivity)
                    adapter.notifyDataSetChanged()

                    /*保存下值，下次就不用请求了*/
                    if(playlistArray.size>0){
                        lovePlayListId  = playlistArray[0].id
                        playlistArray.removeAt(0)
                        playlists = playlistArray
                        getLoveSongs()
                    }

                }else{
                    Toast.makeText(mainActivity,"请求失败",Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<PlaylistResponse>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    private fun getLoveSongs(){

        mainActivity.musicHttpService.getPlaylistDetail(lovePlayListId.toString()).enqueue(object : Callback<PlaylistDetailResponse>{
            override fun onResponse(
                call: Call<PlaylistDetailResponse>,
                response: Response<PlaylistDetailResponse>
            ) {
                val playlistDetailResponse = response.body()!!
                val musicSet = ArrayList<Music>()
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
                    musicSet.add(Music(id,name,authors,obj.al.picUrl))
                }

                loveSongs = musicSet//第一次点击是向网络获取，以后直接在loveSongs中进行增删改差
            }

            override fun onFailure(call: Call<PlaylistDetailResponse>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

}
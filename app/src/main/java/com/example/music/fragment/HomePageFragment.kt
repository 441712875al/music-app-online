package com.example.music.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.music.MainActivity
import com.example.music.R
import com.example.music.adapter.PlaylistAdapter
import com.example.music.json.PlaylistResponse
import com.example.music.pojo.Playlist
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePageFragment(resId:Int, val profile: Bundle?):BaseFragment(resId) {
    private lateinit var mainActivity:MainActivity
    var playlists:ArrayList<Playlist>? = null
    var lovePlayList : Playlist? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(activity!=null){
            mainActivity = activity as MainActivity
        }
        val view  = super.onCreateView(inflater, container, savedInstanceState)!!

        /*配置用户的基本信息*/
        view?.run{
            val avatarBitmap = (activity as MainActivity).bitmap
            findViewById<CircleImageView>(R.id.avatar).setImageBitmap(avatarBitmap)
            findViewById<TextView>(R.id.nickname_main).text = profile?.getString("nickname")
            findViewById<TextView>(R.id.signature).text = profile?.getString("signature")
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
                        lovePlayList  = playlistArray[0]
                        playlistArray.removeAt(0)
                        playlists = playlistArray
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
}
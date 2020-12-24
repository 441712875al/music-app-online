package com.example.music.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.music.MainActivity
import com.example.music.R
import com.example.music.adapter.ImageAdapter
import com.example.music.adapter.PlaylistAdapter
import com.example.music.adapter.RecomPlaylistAdapter
import com.example.music.json.RecomPlaylistResponse
import com.example.music.pojo.Playlist
import com.youth.banner.Banner
import com.youth.banner.indicator.CircleIndicator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MusicHomeFragment:Fragment() {
    private lateinit var  mainActivity:MainActivity
    var recomPlaylists:List<Playlist>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(activity!=null){
            mainActivity = activity as MainActivity
        }
        val view = inflater.inflate(R.layout.music_recom_fragment,container,false)
        val banner = view.findViewById<Banner<Int, ImageAdapter>>(R.id.myBanner)
        val adatper = ImageAdapter(initImages())
        banner.setAdapter(adatper)
            .addBannerLifecycleObserver(this)
            .setIndicator(CircleIndicator(context))

        if(recomPlaylists==null){
            loadPlaylist(view)
        }else{
            val recomSheetListView = view.findViewById<RecyclerView>(R.id.recom_playlist_view)
            val adapter = RecomPlaylistAdapter(recomPlaylists!!,mainActivity)
            recomSheetListView.adapter  = adapter
            recomSheetListView.layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
            adapter.notifyDataSetChanged()
        }
        return view
    }


    /**
     * 初始化banner需要的图片
     */
    private fun initImages():List<Int>{
        val imageList = ArrayList<Int>()
        val imageRes = resources.obtainTypedArray(R.array.bannerImages)
        for (i in 0 until imageRes.length()){
            imageList.add(imageRes.getResourceId(i,0))
        }
        return imageList
    }


    /**
     * 异步加载推荐的歌单
     */
    private fun loadPlaylist(view:View){
        Log.e("Main","你好")
        mainActivity.musicHttpService.getRecomPlaylist(30).enqueue(object :
            Callback<RecomPlaylistResponse> {
            override fun onResponse(
                call: Call<RecomPlaylistResponse>,
                response: Response<RecomPlaylistResponse>
            ) {
                val playlistArray = ArrayList<Playlist>()
                val result = response.body()!!
                if(result.code==200){
                    var playlist = result.result!!
                    for(obj in playlist){
                        playlistArray.add(Playlist(obj.id,obj.name,obj.picUrl,obj.trackCount,obj.playCount))
                    }

                    val recomSheetListView = view.findViewById<RecyclerView>(R.id.recom_playlist_view)
                    val adapter = RecomPlaylistAdapter(playlistArray,mainActivity)
                    recomSheetListView.adapter  = adapter
                    recomSheetListView.layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
                    adapter.notifyDataSetChanged()

                    /*保存下值，下次就不用请求了*/
                    recomPlaylists = playlistArray
                }else{
                    Toast.makeText(mainActivity,"请求失败", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<RecomPlaylistResponse>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

}
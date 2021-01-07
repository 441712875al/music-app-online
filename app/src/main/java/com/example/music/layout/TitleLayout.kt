package com.example.music.layout

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.music.R
import com.example.music.activity.MainActivity
import com.example.music.fragment.MusicListFragment
import com.example.music.network.json.SearchResponse
import com.example.music.entity.Music
import com.example.music.utils.ActivityUtil
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TitleLayout(context:Context,attrs:AttributeSet):ConstraintLayout(context,attrs) {
    init{
        val view = LayoutInflater.from(context).inflate(R.layout.title,this)
        view.findViewById<CircleImageView>(R.id.menu).setOnClickListener {
            /*弹出侧边栏*/
            val mainActivity = context as MainActivity
            mainActivity.findViewById<DrawerLayout>(R.id.drawerLayout)?.openDrawer(GravityCompat.START)
        }

        val searchView = view.findViewById<EditText>(R.id.searchView)
        searchView.setOnKeyListener(object : View.OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    val keywords = searchView.text.toString()
                    val mainActivity = context as MainActivity
                    if(keywords.isNotEmpty()){
                        mainActivity.musicHttpService.search(keywords,1).enqueue(object : Callback<SearchResponse>{
                            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                                t.printStackTrace()
                                Toast.makeText(mainActivity,"搜索失败",Toast.LENGTH_SHORT).show()
                            }

                            override fun onResponse(
                                call: Call<SearchResponse>,
                                response: Response<SearchResponse>
                            ) {
                                val result = response.body()!!
                                if(result.code==200){
                                    val musicList = ArrayList<Music>()

                                    for(song in result.result.songs){
                                        var authors = ""
                                        for(i in 0 until song.artists.size){
                                            authors+=song.artists[i].name
                                            if(i!=song.artists.size-1){
                                                authors+='/'
                                            }
                                        }
                                        musicList.add(Music(song.id,song.name,authors,null))
                                    }
                                    mainActivity.musicListFragment = MusicListFragment(musicList,
                                        R.layout.music_list_fragment
                                    )
                                    ActivityUtil.replaceFragment(mainActivity,
                                        R.id.mainFrag,mainActivity.musicListFragment,true)
                                }else{
                                    Toast.makeText(mainActivity,"搜索失败",Toast.LENGTH_SHORT).show()
                                }

                            }
                        })
                    }
                }
                return false
            }
        })
    }
}
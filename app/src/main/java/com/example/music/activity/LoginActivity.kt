package com.example.music.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.music.AppDatabase
//import com.example.music.AppDatabase
import com.example.music.R
import com.example.music.entity.Music
import com.example.music.entity.Playlist
import com.example.music.fragment.BaseFragment
import com.example.music.fragment.LoginFragment
import com.example.music.network.json.LoginResponse
import com.example.music.entity.User
import com.example.music.network.json.PlaylistDetailResponse
import com.example.music.network.json.PlaylistResponse
import com.example.music.network.service.LoginHttpService
import com.example.music.network.service.MusicHttpService
import com.example.music.services.ServiceCreator
import com.example.music.utils.ActivityUtil
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread


/**
 * @author along
 * 用户登录的活动界面
 */
class LoginActivity : AppCompatActivity() {

    private val activity = this

    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("userConfig",Context.MODE_PRIVATE)
        val isRememberInConfig = prefs.getBoolean("isRemember",false)
        setContentView(R.layout.activity_cover)
        if(isRememberInConfig) {
            val phone = prefs.getString("phone", "")
            val pwd = prefs.getString("pwd", "")
            verify(phone!!,pwd!!,isRememberInConfig)
        }else{
            /*没有记住密码就直接跳到登录页*/
            setContentView(R.layout.activity_login)
            ActivityUtil.replaceFragment(this,R.id.formFrag,LoginFragment(),false)
        }
    }



    /**
     * 在登录和注册页面进行切换
     * */
    fun onClick(view: View){
        when(view.id){
            R.id.loginItem -> {
                loginItem.setBackgroundResource(R.drawable.shape_selected)
                signItem.setBackgroundResource(R.drawable.unselected_shape)
                ActivityUtil.replaceFragment(this,R.id.formFrag,
                    BaseFragment(R.layout.login_fragment),false)
            }
            R.id.signItem ->  {
                signItem.setBackgroundResource(R.drawable.shape_selected)
                loginItem.setBackgroundResource(R.drawable.unselected_shape)
                ActivityUtil.replaceFragment(this,R.id.formFrag,
                    BaseFragment(R.layout.sign_fragment),false)
            }
        }
    }



    fun verify(phone:String,pwd:String,isRemember: Boolean){
        val retrofit = ServiceCreator.create(LoginHttpService::class.java,activity)
        retrofit.login(phone,pwd).enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val loginResponse = response.body()!!
                val editor = getSharedPreferences("userConfig", Context.MODE_PRIVATE).edit()
                when (loginResponse?.code) {
                    /*登录成功*/
                    200 -> {
                        /*记住密码*/
                        if (isRemember) {
                            editor.putBoolean("isRemember", true)
                            editor.putString("phone", phone)
                            editor.putString("pwd", pwd)
                            editor.putString("userId",loginResponse.profile.userId)
                        } else {
                            editor.putBoolean("isRemember", false)
                        }
                        editor.putString("cookie", loginResponse.cookie)

                        val user = User(loginResponse.profile.userId,loginResponse.profile.gender,
                            loginResponse.profile.birthday,loginResponse.profile.avatarUrl,
                            loginResponse.account.createTime,loginResponse.profile.nickname,
                            loginResponse.profile.signature)

                        loadUserData(user)
                        /*传递参数给主活动*/
                        val intent = Intent(activity, MainActivity::class.java)
                        intent.putExtra("theme", R.style.LightTheme)

                        /*loginActivity的任务已经完成，光荣退出*/
                        startActivity(intent)
                        activity.finish()
                    }
                    400 -> {
                        editor.clear()
                        Toast.makeText(activity, "登录失败", Toast.LENGTH_SHORT).show()
                        setContentView(R.layout.activity_login)
                        ActivityUtil.replaceFragment(activity,R.id.formFrag,LoginFragment(),false)
                    }
                    else -> {
                        editor.clear()
                        Toast.makeText(activity, loginResponse?.msg ?: "", Toast.LENGTH_SHORT)
                            .show()
                        setContentView(R.layout.activity_login)
                        ActivityUtil.replaceFragment(activity,R.id.formFrag,LoginFragment(),false)
                    }
                }
                editor.apply()
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    private fun loadUserData(user:User){
        val userDao = AppDatabase.getDatabase(activity).userDao()
        val musicDao = AppDatabase.getDatabase(activity).musicDao()
        val playlistDao = AppDatabase.getDatabase(activity).playlistDao()

        val thread = Thread {
            val userOld = userDao.getUser()
            val playlists = playlistDao.loadAll()

            /*加载用户信息*/
            if(userOld.isEmpty()){
                thread {
                    userDao.insertUser(user)
                }
            }else if(userOld[0] != user){
                thread {
                    userDao.deleteUser()
                    userDao.insertUser(user)
                }
            }

            /**加载歌单、歌曲信息*/
            if(playlists.isEmpty()){
                val retrofit = ServiceCreator.create(MusicHttpService::class.java,activity)

                /*加载歌单信息*/
                retrofit.getPlaylist(user.userId).enqueue(object : Callback<PlaylistResponse>{
                    override fun onResponse(call: Call<PlaylistResponse>, response: Response<PlaylistResponse>) {
                        val result = response.body()!!
                        Log.e(TAG,result.playlist.size.toString())
                        for (i in 0 until result.playlist.size) {
                            val playlistObj = result.playlist[i]
                            var playlist = if (i != 0) {
                                Playlist(
                                    playlistObj.id,
                                    playlistObj.name,
                                    playlistObj.coverImgUrl,
                                    playlistObj.trackCount,
                                    playlistObj.playCount,
                                    false
                                )
                            } else {
                                Playlist(
                                    playlistObj.id,
                                    playlistObj.name,
                                    playlistObj.coverImgUrl,
                                    playlistObj.trackCount,
                                    playlistObj.playCount,
                                    true
                                )
                            }

                            thread {
                                playlistDao.insertPlaylist(playlist)
                            }
                            /*获取歌单中的歌曲*/
                            retrofit.getPlaylistDetail(playlist.playListId.toString())
                                .enqueue(object : Callback<PlaylistDetailResponse> {
                                    override fun onResponse(
                                        call: Call<PlaylistDetailResponse>,
                                        response: Response<PlaylistDetailResponse>
                                    ) {
                                        val result = response.body()!!
                                        result.playlist.tracks.reverse()
                                        result.playlist.tracks.forEach { it ->
                                            var authors = ""
                                            it.ar.forEach {
                                                authors += it.name
                                            }
                                            val music = Music(
                                                it.id,
                                                it.name,
                                                authors,
                                                it.al.picUrl,
                                                playlist.playListId
                                            )
                                            thread { musicDao.insertMusic(music) }
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<PlaylistDetailResponse>,
                                        t: Throwable
                                    ) {
                                        t.printStackTrace()
                                    }
                                })
                        }
                    }

                    override fun onFailure(call: Call<PlaylistResponse>, t: Throwable) {
                        TODO("Not yet implemented")
                    }
                })
            }
        }
        thread.start()
        thread.join()
    }

}
package com.example.music.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.music.AppDatabase
import com.example.music.activity.MainActivity
import com.example.music.R
import com.example.music.adapter.PlaylistAdapter
import com.example.music.viewmodel.HomePageViewModel
import kotlinx.android.synthetic.main.home_framgment.view.*
import kotlin.concurrent.thread

class HomePageFragment(resId:Int):BaseFragment(resId) {
    private val TAG = "HomePage"
    private lateinit var mainActivity: MainActivity
    lateinit var viewModel:HomePageViewModel
    lateinit var thisView:View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG,"OnCreateView")

        if(activity!=null){
            mainActivity = activity as MainActivity
        }

        thisView =  inflater.inflate(resId, container, false)

        viewModel = ViewModelProvider(this)[HomePageViewModel::class.java]
        loadPlaylist()
        loadUserInfo()
        viewModel.playlists.observe(viewLifecycleOwner, { list ->
            /*加载用户的歌单*/
            val sheetListView = thisView.findViewById<RecyclerView>(R.id.sheetListView)
            val adapter = PlaylistAdapter(list,mainActivity)
            sheetListView.adapter  = adapter
            sheetListView.layoutManager = LinearLayoutManager(mainActivity)
            adapter.notifyDataSetChanged()
        })

        viewModel.avatarBitmap.observe(viewLifecycleOwner,{
            thisView.avatar.setImageBitmap(it)
        })

        viewModel.nickname.observe(viewLifecycleOwner,{
            thisView.nickname_main.text = it
        })


        viewModel.signature.observe(viewLifecycleOwner,{
            thisView.signature.text = it
        })
        return thisView
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e(TAG,"OnAttach")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e(TAG,"OnActivityCreated")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.e(TAG,"OnDestroyView")
    }

    override fun onDetach() {
        super.onDetach()
        Log.e(TAG,"OnDetach")
    }
    /**
     * 数据库获取用户的歌单
     */
    private fun loadPlaylist(){
        val playlistDao = AppDatabase.getDatabase(context!!).playlistDao()
        thread{
            viewModel.playlists.postValue(playlistDao.loadAll())
        }
    }


    /**
     * 加载用户的信息数据
     */
    private fun loadUserInfo(){
        viewModel.avatarBitmap.value = mainActivity.userBitmap
        thread {
            val user = AppDatabase.getDatabase(context!!).userDao().getUser()?.get(0)
            viewModel.nickname.postValue(user.nickname)
            viewModel.signature.postValue(user.signature)
        }
    }
}
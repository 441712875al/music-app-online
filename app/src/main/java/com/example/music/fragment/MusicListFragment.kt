package com.example.music.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.music.R
import com.example.music.adapter.MusicRecycleViewAdapter
import com.example.music.entity.Music
import com.example.music.viewmodel.MusicListViewModel


class MusicListFragment(private var musicList: List<Music>, resId:Int):BaseFragment(resId) {
    lateinit var viewModel: MusicListViewModel
    lateinit var thisView:View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisView = super.onCreateView(inflater, container, savedInstanceState)!!
        viewModel = ViewModelProvider(this)[MusicListViewModel::class.java]
        viewModel.musicList.observe(viewLifecycleOwner,{
            configRecycleListView(it)
        })
        viewModel.musicList.value = musicList
        return thisView
    }



    /*配置显示音乐列表的ListView*/
    private fun configRecycleListView(musicList:List<Music>){
        val musicListView = thisView.findViewById<RecyclerView>(R.id.musicListView)
        musicListView.layoutManager = LinearLayoutManager(context)
        val adapter = MusicRecycleViewAdapter(musicList)
        musicListView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

}
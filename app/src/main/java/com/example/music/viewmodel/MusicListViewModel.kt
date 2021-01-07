package com.example.music.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.music.entity.Music

class MusicListViewModel:ViewModel() {
    var musicList = MutableLiveData<List<Music>>()
}
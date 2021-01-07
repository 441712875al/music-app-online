package com.example.music.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.music.entity.Playlist

class HomePageViewModel:ViewModel() {
    var playlists = MutableLiveData<List<Playlist>>()

    var avatarBitmap = MutableLiveData<Bitmap>()

    var nickname = MutableLiveData<String>()

    var signature = MutableLiveData<String>()
}
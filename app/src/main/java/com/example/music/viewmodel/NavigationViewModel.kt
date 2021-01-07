package com.example.music.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NavigationViewModel:ViewModel() {
    var nickname = MutableLiveData<String>()
    var signature =  MutableLiveData<String>()
    var avatarBitmp = MutableLiveData<Bitmap>()
}
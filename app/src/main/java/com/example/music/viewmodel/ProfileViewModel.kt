package com.example.music.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.music.entity.User

class ProfileViewModel:ViewModel() {
    var user = MutableLiveData<User>()
}
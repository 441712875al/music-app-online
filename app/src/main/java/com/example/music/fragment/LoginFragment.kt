package com.example.music.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.music.activity.LoginActivity
import com.example.music.R
import kotlinx.android.synthetic.main.login_fragment.view.*
import java.util.zip.Inflater

class LoginFragment:Fragment() {
    private lateinit var thisView:View
    private lateinit var loginActivity: LoginActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(activity!=null){
            loginActivity = activity as LoginActivity
        }
        thisView = inflater.inflate(R.layout.login_fragment,container,false)
        thisView.commit.setOnClickListener {
            val phone = thisView.phone.text.toString()
            val pwd = thisView.pwd.text.toString()
            val isRemember = thisView.remember_me.isChecked
            loginActivity.verify(phone,pwd,isRemember)
        }
        return thisView
    }

}
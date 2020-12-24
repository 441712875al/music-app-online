package com.example.music.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.music.LoginActivity
import com.example.music.MainActivity
import com.example.music.R
import com.example.music.json.LoginResponse
import com.example.music.services.LoginService
import com.example.music.utils.ServiceCreator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        thisView =  inflater.inflate(R.layout.login_fragment,container,false)
        thisView.findViewById<Button>(R.id.commit).setOnClickListener {
            val phone = thisView.findViewById<EditText>(R.id.phone).text.toString()
            val pwd = thisView.findViewById<EditText>(R.id.pwd).text.toString()
            val isRemember = thisView.findViewById<CheckBox>(R.id.remember_me).isChecked
            loginActivity.verify(phone,pwd,isRemember)
        }
        return thisView
    }
}
package com.example.music.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.music.MainActivity
import com.example.music.R
import de.hdodenhof.circleimageview.CircleImageView


class ProfileFragment(val profile:Bundle?):Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.profile_fragment,container,false)
        view?.run{
            val avatarBitmap = (activity as MainActivity).bitmap
            findViewById<TextView>(R.id.nicknameInProfile).text = profile?.getString("nickname")
            findViewById<TextView>(R.id.signature).text = profile?.getString("signature")
            findViewById<CircleImageView>(R.id.avatarInProfile).setImageBitmap(avatarBitmap)
        }
        return view
    }
}
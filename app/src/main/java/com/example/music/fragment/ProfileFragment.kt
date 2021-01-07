package com.example.music.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.music.activity.MainActivity
import com.example.music.activity.ProfileFormActivity
import com.example.music.R
import com.example.music.entity.User
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.time.LocalDate


class ProfileFragment(val user: User):Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.profile_fragment,container,false)
        view?.run{
            val avatarBitmap = (activity as MainActivity).userBitmap
            findViewById<TextView>(R.id.nicknameInProfile).text = user.nickname
            findViewById<TextView>(R.id.signature).text = user.signature
            findViewById<CircleImageView>(R.id.avatarInProfile).setImageBitmap(avatarBitmap)
            val format = SimpleDateFormat("yyyy-MM-dd")
            val birthday = if(user.birthday!=null){
                LocalDate.parse(format.format(user.birthday)).toString()
            }else{
                ""
            }
            val diff = (System.currentTimeMillis()-user.createTime)/365/24/3600/1000
            val gender = if(user.gender==0){
                "女"
            }else{
                "男"
            }
            findViewById<TextView>(R.id.info).text = "昵称： ${user.nickname}\n\n村龄： ${diff.toInt()} 年\n\n生日： " +
                    "${birthday}\n\n性别： ${gender}\n\n签名： ${user.signature}"
            findViewById<Button>(R.id.changeInfo).setOnClickListener {
                activity?.finish()
                val intent = Intent(view.context, ProfileFormActivity::class.java)
                intent.putExtra("theme",(activity as MainActivity).themeId)
                startActivity(intent)
            }
        }
        return view
    }
}
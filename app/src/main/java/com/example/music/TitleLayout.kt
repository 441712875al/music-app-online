package com.example.music

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.jar.Attributes

class TitleLayout(context:Context,attrs:AttributeSet):ConstraintLayout(context,attrs) {
    init{
        val view = LayoutInflater.from(context).inflate(R.layout.title,this)
        view.findViewById<CircleImageView>(R.id.menu).setOnClickListener {
            val mainActivity = context as MainActivity
            mainActivity.findViewById<DrawerLayout>(R.id.drawerLayout)?.openDrawer(GravityCompat.START)
        }
    }
}
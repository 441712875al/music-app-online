package com.example.music

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.music.fragment.BaseFragment
import com.example.music.fragment.MusicHomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        configBottomNavigation()
        /*加载主页碎片*/
        replaceFragment(R.id.mainFrag, BaseFragment(R.layout.home_framgment),false)
    }

    /**
     * 替换主布局文件中的FrameLayout，实现页面的动态加载功能
     *
     * @param fragmentId 需要被替换FrameLayout的id
     * @param fragment 已经加载了需要被显示的布局文件的Fragment类
     * @param push 是否将操作加入返回栈
     */
    fun replaceFragment(fragmentId:Int, fragment: Fragment, push:Boolean){
        val transition = supportFragmentManager.beginTransaction()
        transition.replace(fragmentId,fragment)

        /*是否将这次的操作加入栈中*/
        if(push)
            transition.addToBackStack(null)
        transition.commit()
    }


    /**
     * 配置底部导航栏
     */
    private fun configBottomNavigation(){
        /**创建选择之后的监听器*/
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.homePageItem -> replaceFragment(R.id.mainFrag,BaseFragment(R.layout.home_framgment),false)
                R.id.musicHomeItem -> replaceFragment(R.id.mainFrag,MusicHomeFragment(R.layout.music_recom_fragment),false)
                R.id.userItem -> replaceFragment(R.id.mainFrag,BaseFragment(R.layout.profile_fragment),false)
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
        }
        return true
    }
}
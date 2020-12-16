package com.example.music

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.music.fragment.BaseFragment
import kotlinx.android.synthetic.main.activity_login.*


/**
 * @author along
 * 用户登录的活动界面
 */
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        replaceFragment(R.id.formFrag,BaseFragment(R.layout.login_fragment),false)
    }




    /**
     * 响应所有的点击事件
     * */
    fun onClick(view: View){
        when(view.id){
            R.id.loginItem -> {
                loginItem.setBackgroundResource(R.drawable.selected_shape)
                signItem.setBackgroundResource(R.drawable.unselected_shape)
                replaceFragment(R.id.formFrag,BaseFragment(R.layout.login_fragment),false)
            }
            R.id.signItem ->  {
                signItem.setBackgroundResource(R.drawable.selected_shape)
                loginItem.setBackgroundResource(R.drawable.unselected_shape)
                replaceFragment(R.id.formFrag,BaseFragment(R.layout.sign_fragment),false)
            }
        }
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

}
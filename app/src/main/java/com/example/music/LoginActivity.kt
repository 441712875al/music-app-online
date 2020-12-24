package com.example.music

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.music.fragment.BaseFragment
import com.example.music.fragment.LoginFragment
import com.example.music.json.LoginResponse
import com.example.music.services.LoginService
import com.example.music.utils.ServiceCreator
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * @author along
 * 用户登录的活动界面
 */
class LoginActivity : AppCompatActivity() {

    private val activity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("userConfig",Context.MODE_PRIVATE)
        val isRememberInConfig = prefs.getBoolean("isRemember",false)

        setContentView(R.layout.activity_cover)
        if(isRememberInConfig) {
            val phone = prefs.getString("phone", "")
            val pwd = prefs.getString("pwd", "")
            verify(phone!!, pwd!!, isRememberInConfig)
        }else{
            /*没有记住密码就直接跳到登录页*/
            setContentView(R.layout.activity_login)
            replaceFragment(R.id.formFrag,LoginFragment(),false)
        }
    }
    /**
     * 响应所有的点击事件
     * */
    fun onClick(view: View){
        when(view.id){
            R.id.loginItem -> {
                loginItem.setBackgroundResource(R.drawable.shape_selected)
                signItem.setBackgroundResource(R.drawable.unselected_shape)
                replaceFragment(R.id.formFrag,BaseFragment(R.layout.login_fragment),false)
            }
            R.id.signItem ->  {
                signItem.setBackgroundResource(R.drawable.shape_selected)
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


    fun verify(phone: String, pwd: String, isRemember: Boolean) {
        /*异步请求登录验证*/
        if (phone != "" && pwd != "") {
            val retrofit = ServiceCreator.create(LoginService::class.java, activity)
            retrofit.login(phone, pwd).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    val loginResponse = response.body()
                    val editor = getSharedPreferences("userConfig", Context.MODE_PRIVATE).edit()
                    when (loginResponse?.code) {
                        /*登录成功*/
                        200 -> {
                            /*记住密码*/
                            if (isRemember) {
                                editor.putBoolean("isRemember", true)
                                editor.putString("phone", phone)
                                editor.putString("pwd", pwd)
                                editor.putString("userId",loginResponse.profile.userId)
                            } else {
                                editor.putBoolean("isRemember", false)
                            }
                            editor.putString("cookie", loginResponse.cookie)
                            val intent = Intent(activity, MainActivity::class.java)
                            val bundle = Bundle()
                            /*传递参数给主活动*/
                            bundle.putString("avatarUrl", loginResponse.profile.avatarUrl)
                            bundle.putString("nickname", loginResponse.profile.nickname)
                            bundle.putString("signature", loginResponse.profile.signature)
                            bundle.putString("userId",loginResponse.profile.userId)
                            intent.putExtra("profile", bundle)

                            /*loginActivity的任务已经完成，光荣退出*/
                            startActivity(intent)
                            activity.finish()
                        }
                        400 -> {
                            editor.clear()
                            Toast.makeText(activity, "登录失败", Toast.LENGTH_SHORT).show()
                            setContentView(R.layout.activity_login)
                            replaceFragment(R.id.formFrag,LoginFragment(),false)
                        }
                        else -> {
                            editor.clear()
                            Toast.makeText(activity, loginResponse?.msg ?: "", Toast.LENGTH_SHORT)
                                .show()
                            setContentView(R.layout.activity_login)
                            replaceFragment(R.id.formFrag,LoginFragment(),false)
                        }
                    }
                    editor.apply()
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }
}
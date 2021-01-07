package com.example.music.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.music.AppDatabase
import com.example.music.R
import com.example.music.dao.UserDao
import com.example.music.network.json.BaseResponse
import com.example.music.network.service.MusicHttpService
import com.example.music.services.ServiceCreator
import com.example.music.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile_form.*
import kotlinx.android.synthetic.main.activity_profile_form.signature
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.regex.Pattern
import kotlin.concurrent.thread

class ProfileFormActivity : AppCompatActivity() {
    private lateinit var  activity: ProfileFormActivity
    private lateinit var viewModel:ProfileViewModel
    lateinit var userDao : UserDao
    var themeId:Int? = null


    private fun init(){
        activity = this
        userDao = AppDatabase.getDatabase(activity).userDao()
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        themeId = intent.getIntExtra("theme", R.style.LightTheme)
    }


    private fun configCommitBtn(){
        updateCommitBtn.setOnClickListener {
            val gender = gender.text.toString()
            val signature = signature.text.toString()
            val birthdayStr = birthday.text.toString()
            val nickname = nickname.text.toString()


            val p = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1][0-9])|([2][0-4]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$")
            //做一些简单的判断
            if(!(gender.equals("男")||gender.equals("女"))){

                Toast.makeText(this,"性别输入格式错误",Toast.LENGTH_SHORT).show()

            }else if(!p.matcher(birthdayStr).matches()){

                Toast.makeText(this,"日期输入格式错误",Toast.LENGTH_SHORT).show()

            }else  if(gender.isNotEmpty()&&signature.isNotEmpty()&&nickname.isNotEmpty()&&birthdayStr.isNotEmpty()){

                val retrofit = ServiceCreator.create(MusicHttpService::class.java,activity)

                val genderCode = when(gender){
                    "男" ->1
                    "女" ->0
                    else ->0
                }
                /*生日转换成时间戳*/
                val birthdayT = LocalDate.parse(birthdayStr).atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli()


                retrofit.updateProfile(genderCode,signature,nickname,birthdayT).enqueue(object: Callback<BaseResponse>{
                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                        t.printStackTrace()
                    }

                    override fun onResponse(
                        call: Call<BaseResponse>,
                        response: Response<BaseResponse>
                    ) {
                        val body = response.body()!!
                        if(body.code==200){
                            Toast.makeText(activity,"更新成功",Toast.LENGTH_SHORT).show()
                            finish()
                            val intent = Intent(activity, MainActivity::class.java)
                            intent.putExtra("theme",themeId)
                            //保存修改的用户信息，返回主活动
                            val user = viewModel.user.value
                            user?.let {
                                it.gender = genderCode
                                it.birthday = birthdayT
                                it.signature = signature
                                it.nickname = nickname
                            }

                            thread {
                                userDao.updateUser(user!!)
                                viewModel.user.postValue(user)
                                startActivity(intent)
                            }

                        }else{
                            Toast.makeText(activity,"${body.msg}",Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }else{
                Toast.makeText(activity,"请填写完成个人信息",Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_form)
        supportActionBar?.hide()

        thread {
            viewModel.user.postValue(userDao.getUser()[0])
        }


        init()
        viewModel.user.observe(this,{user->
            val genderStr = when(user.gender){
                1-> "男"
                else ->"女"
            }
            val birthdayStr= LocalDate.parse(SimpleDateFormat("yyyy-MM-dd").format(user.birthday).toString())
            gender.text.insert(0, genderStr)
            signature.text.insert(0,"${user.signature}")
            nickname.text.insert(0,"${user.nickname}")
            birthday.text.insert(0,"$birthdayStr")
        })

        configCommitBtn()
    }


    override fun onBackPressed() {
        finish()
        val intent = Intent(activity, MainActivity::class.java)
        intent.putExtra("theme",themeId)
        startActivity(intent)
    }
}
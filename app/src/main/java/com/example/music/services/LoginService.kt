package com.example.music.services

import com.example.music.json.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LoginService {
    /**
     * 手机登录验证
     * 501 账号不存在
     * 502 密码错误
     * 200 登录成功
     */
    @GET("login/cellphone/")
    fun login(@Query("phone")phone:String,@Query("password")pwd:String): Call<LoginResponse>

}

package com.example.music.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.music.R
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object HttpDownloadUtil {
    fun download(urlStr:String?,fileOutputStream: FileOutputStream){
        if(urlStr!=null){
            val downloadThread = object : Thread(){
                override fun run() {
                    var connection:HttpURLConnection? = null
                    try{
                        val url = URL(urlStr)
                        connection = url.openConnection() as HttpURLConnection
                        connection.connectTimeout = 8000
                        connection.readTimeout = 8000
                        val input = connection.inputStream
                        val bytes = ByteArray(1024)
                        var len = input.read(bytes)
                        while(len!=-1){
                            fileOutputStream.write(bytes,0,len)
                            len = input.read(bytes)
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }finally {
                        fileOutputStream.close()
                        connection?.disconnect()
                    }
                }
            }
            downloadThread.start()
            downloadThread.join()
        }
    }

    fun getFilenameFromURL(urlStr:String?) = if (urlStr == null) {
        ""
    }else{
        urlStr?.split("/").last()
    }
}
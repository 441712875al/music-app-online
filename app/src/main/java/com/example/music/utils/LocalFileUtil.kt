package com.example.music.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import com.example.music.R
import java.io.FileInputStream

object LocalFileUtil {
    /**
     * @param filename 文件名
     * 检查文件名是否存在
     */
    fun fileExists(filename:String,activity: AppCompatActivity):Boolean{
        if(filename.equals("")){
            return false
        }
        val fileNames = activity.fileList()
        fileNames?.forEach {
            if(it.contains(filename))
                return true
        }
        return false
    }

    /**
     * @param imageName 音乐图片的文件名
     * 以BitMap形式加载音乐的图片
     */
    fun loadImage(imageURL:String?,activity: AppCompatActivity): Bitmap? {
        if(imageURL == null){
            return null
        }
        val imageName = HttpDownloadUtil.getFilenameFromURL(imageURL)
        /*本地文件不存在的话就下载*/
        if(!fileExists(imageName,activity)){
            val imageFout = activity.openFileOutput(HttpDownloadUtil.getFilenameFromURL(imageURL), Context.MODE_PRIVATE)
            HttpDownloadUtil.download(imageURL,imageFout)
        }

        return BitmapFactory.decodeStream(
            FileInputStream(
                "/data/data/${activity.packageName}/files/$imageName")
        )
    }
}
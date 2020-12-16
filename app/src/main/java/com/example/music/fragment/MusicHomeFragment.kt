package com.example.music.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.music.R
import com.example.music.adapter.ImageAdapter
import com.youth.banner.Banner
import com.youth.banner.indicator.CircleIndicator

class MusicHomeFragment(resId:Int):BaseFragment(resId) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.music_recom_fragment,container,false)
        val banner = view.findViewById<Banner<Int, ImageAdapter>>(R.id.myBanner)
        val adatper = ImageAdapter(initImages())
        banner.setAdapter(adatper)
            .addBannerLifecycleObserver(this)
            .setIndicator(CircleIndicator(context))
        return view
    }

    private fun initImages():List<Int>{
        val imageList = ArrayList<Int>()
        val imageRes = resources.obtainTypedArray(R.array.bannerImages)

        for (i in 0 until imageRes.length()){
            imageList.add(imageRes.getResourceId(i,0))
        }
        return imageList
    }
}
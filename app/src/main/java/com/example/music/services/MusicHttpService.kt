package com.example.music.services

import com.example.music.json.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicHttpService {

    /**
     * 华语音乐排行榜
     */
    @GET("top/list")
    fun getTopList(@Query("id")id:String): Call<MusicTopListResponse>

    /**
     * 获取歌曲mp3
     */
    @GET("song/url")
    fun getSong(@Query("id")id:String):Call<MusicUrlReponse>

    /**
     * 获取歌词
     */
    @GET("lyric")
    fun getLyric(@Query("id")id:String): Call<LyricResponse>

    /**
     * 获取歌曲信息
     */
    @GET("recommend/songs")
    fun getRecommendSongs():Call<DailyRecomSongsResponse>

    /**
     * 获取创建的歌单
     */
    @GET("user/playlist")
    fun getPlaylist(@Query("uid")uid:String ):Call<PlaylistResponse>

    /**
     * 获取歌单详情
     */
    @GET("playlist/detail")
    fun getPlaylistDetail(@Query("id")id:String):Call<PlaylistDetailResponse>

    @GET("personalized")
    fun getRecomPlaylist(@Query("limit")limit:Int):Call<RecomPlaylistResponse>
}
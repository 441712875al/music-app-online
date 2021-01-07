package com.example.music.network.service

import com.example.music.network.json.*
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
     * 获取推荐歌曲信息
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

    /**
     * 获取推荐歌单
     */
    @GET("personalized")
    fun getRecomPlaylist(@Query("limit")limit:Int):Call<RecomPlaylistResponse>


    /**
     * 将歌曲加入喜欢，或者是取消
     */
    @GET("like")
    fun love(@Query("id")id:String,@Query("like")like:Boolean):Call<LoveResponse>


    /**
     * 搜索功能
     * @param type 1：单曲，0: 专辑, 100: 歌手, 1000:歌单, 1002: 用户, 1004: MV, 1006: 歌词, 1009: 电台, 1014: 视频, 1018:综合
     * @param keywords 关键字
     */
    @GET("search?limit=100")
    fun search(@Query("keywords")keywords:String,@Query("type")type:Int) : Call<SearchResponse>

    @GET("user/update")
    fun updateProfile(@Query("gender")gender:Int,
                      @Query("signature")signature:String,
                      @Query("nickname")nickname:String,
                      @Query("birthday")birthday:Long):Call<BaseResponse>
}
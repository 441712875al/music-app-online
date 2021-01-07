package com.example.music.entity

class LrcInfo {

    /*music title*/
    var title : String? = null

    /*artist name*/
    var artist : String? = null

    /*album name*/
    var album : String? = null

    /*the lrc maker*/
    var bySomeBody : String? = null

    /*the time delay or bring forward*/
    var offset : String? = null

    /*保存歌词信息和时间点一一对应的Map*/
    var infos : Map<Int, String?>? = null

}
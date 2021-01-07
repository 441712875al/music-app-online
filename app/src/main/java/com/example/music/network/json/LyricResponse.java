/**
  * Copyright 2020 json.cn 
  */
package com.example.music.network.json;

/**
 * Auto-generated: 2020-12-19 20:42:1
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */

public class LyricResponse {
    private Lrc lrc;
    public void setLrc(Lrc lrc) {
         this.lrc = lrc;
     }
     public Lrc getLrc() {
         return lrc;
     }

    public class Lrc {

        private int version;
        private String lyric;
        public void setVersion(int version) {
            this.version = version;
        }
        public int getVersion() {
            return version;
        }

        public void setLyric(String lyric) {
            this.lyric = lyric;
        }
        public String getLyric() {
            return lyric;
        }
    }
}
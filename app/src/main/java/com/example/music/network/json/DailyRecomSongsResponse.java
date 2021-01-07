/**
  * Copyright 2020 json.cn 
  */
package com.example.music.network.json;

import java.util.List;

/**
 * Auto-generated: 2020-12-23 10:52:52
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class DailyRecomSongsResponse {

    private int code;
    private Data data;
    public void setCode(int code) {
         this.code = code;
     }
     public int getCode() {
         return code;
     }

    public void setData(Data data) {
         this.data = data;
     }
     public Data getData() {
         return data;
     }

    /**
     * Auto-generated: 2020-12-23 10:52:52
     *
     * @author json.cn (i@json.cn)
     * @website http://www.json.cn/java2pojo/
     */
    public class Data {

        private List<DailySongs> dailySongs;

        public void setDailySongs(List<DailySongs> dailySongs) {
            this.dailySongs = dailySongs;
        }
        public List<DailySongs> getDailySongs() {
            return dailySongs;
        }

        public class DailySongs {

            private String name;
            private long id;
            private List<Ar> ar;
            private Al al;

            public void setName(String name) {
                this.name = name;
            }
            public String getName() {
                return name;
            }

            public void setId(long id) {
                this.id = id;
            }
            public long getId() {
                return id;
            }

            public void setAl(Al al) {
                this.al = al;
            }
            public Al getAl() {
                return al;
            }

            public List<Ar> getAr() {
                return ar;
            }
            public void setAr(List<Ar> ar) {
                this.ar = ar;
            }
        }
    }
}
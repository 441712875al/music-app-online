/**
  * Copyright 2020 json.cn 
  */
package com.example.music.network.json;
import java.util.List;

/**
 * Auto-generated: 2020-12-18 18:31:11
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class MusicUrlReponse {

    private List<Data> data;
    private int code;
    public void setData(List<Data> data) {
         this.data = data;
     }
     public List<Data> getData() {
         return data;
     }

    public void setCode(int code) {
         this.code = code;
     }
     public int getCode() {
         return code;
     }

    /**
     * Auto-generated: 2020-12-18 18:31:11
     *
     * @author json.cn (i@json.cn)
     * @website http://www.json.cn/java2pojo/
     */
     public class Data {

        private Long id;

        private String url;

        public void setUrl(String url) {
             this.url = url;
         }
         public String getUrl() {
             return url;
         }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
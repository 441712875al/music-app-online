/**
  * Copyright 2020 json.cn 
  */
package com.example.music.network.json;

import java.util.List;

/**
 * Auto-generated: 2020-12-18 14:59:26
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class MusicTopListResponse {

    private Playlist playlist;
    private int code;
    public void setPlaylist(Playlist playlist) {
         this.playlist = playlist;
     }
     public Playlist getPlaylist() {
         return playlist;
     }
     public void setCode(int code) {
         this.code = code;
     }
     public int getCode() {
         return code;
     }

    @Override
    public String toString() {
        return "MusicTopListData{" +
                "playlist=" + playlist +
                ", code=" + code +
                '}';
    }

    /**
     * Auto-generated: 2020-12-18 14:59:26
     *
     * @author json.cn (i@json.cn)
     * @website http://www.json.cn/java2pojo/
     */
    public class Playlist {
        private List<Tracks> tracks;

        public List<Tracks> getTracks() {
            return tracks;
        }

        public void setTracks(List<Tracks> tracks) {
            this.tracks = tracks;
        }

        @Override
        public String toString() {
            return "Playlist{" +
                    "tracks=" + tracks +
                    '}';
        }

        /**
         * Auto-generated: 2020-12-18 14:59:26
         *
         * @author json.cn (i@json.cn)
         * @website http://www.json.cn/java2pojo/
         */
        public  class Tracks {

            private String name;
            private long id;
            private List<Ar> ar;
            private Al al;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public List<Ar> getAr() {
                return ar;
            }

            public void setAr(List<Ar> ar) {
                this.ar = ar;
            }

            public Al getAl() {
                return al;
            }

            public void setAl(Al al) {
                this.al = al;
            }

            @Override
            public String toString() {
                return "Tracks{" +
                        "name='" + name + '\'' +
                        ", id=" + id +
                        ", ar=" + ar +
                        ", al=" + al +
                        '}';
            }
        }
    }


}
package com.example.music.network.json;

import java.util.List;

public class RecomPlaylistResponse {
    private int code;
    private List<PlaylistObj> result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<PlaylistObj> getResult() {
        return result;
    }

    public void setResult(List<PlaylistObj> result) {
        this.result = result;
    }

    public class PlaylistObj{
        private Long id;
        private String name;
        private String picUrl;
        private Long trackCount;
        private Long playCount;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public Long getTrackCount() {
            return trackCount;
        }

        public void setTrackCount(Long trackCount) {
            this.trackCount = trackCount;
        }

        public Long getPlayCount() {
            return playCount;
        }

        public void setPlayCount(Long playCount) {
            this.playCount = playCount;
        }
    }
}

package com.example.music.network.json;

import java.util.List;

public class PlaylistResponse {
    private int code;
    private List<PlaylistObj> playlist;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<PlaylistObj> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<PlaylistObj> playlist) {
        this.playlist = playlist;
    }

    public class PlaylistObj{
        private Long id;
        private String name;
        private String coverImgUrl;
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

        public String getCoverImgUrl() {
            return coverImgUrl;
        }

        public void setCoverImgUrl(String coverImgUrl) {
            this.coverImgUrl = coverImgUrl;
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

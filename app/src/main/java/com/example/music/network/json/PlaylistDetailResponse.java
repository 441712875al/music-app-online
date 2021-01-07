package com.example.music.network.json;

public class PlaylistDetailResponse {
    private int code;
    private MusicTopListResponse.Playlist playlist;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public MusicTopListResponse.Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(MusicTopListResponse.Playlist playlist) {
        this.playlist = playlist;
    }
}

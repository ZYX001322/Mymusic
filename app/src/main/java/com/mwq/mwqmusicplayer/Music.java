package com.mwq.mwqmusicplayer;

import android.net.Uri;

/**
 * Created by mwq on 2017/5/22.
 */

public class Music {

    private int musicResid;
    private String songName;
    private String artist;
    private int image;
	public int getMusicResid() {
		return musicResid;
	}
	public void setMusicResid(int musicResid) {
		this.musicResid = musicResid;
	}
	public String getSongName() {
		return songName;
	}
	public void setSongName(String songName) {
		this.songName = songName;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public int getImage() {
		return image;
	}
	public void setImage(int image) {
		this.image = image;
	}
	@Override
	public String toString() {
		return "Music [musicResid=" + musicResid + ", songName=" + songName
				+ ", artist=" + artist + ", image=" + image + "]";
	}
    
	
    

    
}

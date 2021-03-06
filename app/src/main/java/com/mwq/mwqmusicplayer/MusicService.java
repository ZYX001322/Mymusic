package com.mwq.mwqmusicplayer;

import android.R.bool;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
	private MediaPlayer mp;
	private final ServiceBinder myBinder = new ServiceBinder();
	private int musicResid;
    private int musicFlag = 0;
    List<Music> musics;
	private List<Music> createMusic(){
		List<Music> musics = new ArrayList<Music>();
		Music music = new Music();
		music.setMusicResid(R.raw.music1);
		music.setImage(R.drawable.music1);
		music.setSongName("山外小楼夜听雨");
		music.setArtist("任然");
		musics.add(music);

		music = new Music();
		music.setMusicResid(R.raw.music2);
		music.setImage(R.drawable.music2);
		music.setSongName("小半");
		music.setArtist("陈粒");
		musics.add(music);

		music = new Music();
		music.setMusicResid(R.raw.music3);
		music.setImage(R.drawable.music3);
		music.setSongName("雪");
		music.setArtist("杜雯媞");
		musics.add(music);
		return musics;
	}

	// 实例化
	class ServiceBinder extends Binder {
		public MusicService getService() {
			return MusicService.this;
		}

	}
	public int  getTotalTime(){
		return mp.getDuration();
	}

	public int getCurrentPositon(){return mp.getCurrentPosition();}

	public void onDestroy() {
		super.onDestroy();
		mp.stop();
	}

	@Override
	public void onCreate() {
		super.onCreate();
        musics = createMusic();
		/*mp = MediaPlayer.create(this, musics.get(musicFlag).getMusicResid());
		mp.start();
		mp.pause();*/
	}

	public void changeSeekBar(int time){
		mp.pause();
		mp.seekTo(time);
		mp.start();
	}

    public void nextSong(){
        mp.stop();
        mp.reset();
        musicFlag +=1;
        mp = MediaPlayer.create(this,musics.get(musicFlag%3).getMusicResid());
        mp.start();
    }
    public void prevSong(){
        mp.stop();
		mp.reset();
        musicFlag += 2;
        mp = MediaPlayer.create(this,musics.get(musicFlag%3).getMusicResid());
        mp.start();
    }


	public void pauseMusic() {
		mp.pause();
	}

	public void startMusic() {
		mp.start();

	}

	public boolean isPlaying() {
		return mp.isPlaying();
	}

	@Override
	public IBinder onBind(final Intent intent) {
		musicResid = intent.getIntExtra("musicId",R.raw.music2);
		Log.i("接受参数",String.valueOf(musicResid));
		Toast.makeText(MusicService.this,musicResid,Toast.LENGTH_LONG);
		mp = MediaPlayer.create(this, musicResid);
		mp.start();
		mp.pause();

		final Intent intentReturn = new Intent();
		intentReturn.setAction("updateNowTime");
		Timer timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				intentReturn.putExtra("nowTime",getCurrentPositon());
				intentReturn.putExtra("totalTime",getTotalTime());
				if(mp.getDuration()-mp.getCurrentPosition()<1000){
					intentReturn.putExtra("nextSongFlag",1);
				}
				else{
					intentReturn.putExtra("nextSongFlag",0);
				}
				sendBroadcast(intentReturn);
			}
		};
		timer.schedule(timerTask,100,200);


		return myBinder;
	}


	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
}

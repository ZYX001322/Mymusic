package com.mwq.mwqmusicplayer;

import android.R.bool;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;

public class MusicService extends Service {
	private MediaPlayer mp;
	private final ServiceBinder myBinder = new ServiceBinder();
	private int musicResid;

	// 实例化
	class ServiceBinder extends Binder {
		public MusicService getService() {
			return MusicService.this;
		}

	}
	public void onDestroy() {
		super.onDestroy();
		mp.stop();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mp = MediaPlayer.create(this, R.raw.music2);
		try {
			mp.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mp.start();
	}

	public void pauseMusic() {
		mp.pause();
	}

	public void startMusic() {
		mp.start();
		Toast.makeText(MusicService.this, "启动音乐", Toast.LENGTH_SHORT).show();
	}

	public boolean isPlaying() {
		return mp.isPlaying();
	}

	@Override
	public IBinder onBind(Intent intent) {
		musicResid = intent.getIntExtra("musicId",R.raw.music2);
		return myBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
}

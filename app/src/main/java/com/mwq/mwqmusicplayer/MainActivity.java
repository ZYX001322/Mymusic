package com.mwq.mwqmusicplayer;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private Button prevBtn;
    private Button pauseOrPlayBtn;
    private Button nextBtn;
    private TextView title;
    private ImageView imageMain;
    ServiceConnection serviceConnection;
    MusicService musicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final List<Music> musics = createMusic();
        title = (TextView) findViewById(R.id.musicText);
        imageMain = (ImageView) findViewById(R.id.imageMain);
        prevBtn = (Button) findViewById(R.id.pauseOrStart);
        pauseOrPlayBtn = (Button) findViewById(R.id.pauseOrStart);
        nextBtn = (Button) findViewById(R.id.nextSong);
        //当前音乐
        final int musicFlag = 0;
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicService = null;
            }
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicService = ((MusicService.ServiceBinder)service).getService();

            }
        };



        pauseOrPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(!musicService.isPlaying()){
                //startService(new Intent(MainActivity.this,MusicService.class));
                //musicService.onCreate(musics.get(musicFlag).getMusicResid());
                Intent intent = new Intent(MainActivity.this,MusicService.class);
                intent.putExtra("musicId", musics.get(musicFlag).getMusicResid());
                bindService(intent,
                        serviceConnection, Context.BIND_AUTO_CREATE);
                /*startService(intent);*/


                //musicService.onCreate(musics.get(0).getMusicResid());
                pauseOrPlayBtn.setText("Stop");
				/*}
				else {
					musicService.pauseMusic();
					pauseOrPlayBtn.setText("Play");
				}*/
            }
        });



    }


    private String getMusic(Music music){
        String title = music.getSongName()+"-"+music.getArtist()+"   ";
        String result ="";
        for(int i=0;i<6;i++){
            result = result + title;
        }
        return result;
    }

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
/*public class MainActivity extends Activity {
        private Button startServiceButton;
        private Button stopServiceButton;
        private Button bindServiceButton;
        private Button unBindServiceButton;
        private Button pauseButton;
        private Button ExitButton;
        MusicService musicService;

        //获取组件
        private void getWidget(){
            startServiceButton=(Button) findViewById(R.id.start);
            stopServiceButton=(Button) findViewById(R.id.stop);
            bindServiceButton=(Button) findViewById(R.id.bind);
            unBindServiceButton=(Button) findViewById(R.id.unbind);
            pauseButton=(Button) findViewById(R.id.pause);
            ExitButton=(Button) findViewById(R.id.exit);
        }

        private void registerListener(){
            startServiceButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startService(new Intent(MainActivity.this,MusicService.class));
                    Log.v("MainStadyServics", "start service");}
            });


            stopServiceButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    stopService(new Intent(MainActivity.this,MusicService.class));
                }
            });
            bindServiceButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    bindService(new Intent(MainActivity.this,MusicService.class), conn, Context.BIND_AUTO_CREATE);

                }
            });
            unBindServiceButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    unbindService(conn);

                }
            });
            pauseButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(musicService.isPlaying()){
                        musicService.pauseMusic();
                        pauseButton.setText("Resume Music");
                    }else{
                        musicService.startMusic();
                        pauseButton.setText("Pause Music");
                    }

                }
            });
            ExitButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    MainActivity.this.finish();

                }
            });
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            getWidget();
            registerListener();
        }
        private ServiceConnection conn = new ServiceConnection() {

            //无法获取到对象时
            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicService=null;
            }
            //获取到对象时
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicService = ((MusicService.ServiceBinder)service).getService();


            }
        };

        //启动服务
        public Button.OnClickListener startService = new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this,MusicService.class));
                Log.v("MainStadyServics", "start service");
            }};





    }
*/
}

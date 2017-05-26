package com.mwq.mwqmusicplayer;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import static com.mwq.mwqmusicplayer.R.layout.support_simple_spinner_dropdown_item;

public class MainActivity extends Activity {
    private Button prevBtn;
    private Button pauseOrPlayBtn;
    private Button nextBtn;
    private TextView title;
    private ImageView imageMain;
    private List<Music> musics;
    private int musicFlag = 0;
    MusicService musicService = null;
    private TextView totalTimeText;
    private TextView nowTimeText;
    int totalTime = 0;
    private SeekBar musicSeekBar;
    private ListView musicListView;
    ServiceConnection serviceConnection;
    Handler updateSeekBarHandler;
    UpdateUIBroadcastReceiver updateUIBroadcastReceiver;
    private DataBaseUtil musicDataBaseUtil;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        musics = createMusic();
        title = (TextView) findViewById(R.id.musicText);
        imageMain = (ImageView) findViewById(R.id.imageMain);
        prevBtn = (Button) findViewById(R.id.lastSong);
        pauseOrPlayBtn = (Button) findViewById(R.id.pauseOrStart);
        nextBtn = (Button) findViewById(R.id.nextSong);
        totalTimeText = (TextView) findViewById(R.id.totalTime);
        musicSeekBar = (SeekBar) findViewById(R.id.musicSeekBar);
        nowTimeText = (TextView) findViewById(R.id.nowTime);
        musicListView = (ListView) findViewById(R.id.musicListView);
        bindMusicService();
        //当前音乐
        musicFlag = 0;

        SQLiteDatabase musicDataBase = new DataBaseUtil(this).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("resid",R.raw.music1);
        cv.put("name","山外小楼夜听雨");
        cv.put("artist","任然");
        cv.put("image",R.drawable.music1);
        musicDataBase.insert("music",null,cv);

        List<Music> musicList = new ArrayList<Music>();
        List<String> showMusicList = new ArrayList<>();
        Cursor cursor = musicDataBase.query("music",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                Music tempMusic = new Music();
                tempMusic.setMusicResid(cursor.getInt(cursor.getColumnIndex("resid")));
                tempMusic.setSongName(cursor.getString(cursor.getColumnIndex("name")));
                tempMusic.setArtist(cursor.getString(cursor.getColumnIndex("artist")));
                tempMusic.setImage(cursor.getInt(cursor.getColumnIndex("image")));
                musicList.add(tempMusic);
                showMusicList.add(tempMusic.getSongName()+"-"+tempMusic.getArtist()+" ");
            }while (cursor.moveToNext());
            cursor.close();
        }




        //绑定服务
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        intent.putExtra("musicId",musics.get(musicFlag).getMusicResid());
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        updateSeekBarHandler = new Handler();

        String[] data = new String[]{"山外小楼夜听雨-任然","小半-陈粒","雪-杜雯媞"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (MainActivity.this, R.layout.support_simple_spinner_dropdown_item,showMusicList);
        musicListView.setAdapter(adapter);


        //设置按钮点击事件
        pauseOrPlayBtn.setOnClickListener(pauseOrPlay);
        nextBtn.setOnClickListener(nextSong);
        prevBtn.setOnClickListener(lastSong);

        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser==true){
                if(musicService!=null){
                    musicService.changeSeekBar(progress);
                    if(pauseOrPlayBtn.getText().equals("STOP")){
                        musicService.pauseMusic();
                    }else{
                        musicService.startMusic();
                    }
                }}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                musicService.pauseMusic();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                

            }
        });

        //动态注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("updateNowTime");
        //接受广播,调用方法,设置当前音乐时间
        updateUIBroadcastReceiver = new UpdateUIBroadcastReceiver();
        registerReceiver(updateUIBroadcastReceiver,intentFilter);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /*private void initDraw(){
        LinearLayout main = (LinearLayout) findViewById(R.id.mainLinerLayout);
        ImageButton imageButton = (ImageButton) findViewById(R.id.nextSong);
        DrawView drawView = new DrawView(this);
        drawView.invalidate();
        imageButton.
        main.addView(drawView);
    }*/

    private void initMusicSeekBar(){
        musicSeekBar.setProgress(0);
    }

    private class UpdateUIBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            nowTimeText.setText(getMinuteAndSeccond(intent.getIntExtra("nowTime",0)));
            //musicSeekBar.setProgress(intent.getIntExtra("nowTime",0)/intent.getIntExtra("totalTime",999999999));
            if(intent.getIntExtra("nextSongFlag",0)==1){
                nextBtn.performClick();
            }
        }
    }

    private void bindMusicService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicService = ((MusicService.ServiceBinder) service).getService();
                totalTime = musicService.getTotalTime();
                totalTimeText.setText(getMinuteAndSeccond(musicService.getTotalTime()));
                musicSeekBar.setMax(musicService.getTotalTime());
                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        musicSeekBar.setProgress(musicService.getCurrentPositon());
                    }
                };
                timer.schedule(timerTask,1000,500);

            }


        };
    }

    private Button.OnClickListener nextSong = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (musicService != null) {
                musicService.nextSong();
                musicFlag += 1;
                title.setText(getMusicTitle(musics.get(musicFlag % 3)));
                imageMain.setImageResource(musics.get(musicFlag % 3).getImage());
                pauseOrPlayBtn.setText("PLAY");
                totalTimeText.setText(getMinuteAndSeccond(musicService.getTotalTime()));
                initMusicSeekBar();
                Log.i("信息", String.valueOf(musicService.getTotalTime()));

                Toast.makeText(MainActivity.this, "下一首", Toast.LENGTH_SHORT);
            }
        }
    };

    private Button.OnClickListener lastSong = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (musicService != null) {
                musicService.prevSong();
                musicFlag += 2;
                title.setText(getMusicTitle(musics.get(musicFlag % 3)));
                imageMain.setImageResource(musics.get(musicFlag % 3).getImage());
                pauseOrPlayBtn.setText("PLAY");
                initMusicSeekBar();
                //totalTimeText.setText(totalTime);
                Toast.makeText(MainActivity.this, "上一首", Toast.LENGTH_SHORT);
            }
        }
    };

    private Button.OnClickListener pauseOrPlay = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (musicService == null) {
                Intent intent = new Intent(MainActivity.this, MusicService.class);
                intent.putExtra("musicId", musics.get(musicFlag).getMusicResid());
                bindService(intent,
                        serviceConnection, Context.BIND_AUTO_CREATE);
                pauseOrPlayBtn.setText("PLAY");
                Toast.makeText(MainActivity.this, "播放", Toast.LENGTH_SHORT).show();
            } else if (musicService.isPlaying() == true) {
                musicService.pauseMusic();
                pauseOrPlayBtn.setText("STOP");
                Toast.makeText(MainActivity.this, "暂停", Toast.LENGTH_SHORT).show();
            } else if (musicService.isPlaying() == false) {
                musicService.startMusic();
                pauseOrPlayBtn.setText("PLAY");
                Toast.makeText(MainActivity.this, "播放", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private String getMinuteAndSeccond(int time) {
        String minutes = String.valueOf((time / 60000));
        if (minutes.equals("0")) {
            minutes = "00";
        } else if (minutes.length() == 1) {
            minutes = "0" + minutes;
        }
        String secconds = String.valueOf((time % 60000) / 1000);
        if (secconds.length() == 1) {
            secconds = "0" + secconds;
        } else if (secconds.length() == 0) {
            secconds = "00";
        }
        return minutes + ":" + secconds;
    }






    private String getMusicTitle(Music music) {
        String title = music.getSongName() + "-" + music.getArtist() + "               ";
        String result = "";
        for (int i = 0; i < 4; i++) {
            result = result + title;
        }
        return result;
    }

    private List<Music> createMusic() {
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
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

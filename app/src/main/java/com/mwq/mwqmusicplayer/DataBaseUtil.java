package com.mwq.mwqmusicplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by wqmei on 2017/5/26.
 */

public class DataBaseUtil extends SQLiteOpenHelper{
    private static final String baseName = "musicData";
    private static final int version = 1;
    private static final String tableName = "music";
    private Context context1;



    public void addMusic(SQLiteDatabase db, Music music){
        String sqlSentence = "INSERT into music ";
        String sql1 = "(";
        String sql2 = "(";
        if(music!=null){
        if(music.getMusicResid()!=0){
            sql1 +="resid,";
            sql2 +=String.valueOf(music.getMusicResid())+",";
        }if (music.getSongName()!=null){
            sql1 +="name,";
                sql2 += String.valueOf(music.getSongName())+",";
            }if(music.getArtist()!=null){
                sql1 +="artist,";
                sql2 += String.valueOf(music.getArtist()) +",";
            }if(music.getImage()!=0){
                sql1 += "image";
                sql2 += String.valueOf(music.getImage())+",";
            }if(sql1.substring(sql1.length()-1).equals(",")){
                sql1 = sql1.substring(0,sql1.length()-1)+")";
            }else{
                sql1 +=")";
            }
            if(sql2.substring(sql2.length()-1).equals(",")){
                sql2 = sql2.substring(0,sql2.length()-1)+")";
            }else{
                sql2 +=")";
            }
        sqlSentence = sqlSentence +sql1 + " VALUES " + sql2;
        db.execSQL(sqlSentence);
        }

    }

    /*public List<Music> getAllMusic(SQLiteDatabase db){
        //db.query(tableName,new String[]{"id","resid","name","artist","image"});
        List<Music> musics =db.query(tableName,null,null,);
        return musics;
    }*/

    public DataBaseUtil(Context context) {
        super(context, baseName, null, version);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " +
                //"IF NOT EXISTS " +
                tableName+
                "(id integer primary key autoincrement, resid id integer," +
                "name varchar(50),artist varchar(20),image integer)");
        Toast.makeText(context1,"成功创建数据库", LENGTH_SHORT).show();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

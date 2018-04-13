package com.example.mypc.music;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListSongActivity extends AppCompatActivity {

    ListView lvSong;
    private SongAdaptet adapter;
    public static ArrayList<Song> items;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_song);

        lvSong = (ListView) findViewById(R.id.lv_song);
        toolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Danh sách nhạc");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Danh sách nhạc</font>"));


        initData();

        adapter = new SongAdaptet(getApplicationContext(), R.layout.cus_row_list_song, items);
        lvSong.setAdapter(adapter);

        lvSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                startActivity(new Intent(ListSongActivity.this,PlayActivity.class).putExtra("pos",position));
                startActivity(new Intent(ListSongActivity.this, PlayActivity.class).putExtra("pos", position));
            }
        });
    }

    private void initData() {
        items = new ArrayList<Song>();

        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int pathColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            //add songs to list
            do {
                int thisId = musicCursor.getInt(durationColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisPath = musicCursor.getString(pathColumn);
                int thisDuration = musicCursor.getInt(durationColumn);

                items.add(new Song(thisTitle, thisPath, "", thisArtist, thisDuration));
            }
            while (musicCursor.moveToNext());
        }

        Collections.sort(items, new Comparator<Song>() {
            @Override
            public int compare(Song cv1, Song cv2) {
                return cv1.getName().compareToIgnoreCase(cv2.getName());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_song_activity, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.tiemKiem_CV(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}

package com.example.mypc.music;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    LinearLayout contentView;
    ImageButton imbPlay, imbStop, imbPrev, imbNext;
    TextView txtTen, txtTimeSong, txtTimeTotal, tvTimeSleep;
    ImageView imvBack, imvClock;
    CircleImageView imgCD;
    SeekBar sbSong;
    ArrayList<Song> arrayListSong;
    int idx = 0, layout = 1;
    MediaPlayer mediaPlayer;
    Animation animation, zoomTitle;
    Toolbar toolbar;
    CountDownTimer timerSleep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_1);

        AnhXa();

//        animation = AnimationUtils.loadAnimation(this, R.anim.cd_rotate);
        zoomTitle = AnimationUtils.loadAnimation(this, R.anim.zoom);

        CreateMediaPlayer();
        doPlay();
    }

    private void stop_sleepp(int time, int interval) {
        tvTimeSleep.setVisibility(View.VISIBLE);
        imvClock.setVisibility(View.VISIBLE);
        timerSleep = new CountDownTimer(time, interval) {

            public void onTick(long millisUntilFinished) {
//                int h = (int) (time - millisUntilFinished);

                // đếm ngược
                String countDown = Ultils.getStringTimeFromDuration((int) millisUntilFinished);
                tvTimeSleep.setText(countDown);
            }

            public void onFinish() {
                // tắt app
                doStop();
                moveTaskToBack(true);

                // tắt máy
//                try {
//                    Process proc = Runtime.getRuntime()
//                            .exec(new String[]{ "su", "-c", "reboot -p" });
//                    proc.waitFor();
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
            }
        };
        timerSleep.start();
        Toast.makeText(this, "Đã bật hẹn giờ tắt sau " + Ultils.getStringTimeFromDuration(time), Toast.LENGTH_LONG).show();
    }

    private void UpdateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = Ultils.getStringTimeFromDuration(mediaPlayer.getCurrentPosition());
                txtTimeSong.setText(currentTime);
                // cập nhật progess của sbSong
                sbSong.setProgress(mediaPlayer.getCurrentPosition());

                // kiểm tra hết bài -> next
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        idx++;
                        if (idx > arrayListSong.size() - 1) {
                            idx = 0;
                        }
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        CreateMediaPlayer();
                        mediaPlayer.start();
                        imbPlay.setImageResource(R.drawable.ic_pause);
                        SetTimeTotal();
                        UpdateTimeSong();
                    }
                });
                handler.postDelayed(this, 500);
            }
        }, 100);
    }

    private void SetTimeTotal() {
        String timeTotal = Ultils.getStringTimeFromDuration(mediaPlayer.getDuration());
        txtTimeTotal.setText(timeTotal);
        // gán duration cho max của sbSong
        sbSong.setMax(mediaPlayer.getDuration());
    }

    private void CreateMediaPlayer() {
//        mediaPlayer = MediaPlayer.create(PlayActivity.this, arrayListSong.get(idx).getFile());
//        txtTen.setText(arrayListSong.get(idx).getTen());

        Uri uri = Uri.parse(arrayListSong.get(idx).getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);


        txtTen.setText(arrayListSong.get(idx).getName());
        txtTen.startAnimation(zoomTitle);

        // set color title actionbar
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" + arrayListSong.get(idx).getName() + " </font>"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaPlayer.stop();
    }

    private void AnhXa() {
        contentView = (LinearLayout) findViewById(R.id.contentView);
        imbNext = (ImageButton) findViewById(R.id.imageButtonNext);
        imbPrev = (ImageButton) findViewById(R.id.imageButtonPrev);
        imbPlay = (ImageButton) findViewById(R.id.imageButtonPlay);
        imbStop = (ImageButton) findViewById(R.id.imageButtonStop);
        txtTen = (TextView) findViewById(R.id.textviewTitle);
        txtTimeSong = (TextView) findViewById(R.id.textViewTimeSong);
        txtTimeTotal = (TextView) findViewById(R.id.textViewTimeTotal);
        tvTimeSleep = (TextView) findViewById(R.id.tvTimeSleep);
        sbSong = (SeekBar) findViewById(R.id.seekBarTime);
        imgCD = (CircleImageView) findViewById(R.id.imageViewCD);
        imvBack = (ImageView) findViewById(R.id.imvBack);
        imvClock = (ImageView) findViewById(R.id.imvClock);

        imbNext.setOnClickListener(this);
        imbPrev.setOnClickListener(this);
        imbStop.setOnClickListener(this);
        imbPlay.setOnClickListener(this);
        imvBack.setOnClickListener(this);
        sbSong.setOnSeekBarChangeListener(this);

        idx = getIntent().getIntExtra("pos", 0);
        arrayListSong = new ArrayList<Song>();
        arrayListSong.addAll(ListSongActivity.items);

        toolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_play_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int time;
        switch (item.getItemId()) {
            case R.id.action_10:
                time = 600000;
                stop_sleepp(time, 1000);
                break;
            case R.id.action_30:
                time = 1800000;
                stop_sleepp(time, 1000);
                break;
            case R.id.action_45:
                time = 2700000;
                stop_sleepp(time, 1000);
                break;
            case R.id.action_60:
                time = 3600000;
                stop_sleepp(time, 1000);
                break;
            case R.id.action_90:
                time = 5400000;
                stop_sleepp(time, 1000);
                break;
            case R.id.action_clear:
                timerSleep.cancel();
                tvTimeSleep.setVisibility(View.INVISIBLE);
                imvClock.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Đã tắt hẹn giờ ", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_renew:
                reNewLayout();
//                reloadContentView();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reloadContentView() {
        layout++;
        if (layout % 2 == 0) {
            contentView.setBackgroundColor(Color.parseColor("#ffffff"));
            imvBack.setBackgroundColor(Color.parseColor("#0583b0"));
            toolbar.setBackgroundColor(Color.parseColor("#0583b0"));
            txtTen.setTextColor(Color.parseColor("#0583b0"));
            imgCD.setImageResource(R.drawable.singer);
            imgCD.setBorderWidth(3);
            imgCD.setBorderColor(Color.BLACK);
            imvClock.setImageResource(R.drawable.ic_alarms_red);
            tvTimeSleep.setTextColor(Color.parseColor("#ff0000"));
            txtTimeTotal.setTextColor(Color.parseColor("#000000"));
            txtTimeSong.setTextColor(Color.parseColor("#000000"));
            imbNext.setBackgroundColor(Color.parseColor("#0583b0"));
            imbPlay.setBackgroundColor(Color.parseColor("#0583b0"));
            imbPrev.setBackgroundColor(Color.parseColor("#0583b0"));
            imbStop.setBackgroundColor(Color.parseColor("#0583b0"));
        } else {
            contentView.setBackgroundResource(R.drawable.black_background);
            imvBack.setBackgroundColor(Color.parseColor("#000000"));
            toolbar.setBackgroundResource(R.drawable.black_background);
            txtTen.setTextColor(Color.parseColor("#ffffff"));
            imgCD.setImageResource(R.drawable.banner);
            imgCD.setBorderWidth(3);
            imgCD.setBorderColor(Color.WHITE);
            imvClock.setImageResource(R.drawable.ic_alarms);
            tvTimeSleep.setTextColor(Color.parseColor("#ffffff"));
            txtTimeTotal.setTextColor(Color.parseColor("#ffffff"));
            txtTimeSong.setTextColor(Color.parseColor("#ffffff"));
            imbPrev.setBackground(Drawable.createFromPath("@null"));
            imbPlay.setBackground(Drawable.createFromPath("@null"));
            imbStop.setBackground(Drawable.createFromPath("@null"));
            imbNext.setBackground(Drawable.createFromPath("@null"));
        }
    }

    private void reNewLayout() {
        layout++;
        if (layout % 2 == 0) {
            doStop();
            setContentView(R.layout.activity_play_2);
            AnhXa();
            zoomTitle = AnimationUtils.loadAnimation(this, R.anim.zoom);
            CreateMediaPlayer();
            doPlay();
        } else {
            doStop();
            setContentView(R.layout.activity_play_1);
            AnhXa();
            zoomTitle = AnimationUtils.loadAnimation(this, R.anim.zoom);
            CreateMediaPlayer();
            doPlay();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButtonPlay:
                doPlay();
                break;
            case R.id.imageButtonPrev:
                doPrev();
                break;
            case R.id.imageButtonNext:
                doNex();
                break;
            case R.id.imageButtonStop:
                doStop();
                break;
            case R.id.imvBack:
//                doStop();
                onBackPressed();
                break;
            default:
                break;
        }
    }

    private void doStop() {
        mediaPlayer.stop();
        mediaPlayer.release();
        imbPlay.setImageResource(R.drawable.ic_play);
        CreateMediaPlayer();
    }

    private void doNex() {
        idx++;
        if (idx > arrayListSong.size() - 1) {
            idx = 0;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        CreateMediaPlayer();
        mediaPlayer.start();
        imbPlay.setImageResource(R.drawable.ic_pause);
        SetTimeTotal();
        UpdateTimeSong();

    }

    private void doPrev() {
        idx--;
        if (idx < 0) {
            idx = arrayListSong.size() - 1;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        CreateMediaPlayer();
        mediaPlayer.start();
        imbPlay.setImageResource(R.drawable.ic_pause);
        SetTimeTotal();
        UpdateTimeSong();

    }

    private void doPlay() {
        if (mediaPlayer.isPlaying()) {
            // nếu đang play -> pause -> đối hình
            mediaPlayer.pause();
            imbPlay.setImageResource(R.drawable.ic_play);
        } else {
            // nếu đang pause -> play -> đối hình
            mediaPlayer.start();
            imbPlay.setImageResource(R.drawable.ic_pause);
        }
        SetTimeTotal();
        UpdateTimeSong();

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mediaPlayer.seekTo(seekBar.getProgress());
    }
}

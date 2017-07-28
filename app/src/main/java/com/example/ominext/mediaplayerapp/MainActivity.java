package com.example.ominext.mediaplayerapp;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//1. List bài hát: 1 recyclerView trong đó có: row (row là fragment) , mỗi row có 1 cái ctrúc dliệu, 1 cái adapter, dữ liệu sẽ được lưu trong sqlite
//view pager with fragment để chuyển đổi giữa 2 màn hình (màn hình tiếp theo cũng là fragment)
//1 nút play, 1 nút pause, 1 nút next bài, 1 nút prev, 1 nút lặp lại, 1 nút cho phát ngẫu nhiên
//cập nhật lyric của bài hát. phải lưu trong csdl -> cấu trúc dữ liệu
public class MainActivity extends AppCompatActivity {
    MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(this);

    @BindView(R.id.button_random)
    Button buttonRandom;
    @BindView(R.id.button_back)
    Button buttonBack;
    @BindView(R.id.button_play)
    Button buttonPlay;
    @BindView(R.id.button_next)
    Button buttonNext;
    @BindView(R.id.button_replay)
    Button buttonReplay;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.tvCurrentTime)
    TextView tvCurrentTime;
    @BindView(R.id.tvTotalTime)
    TextView tvTotalTime;
    @BindView(R.id.title)
    TextView title;
    Handler handler;
    MediaPlayer mediaPlayer = new MediaPlayer();

    private int totalTime = 0;
    boolean audioAvailable = false;
    private int counter = 0;
    int check = 0;
    boolean play = true;//biến ktra xem có cho phép bật hay không
    int i = 0;
    RecyclerView.LayoutManager layoutManager;
    List<MyData> listSong = new ArrayList<>();
    MyAdapterRecyclerView adapter;

    //Trước khi load xong ấn play thì sẽ bị lỗi
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        handler = new Handler();
        //chèn dliệu vào sqlite
        insertData();
        //tạo 1 dòng
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        List<MyData> list = mySQLiteHelper.getAllSong();
        listSong.addAll(list);
        adapter = new MyAdapterRecyclerView(this, listSong);
        recyclerView.setAdapter(adapter);
        buttonPlay.setBackground(getResources().getDrawable(R.drawable.play));
        //viewpager
//        setupViewPager(viewPager);
//        //tab
//        tabs.setupWithViewPager(viewPager);
    }

//    private void setupViewPager(ViewPager viewPager) {
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFragment(new Fragment(), "Lyric");
//        viewPager.setAdapter(adapter);
//    }

//    class ViewPagerAdapter extends FragmentPagerAdapter {
//        private final List<Fragment> fragmentList = new ArrayList<>();
//        private final List<String> fragmentTitleList = new ArrayList<>();
//
//        public ViewPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return fragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return fragmentList.size();
//        }
//
//        public void addFragment(Fragment fragment, String title) {
//            fragmentList.add(fragment);
//            fragmentTitleList.add(title);
//        }

    //        @Override
//        public CharSequence getPageTitle(int position) {
//            return super.getPageTitle(position);
//        }
//    }
    //Có dữ liệu trong data rồi
    //Có các hàm xử lý cho seekbar rồi
    //Hiển thị tất dữ liệu ra recyclerView <-
    //Bắt sự kiện cho item
    //Kích vào 1 item thì gọi hàm initMedia
    //Kích vào nút play lần 1 thì phát và hiển thị nút pause
    //Kích vào nút play (nút pause) lần 2 thì dừng hiển thị nút play
    public void insertData() {
        //chèn dữ liệu vào 1 danh sách list, chèn dữ liệu vào trong database.
        insert("Bức tranh từ nước mắt", "http://zmp3-mp3-s1-te-zmp3-fpthn-1.zadn.vn/29923f67b823517d0832/1668834566220689309?key=VXkvotvG9GWiIgxLjhl8lQ&expires=1501310227", "5:24");
        insert("Không cần thêm một ai nữa", "http://zmp3-mp3-s1-te-zmp3-fpthn-2.zadn.vn/e486ee0ae74e0e10575f/240316487502201369?key=zt0HfLTLQLtOWYoBetufoQ&expires=1501311082", "4:12");
        insert("Gọi mưa", "http://zmp3-mp3-s1-te-zmp3-fpthn-2.zadn.vn/a903c8e1cba522fb7bb4/9206259410002477454?key=P7AVjj7SmVvfNe1GVNtnYA&expires=1501311644", "4:20");
        insert("Em của ngày hôm qua", "http://zmp3-mp3-s1-te-zmp3-fpthn-1.zadn.vn/b47247d44e90a7cefe81/2858089880453935061?key=wIijlkOkx0kA30xCTlfyaw&expires=1501311354", "3:52");
        insert("Nắng ấm xa dần", "http://zmp3-mp3-s1-te-zmp3-fpthn-1.zadn.vn/b28a472c4e68a736fe79/6386199754778197268?key=4F4KmTL_YvpRHQlY7vxpXQ&expires=1501311209", "3:54");
        insert("Như phút ban đầu", "http://zmp3-mp3-s1-te-vnso-qt-4.zadn.vn/00e8062f9c6b75352c7a/8259131834330418717?key=bnAfXsEzDavgXFVAP8gFhA&expires=1501313536", "4:13");
        insert("Phía sau một cô gái", "http://zmp3-mp3-s1-te-vnso-qt-4.zadn.vn/a32e305cab1842461b09/4870257449214198184?key=CSujMe5nkkqiE690hoyr1g&expires=1501313598", "4:30");
        insert("Mình là gì của nhau", "http://zmp3-mp3-s1-te-media-backup-qt-1.zadn.vn/6848a9e333a7daf983b6/6214618151873325179?key=e3WAkgkpJu_lNVZU04rU4Q&expires=1501313699", "4:55");
    }

    public void insert(String name, String url, String time) {
        MyData data = new MyData();
        data.setName(name);
        data.setTime(time);
        data.setUrl(url);
        mySQLiteHelper.insert(data);
    }

    @OnClick({R.id.button_random, R.id.button_back, R.id.button_play, R.id.button_next, R.id.button_replay})
    public void onViewClicked(View view) {
        //Kích vào item nút pause
        switch (view.getId()) {
            case R.id.button_random:
                i = rand(0, listSong.size() - 1);
                tvTotalTime.setText("00:00");
                tvCurrentTime.setText("00:00");
//                    buttonPlay.setVisibility(View.INVISIBLE);
                title.setText(listSong.get(i).getName());
                buttonPlay.setEnabled(false);
                initMedia(listSong.get(i).getUrl(), i);
                break;
            case R.id.button_back:

                if (i >= 1) {
                    i--;
                    tvTotalTime.setText("00:00");
                    tvCurrentTime.setText("00:00");
                    title.setText(listSong.get(i).getName());
                    buttonPlay.setEnabled(false);
                    initMedia(listSong.get(i).getUrl(), i);
                }
                break;
            case R.id.button_play:
                //nếu cho phép bật thì bật, sau đó cho phép pause
                if (play == true) {
                    if (audioAvailable) {
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                            //chạy current time
                            countTimer();
                            seekBar.setOnSeekBarChangeListener(seekBarChange);
                            check = 1;//set flag Đã từng ấn nút play
                            buttonPlay.setBackground(getResources().getDrawable(R.drawable.pause));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Vui lòng chờ trong ít phút", Toast.LENGTH_LONG).show();
                    }
                    play = false;//không cho phép bật
                } else {//ngược lại nếu không cho phép bật thì dừng+kích hoạt chế độ cho phép bật
                    mediaPlayer.pause();
                    buttonPlay.setBackground(getResources().getDrawable(R.drawable.play));
                    play = true;
                }
                break;
            case R.id.button_next:
                //1. xử lý với trường hợp trong lúc load dữ liệu kích nút play và pause:
                //cần: chỉ khi nào load xong thì mới cho phép kích play (kích play ms có tác dung)
                //lỗi: kích play và pause liên tục sẽ mắc lỗi không chạy được nhạc

                //2. Khi kích next thì không bật play, load xong mới bật play
                //Trong khi kích next gọi đến hàm initMedia, gọi hàm xong lấy được source, lấy được source, load đc giây mới cho phép sử dụng nút play
                if (i < listSong.size() - 1) {
                    i++;
                    tvTotalTime.setText("00:00");
                    tvCurrentTime.setText("00:00");
                    title.setText(listSong.get(i).getName());
                    buttonPlay.setEnabled(false);
                    initMedia(listSong.get(i).getUrl(), i);
                }
                break;
            case R.id.button_replay:
                if (check == 1) {
                    mediaPlayer.setLooping(true);
                    Toast.makeText(getApplicationContext(), "Đã kích hoạt chế độ lặp lại", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public int rand(int min, int max) {
        try {
            Random rn = new Random();
            int range = max - min + 1;
            int randomNum = min + rn.nextInt(range);
            return randomNum;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void initMedia(String dataSource, int position) {//sẽ gọi lại khi lấy được đường link
        //tạo mới 1 media với đường dẫn
        i = position;
        if (check == 1) {//nếu đã từng bật
            mediaPlayer.pause();
            buttonPlay.setBackground(getResources().getDrawable(R.drawable.play));
            play = true;
        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(dataSource);
            //Get song link
            mediaPlayer.setOnBufferingUpdateListener(onBufferingLoading);//audio load vào bộ đệm và đang load đến phần nào
            mediaPlayer.setOnPreparedListener(onPrepareAudio);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Không load được dữ liệu...", Toast.LENGTH_LONG).show();
        }
    }

    private SeekBar.OnSeekBarChangeListener seekBarChange = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayer.seekTo(seekBar.getProgress());
        }
    };
    private MediaPlayer.OnBufferingUpdateListener onBufferingLoading = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
            //i: là phần trăm audio đã load được khác với phần đang play
            seekBar.setSecondaryProgress(i);
        }
    };
    private MediaPlayer.OnPreparedListener onPrepareAudio = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            //audio sẵn sàng play
            //lấy đc total+time
            totalTime = mediaPlayer.getDuration();//time tính theo milliseconds
            int minute = totalTime / 1000 / 60;
            int second = totalTime / 1000 % 60;
            tvTotalTime.setText(minute + ":" + second);
            audioAvailable = true;//flag để đánh dấu mediaPlayer sẵn sàng
            buttonPlay.setEnabled(true);
        }
    };

    private void countTimer() {
        handler.postDelayed(timerCounter, 1000);//sau 1000 millisecond sẽ thay đổi giá trị 1 lần; timerCounter thuộc kiểu runnable
    }

    private Runnable timerCounter = new Runnable() {
        @Override
        public void run() {
            //lấy thời gian hiện tại
            counter = mediaPlayer.getCurrentPosition();
            int minute = (int) counter / 1000 / 60;
            int second = (int) counter / 1000 % 60;
            //định dạng
//               java.text.SimpleDateFormat simpleDateFormat=new java.text.SimpleDateFormat("mm:ss");
            tvCurrentTime.setText(minute + ":" + second);
            seekBar.setMax(mediaPlayer.getDuration());
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            //Dùng đệ quy để lặp lại
            countTimer();
        }
    };
}

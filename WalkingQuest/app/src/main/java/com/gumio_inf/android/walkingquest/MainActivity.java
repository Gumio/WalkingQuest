package com.gumio_inf.android.walkingquest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences pref;

    // モンスター
    private ImageButton monster;
    private int [] monsters;
    private int monsterNumber;
    private Random random;

    // モンスターHPゲージ
    private ProgressBar hpBar;

    // モンスターHPのText
    private TextView hpText;

    // 最大HP
    private int maxHp;

    // 現在のHP;
    private int damagedHp;

    // 攻撃力
    public static int attack;

    // 攻撃音出す用
    private AudioAttributes audioAttributes;
    private SoundPool soundPool;
    private int attacked;
    private int magiced;
    private int destroyed;

    // BGM
    private MediaPlayer bgm;

    // 振動
    private Vibrator vibrator;

    // アニメーション
    private TranslateAnimation animation;

    // FAB
    private FloatingActionButton attackFab;
    private FloatingActionButton magicFab;
    private FloatingActionButton mapAccessFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        random = new Random();
        pref = getSharedPreferences("Data", Context.MODE_PRIVATE);
        attack = pref.getInt("attack", 0);

        // bgm読み込み
        bgm = MediaPlayer.create(this, R.raw.boss_bgm);
        bgm.setLooping(true);
        bgm.start();

        audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(1)
                .build();

        // 効果音をロードしておく
        attacked = soundPool.load(this, R.raw.sword_slash1, 1);
        magiced = soundPool.load(this, R.raw.magic_ice, 1);
        destroyed = soundPool.load(this, R.raw.monster1, 1);

        maxHp = pref.getInt("hp", 1000);
        damagedHp = maxHp;
        hpText = (TextView)findViewById(R.id.hp);
        hpText.setText(damagedHp + "/" + maxHp);

        hpBar = (ProgressBar)findViewById(R.id.hpBar);
        hpBar.setMax(maxHp);
        hpBar.setProgress(maxHp);

        monsters = new int[100];
        monsters[0] = R.mipmap.time_dragon;
        monsters[1] = R.mipmap.red_dragon;
        monsters[2] = R.mipmap.lucifer;
        monsters[3] = R.mipmap.kisaragi;
        monsters[4] = R.mipmap.unko;
        monsters[5] = R.mipmap.mare;
        monsters[6] = R.mipmap.cty;
        monsters[7] = R.mipmap.kimoi;
        monsters[8] = R.mipmap.quin;
        monsters[9] = R.mipmap.dark;
        monsterNumber = random.nextInt(10);

        // 関連付け
        monster = (ImageButton)findViewById(R.id.monster);
        attackFab = (FloatingActionButton)findViewById(R.id.attackFab);
        magicFab = (FloatingActionButton)findViewById(R.id.magicFab);

        // バイヴ
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        // アニメーション
        animation = new TranslateAnimation(0, 30, 0, 30);
        animation.setDuration(100);

        // リスナー登録
        attackFab.setOnClickListener(this);
        magicFab.setOnClickListener(this);

        monster.setBackgroundResource(monsters[monsterNumber]);
    }

    @Override
    protected void onDestroy() {
        bgm.stop();
        bgm.reset();
        bgm.release();

        bgm = null;
    }

    @Override
    public void onClick(View v) {
        // モンスター揺らしてみる
        monster.startAnimation(animation);
        // 効果音の再生
        // play(ロードしたID, 左音量, 右音量, 優先度, ループ,再生速度)
        switch (v.getId()) {
            case R.id.attackFab:
                soundPool.play(attacked, 1.0f, 1.0f, 0, 0, 1);
                break;
            case R.id.magicFab:
                soundPool.play(magiced, 1.0f, 1.0f, 0, 0, 1);
                break;
            default:
                break;
        }

        // 震わす
        vibrator.vibrate(100);

        damagedHp -= attack;
        hpText.setText(damagedHp + "/" + maxHp);
        hpBar.setProgress(damagedHp);

        if (damagedHp <= 0) {
            soundPool.play(destroyed, 1.0f, 1.0f, 0, 0, 1);
            monsterNumber = random.nextInt(10);
            monster.setBackgroundResource(monsters[monsterNumber]);
            maxHp *= 2;
            SharedPreferences.Editor edit = pref.edit();
            edit.putInt("hp", maxHp);
            edit.apply();
            damagedHp = maxHp;
            hpText.setText(maxHp + "/" + maxHp);
            hpBar.setMax(maxHp);
            hpBar.setProgress(maxHp);
        }
    }

    protected void tapMapView(View v) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}

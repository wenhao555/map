package com.example.administrator.baidumap_testdemo_a;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;


public class WuActivity extends Fragment {
    private TextView t1;
    private Button tiyan;
    private String aString;
    private boolean isVisibleToUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_three, container, false);

        tiyan = view.findViewById(R.id.tiyan);
        t1 = view.findViewById(R.id.t1);

        startAnim();

        AssetManager assetManager = getActivity().getAssets();
        Typeface tf = Typeface.createFromAsset(assetManager, "font/FZYTK.TTF");
        t1.setTypeface(tf);


        tiyan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences ii = getActivity().getSharedPreferences("iss", MODE_PRIVATE);
                SharedPreferences.Editor edit = ii.edit();
                edit.putString("str", "123");
                edit.commit();
                aString = ii.getString("str", "");
                Intent it = new Intent(getContext(), MainActivity.class);
                startActivity(it);
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            startAnim();
        } else {

            if (t1 != null) {
                Animation anim1 = t1.getAnimation();
                if (anim1 != null) anim1.cancel();
                Animation anim2 = tiyan.getAnimation();
                if (anim2 != null) anim2.cancel();
                t1.setVisibility(View.INVISIBLE);
                tiyan.setVisibility(View.INVISIBLE);

            }

        }
    }

    private void startAnim() {
        if (t1 != null && isVisibleToUser) {
            Animation anim1 = t1.getAnimation();
            if (anim1 != null) anim1.cancel();
            Animation anim2 = tiyan.getAnimation();
            if (anim2 != null) anim2.cancel();

            TranslateAnimation tion = new TranslateAnimation(0, 0, 0, -50);//向上移动动画
            tion.setDuration(2000);//动画从开始到结束的所用的时间
            tion.setFillAfter(true);
            t1.setVisibility(View.VISIBLE);
            t1.startAnimation(tion);

            AlphaAnimation tion1 = new AlphaAnimation(0, 1);
            tion1.setDuration(1000);
            tion1.setStartOffset(1000);
            tion1.setFillAfter(true);
            tiyan.setVisibility(View.VISIBLE);
            tiyan.startAnimation(tion1);
        }
    }


}

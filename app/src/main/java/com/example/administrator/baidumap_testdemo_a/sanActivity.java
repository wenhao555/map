package com.example.administrator.baidumap_testdemo_a;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class sanActivity extends Fragment {
    private ImageView img1;
    private TextView t1,t2;

    protected boolean isCreated = false;
    private Handler handler=new Handler();


  public   View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_one, container, false);

        t1=view.findViewById(R.id.t1);
        t2=view.findViewById(R.id.t2);

        TranslateAnimation tion = new TranslateAnimation(0, 0, 0, -50);//向上移动动画
        tion.setDuration(2000);//动画从开始到结束的所用的时间
        tion.setFillAfter(true);
        t1.setVisibility(View.VISIBLE);
        t1.startAnimation(tion);

        AlphaAnimation tion1 = new AlphaAnimation(0,1);
        tion1.setDuration(2000);
        tion1.setStartOffset(1000);
        t2.setVisibility(View.VISIBLE);
        t2.startAnimation(tion1);

        AssetManager assetManager = getActivity().getAssets();
        Typeface tf=Typeface.createFromAsset(assetManager,"font/FZYTK.TTF");
        t1.setTypeface(tf);
        t2.setTypeface(tf);

        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if (t1!=null) {
                Animation anim1 = t1.getAnimation();
                if (anim1 != null) anim1.cancel();
                Animation anim2 = t2.getAnimation();
                if (anim2 != null) anim2.cancel();

                TranslateAnimation tion = new TranslateAnimation(0, 0, 0, -50);//向上移动动画
                tion.setDuration(2000);//动画从开始到结束的所用的时间
                tion.setFillAfter(true);
                t1.setVisibility(View.VISIBLE);
                t1.startAnimation(tion);

                AlphaAnimation tion1 = new AlphaAnimation(0, 1);
                tion1.setDuration(2000);
                tion1.setStartOffset(1000);
                t2.setVisibility(View.VISIBLE);
                t2.startAnimation(tion1);
            }

        }else {

            if (t1!=null){
                Animation anim1 = t1.getAnimation();
                if (anim1 != null) anim1.cancel();
                Animation anim2 = t2.getAnimation();
                if (anim2 != null) anim2.cancel();
                t1.setVisibility(View.INVISIBLE);
                t2.setVisibility(View.INVISIBLE);
            }

        }
    }
}





































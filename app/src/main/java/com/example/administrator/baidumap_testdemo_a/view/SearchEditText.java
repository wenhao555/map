package com.example.administrator.baidumap_testdemo_a.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.example.administrator.baidumap_testdemo_a.R;

import java.util.List;

public class SearchEditText extends LinearLayout {
    private EditText mQuery;
    private ImageView mImage;
    private ListView mShowItme;
    private ArrayAdapter<String> adapter;
    private SuggestionSearch mSearch;
    private PopupWindow showWindow;

    public SearchEditText(Context context) {
        this(context, null);
    }

    public SearchEditText(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = LayoutInflater.from(context).inflate(R.layout.view_serch_edittext, this);
        mQuery = view.findViewById(R.id.edit_query);
        mImage = view.findViewById(R.id.iv_close);

        //获取百度数据
        mSearch = SuggestionSearch.newInstance();

        initPopuWindow();
        initListener();


    }

    private void initListener() {
        mQuery.addTextChangedListener(textChangedListener);
        mQuery.setOnFocusChangeListener(focusChangeListener);
        mImage.setOnClickListener(clickListener);
        mSearch.setOnGetSuggestionResultListener(getSuggestionResultListener);
    }

    private void initPopuWindow() {
        mShowItme = new ListView(getContext());
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line);
        mShowItme.setAdapter(adapter);
        mShowItme.setOnItemClickListener(itemClickListener);

        showWindow = new PopupWindow(mShowItme, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        showWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        showWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        showWindow.setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
    }


    OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                showWindow.showAsDropDown(mQuery);
                mQuery.setCursorVisible(true);
            }
        }
    };

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mQuery.setText(adapter.getItem(i));
            //隐藏光标
            mQuery.setCursorVisible(false);
            //隐藏列表
            showWindow.dismiss();
        }
    };

    TextWatcher textChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (TextUtils.isEmpty(charSequence)) {
                mImage.setVisibility(GONE);
                adapter.clear();
                adapter.notifyDataSetChanged();
            } else {
                mImage.setVisibility(View.VISIBLE);
                //开始查询
                String city = "郑州";
                mSearch.requestSuggestion((new SuggestionSearchOption()).keyword(charSequence.toString()).city(city));
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    //清空
    OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            mQuery.setText("");
            //显示列表
            showWindow.showAsDropDown(mQuery);
            //显示光标
            mQuery.setCursorVisible(true);
        }
    };

    //监听查询结果
    OnGetSuggestionResultListener getSuggestionResultListener = new OnGetSuggestionResultListener() {
        @Override
        public void onGetSuggestionResult(SuggestionResult suggestionResult) {
            adapter.clear();
            if (suggestionResult == null || suggestionResult.getAllSuggestions() == null || mQuery.getText().length() == 0) {
                return;
            }


            List<SuggestionResult.SuggestionInfo> allSuggestions = suggestionResult.getAllSuggestions();
            for (SuggestionResult.SuggestionInfo info : allSuggestions) {

                if (info.key != null) {
                    adapter.add(info.key + " " + info.district + " " + info.city);
                }
            }
            adapter.notifyDataSetChanged();
        }
    };

    public void setText(String str) {
        mQuery.setText(str);
    }

    public String getText() {
        return mQuery.getText().toString();
    }
}

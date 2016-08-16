package com.example.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import com.zhangcheng.widget.AutoTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhangcheng on 16/8/16.
 */
public class AutoTextActivity extends Activity {
    private static final int CHANGE_TEXT = 110;
    private List<String> autoTextList;
    private AutoTextView atv;
    private int count = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CHANGE_TEXT:
                    int position =  msg.arg1;
                    if(autoTextList!=null && autoTextList.size()>0){
                        atv.setText(autoTextList.get(position));
                    }

                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_text);
        atv = (AutoTextView) findViewById(R.id.atv);
        initData();


    }

    private void initData() {
        autoTextList = new ArrayList<>();
        autoTextList.add("this is 1 :?");
        autoTextList.add("this is 2 :?");
        autoTextList.add("this is 3 :?");
        atv.setText(autoTextList.get(count));
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                count++;
                if(count == autoTextList.size()){
                    count = 0;
                }
                Message obtain = Message.obtain();
                obtain.what = CHANGE_TEXT;

                obtain.arg1 = count;
                handler.sendMessage(obtain);

            }
        }, 5000,5000);
    }
}

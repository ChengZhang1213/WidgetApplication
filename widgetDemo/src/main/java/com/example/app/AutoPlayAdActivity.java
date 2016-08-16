package com.example.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.zhangcheng.widget.AutoPlayAdLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangcheng on 16/8/16.
 */
public class AutoPlayAdActivity extends Activity {
    private AutoPlayAdLayout auto_play_ad;
    private int[] images;
    private Context context;
    private List<Ads> adsList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_play_ad);
        auto_play_ad = (AutoPlayAdLayout) findViewById(R.id.auto_play_ad);
        context = AutoPlayAdActivity.this;
        adsList = new ArrayList<>();
        images = new int[]{R.drawable.cupcake, R.drawable.donut};
        for (int i = 0; i < images.length; i++) {
            Ads ads = new Ads();
            ads.icon = images[i];
            ads.title = "1";
            adsList.add(ads);
        }
        ADPagerAdapter adPagerAdapter = new ADPagerAdapter(context, adsList);
        auto_play_ad.setAutoPlayAdapter(adPagerAdapter);
//        auto_play_ad.addTextChangeListener(new AddTextChangeListener() {
//            @Override
//            public void doChangeText(int i) {
//                tv_short_message.setText(mainNewsMDLs.get(i).getTitle());
//            }
//        });

    }

    private class ADPagerAdapter implements AutoPlayAdLayout.AutoPlayAdapter {

        private List<Ads> adsList;
        private Context context;

        public ADPagerAdapter(Context context, List<Ads> adsList) {
            this.adsList = adsList;
            this.context = context;
        }


        @Override
        public boolean isEmpty() {
            return adsList.size() > 0 ? false : true;
        }

        @Override
        public View getView(int position) {
            View inflate = LayoutInflater.from(context).inflate(R.layout.item_ads, null);
            ImageView iv_content = (ImageView) inflate.findViewById(R.id.iv_content);
            iv_content.setImageResource(adsList.get(position).icon);
            return inflate;
        }

        @Override
        public int getCount() {
            return adsList.size();
        }
    }
}

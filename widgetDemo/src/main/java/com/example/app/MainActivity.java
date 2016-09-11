package com.example.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        findViewById(R.id.btn_auto_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,AutoTextActivity.class));
            }
        });

        findViewById(R.id.btn_load_more_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,LoadMoreActivity.class));

            }
        });
        findViewById(R.id.btn_auto_play_ad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,AutoPlayAdActivity.class));

            }
        });

        findViewById(R.id.btn_path_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(context,PathDemoActivity.class));
                startActivity(new Intent(context,RecyclerViewActivity.class));
            }
        });

    }
}



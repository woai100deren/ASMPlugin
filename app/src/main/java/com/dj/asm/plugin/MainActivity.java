package com.dj.asm.plugin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dj.asm.dot.annotation.DotAnnotation;

import java.lang.annotation.Native;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean tag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
            @DotAnnotation("textView click")
            @Override
            public void onClick(View v) {
                Log.e("123","456");
            }
        });

        findViewById(R.id.textView2).setOnClickListener(this);
        findViewById(R.id.textView2).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textView2:
                textView2Click();
                break;
            default:
                break;
        }
    }

    @DotAnnotation("textView2 click")
    private void textView2Click(){
        Log.e("111","222");
    }
}
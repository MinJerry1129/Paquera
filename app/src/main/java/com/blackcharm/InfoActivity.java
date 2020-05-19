package com.blackcharm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.blackcharm.registerlogin.TermsScreen;

public class InfoActivity extends AppCompatActivity {
    RelativeLayout _layout1;
    RelativeLayout _layout2;
    RelativeLayout _layout3;
    Button _next1;
    Button _next2;
    Button _goLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        _layout1 = (RelativeLayout)findViewById(R.id.layout1);
        _layout2 = (RelativeLayout)findViewById(R.id.layout2);
        _layout3 = (RelativeLayout)findViewById(R.id.layout3);
        _next1 = (Button) findViewById(R.id.btn_next1);
        _next2 = (Button) findViewById(R.id.btn_next2);
        _goLogin = (Button) findViewById(R.id.btn_login);
        getReady();
    }
    private void getReady(){
        _next1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _layout1.setVisibility(RelativeLayout.GONE);
                _layout2.setVisibility(RelativeLayout.VISIBLE);
            }
        });
        _next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _layout2.setVisibility(RelativeLayout.GONE);
                _layout3.setVisibility(RelativeLayout.VISIBLE);
            }
        });
        _goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(), TermsScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
//                val intent =  Intent(this, TermsScreen::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//                finish()
            }
        });
    }
}

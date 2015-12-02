package com.beerwhere.poster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beerwhere.poster.utils.MyApplication;

import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Button login_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login_btn = (Button) findViewById(R.id.buton1);
    }

    private void volleyPost() {
        String url = "http://192.168.1.117:5001/login";
        HashMap<String, String> map = new HashMap<String, String>();
        EditText name = (EditText) findViewById(R.id.name);
        EditText pass = (EditText) findViewById(R.id.password);
        String login_name = name.getText().toString();
        String login_pass = pass.getText().toString();
        map.put("user_name", login_name);
        map.put("user_pws", login_pass);
        JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    System.out.println("login name" + response);
                    if (response.getInt("data") == 1) {
                        System.out.println("wangqi is in");
                        final Intent login_intent = new Intent();
                        login_intent.setClass(MainActivity.this, OrderLists.class);
                        Bundle bundleM = new Bundle();
                        bundleM.putString("userid", response.getString("data"));  //查询出配送人员id，并通过intent传值给OrderLists的activity
                        login_intent.putExtras(bundleM);
                        startActivity(login_intent);
                    }
                    else{
                        hintKb();
                        Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("network error", "error");
            }
        });
        request.setTag("abcPost");
        MyApplication.queue.add(request);
    }

    public void loginCheck(View view){
        volleyPost();
    }
    // 隐藏键盘
    private void hintKb() {
        // 得到InputMethodManager的实例
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {            // 如果开启
            // 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}

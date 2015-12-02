package com.beerwhere.poster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beerwhere.poster.utils.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderLists extends AppCompatActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_lists);
        init();
    }

    private void init(){

        volleyPostList();
        dataList = new ArrayList<Map<String, Object>>();

        listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);
    }

    private void volleyPostList() {
        String url = "http://192.168.1.117:5001/orderlist";
        HashMap<String, String> map2 = new HashMap<String, String>();
        map2.put("userid", getIntent().getExtras().getString("userid"));
        JSONObject jsonObject2 = new JSONObject(map2);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject2, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response2) {
                dataList = getDataList(response2);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("network error", "error");
            }
        });
        request.setTag("abcPost2");
        MyApplication.queue.add(request);
    }

    private List<Map<String, Object>> getDataList(JSONObject j) {
        JSONArray jsonArray = null;

        String client_addr = "";

        try {
            jsonArray = j.getJSONArray("orderlist");
            int array_len = jsonArray.length();
            for (int i = 0; i < array_len; i++) {
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                client_addr = jsonObject2.getString("client_addr");
                String submit_time = jsonObject2.getString("submit_time");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("pic", R.drawable.xg);
                map.put("text", client_addr);
                map.put("submit_time", submit_time);
                map.put("l_id", jsonObject2.getString("order_id"));
                dataList.add(map);
            }
            simpleAdapter = new SimpleAdapter(this, dataList, R.layout.items,
                    new String[]{"pic", "text", "submit_time"},
                    new int[]{R.id.pic, R.id.text, R.id.submit_time});
            listView.setAdapter(simpleAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("dataList=======" + dataList);
        return dataList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_lists, menu);
        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final Intent itm_intent = new Intent();
        Map<String, Object> detail = dataList.get(i);
        itm_intent.setClass(OrderLists.this, ItemDetail.class);
        Bundle bundle = new Bundle();
        bundle.putString("order_id", (String) detail.get("l_id"));
        itm_intent.putExtras(bundle);
        startActivity(itm_intent);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        switch (i) {
            case SCROLL_STATE_FLING:
                Log.i("Main", "浏览列表");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("pic", R.drawable.xg);
                map.put("text", "佳佳阿基" + i);
                dataList.add(map);
                simpleAdapter.notifyDataSetChanged();
                break;
            case SCROLL_STATE_IDLE:
//                int bgcolor = getResources().getColor(R.color.bgcolor);
//                findViewById(R.id.list).setBackgroundColor(bgcolor);
                Log.i("Main", "视图已停止滑动");
                break;
            case SCROLL_STATE_TOUCH_SCROLL:
//                int mycolor = getResources().getColor(R.color.mycolor);
//                findViewById(R.id.list).setBackgroundColor(mycolor);
                Log.i("Main", "手指没离开屏幕正在滑动");
                break;
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }

}

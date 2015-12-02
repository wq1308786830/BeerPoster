package com.beerwhere.poster;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beerwhere.poster.maps.SimpleGPSNaviActivity;
import com.beerwhere.poster.utils.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemDetail extends AppCompatActivity {

    private static final int msgKey1 = 1;
    private List<Map<String, Object>> dataList;
    private TextView submit_time;
    private Date after;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);
        submit_time = (TextView) findViewById(R.id.submit_time1);
        volleyPostList();
        new TimeThread().start();

    }

    private void volleyPostList() {
        String url = "http://192.168.1.117:5001/orderdetail";
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        String order_id = getIntent().getExtras().getString("order_id");
        map.put("order_id", Integer.parseInt(order_id));
        JSONObject jsonObject = new JSONObject(map);
        Log.e("jsonObj", jsonObject.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dataList = getDataList(response);
                Log.d("TAG_list======", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("network error", "请求失败");
            }
        });

        ImageRequest imageRequest = new ImageRequest(
                "http://pica.nipic.com/2008-01-14/2008114144910806_2.jpg",
                new Response.Listener<Bitmap>() {

                    ImageView article_img = (ImageView) findViewById(R.id.article_img);

                    @Override
                    public void onResponse(Bitmap response) {
                        article_img.setImageBitmap(response);
                    }
                }, 200, 200, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ImageView article_img = (ImageView) findViewById(R.id.article_img);
                article_img.setImageResource(R.drawable.small_corona);
            }
        });
        request.setTag("abcPost3");
        imageRequest.setTag("imageRequest1");
        MyApplication.queue.add(request);
        MyApplication.queue.add(imageRequest);
    }

    private List<Map<String, Object>> getDataList(JSONObject j) {
        TextView order_name = (TextView) findViewById(R.id.order_name);
        TextView order_location = (TextView) findViewById(R.id.order_location);
        TextView order_tel = (TextView) findViewById(R.id.order_tel);
        TextView article_name = (TextView) findViewById(R.id.article_name);
        TextView unit_price = (TextView) findViewById(R.id.unit_price);
        TextView article_count = (TextView) findViewById(R.id.article_count);
        TextView order_pay = (TextView) findViewById(R.id.order_pay);
        TextView order_no = (TextView) findViewById(R.id.order_no);
        try {

            //
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(j.getString("submit_time"));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, 20);
            Date after = calendar.getTime();
            this.setAfter(after);
            //
            order_name.setText(j.getString("client_name"));
            order_location.setText(j.getString("client_addr"));
            order_tel.setText(j.getString("client_tel"));
            article_name.setText(j.getString("article_name"));
            unit_price.setText("￥" + j.getString("unit_price"));
            article_count.setText("x" + j.getString("article_count"));
            order_pay.setText("￥" + j.getString("order_money"));
            order_no.setText(j.getString("orderno"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public void callClient(View view) {
        /**
         * 拨打客户电话
         */
        TextView order_tel = (TextView) findViewById(R.id.order_tel);
        String client_tel = (String) order_tel.getText();
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + client_tel));
        startActivity(intent);
    }

    public void callAMap(View view){
        TextView orer_addr = (TextView) findViewById(R.id.order_location);
        String client_addr = (String) orer_addr.getText();
        Intent gpsNaviIntent = new Intent(ItemDetail.this, SimpleGPSNaviActivity.class);
        gpsNaviIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        gpsNaviIntent.putExtra("cleint_adde", client_addr);

        startActivity(gpsNaviIntent);
    }

    public void acceptOrder(View view) {
        /**
         * 接受订单
         */

    }

    public class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {

                }
            } while (true);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case msgKey1:
                    long l = ItemDetail.this.getAfter().getTime() - (new Date()).getTime();
                    CharSequence sysTimeStr = DateFormat.format("mm:ss", l);
                    submit_time.setText(sysTimeStr);
                    break;

                default:
                    break;
            }
        }
    };


    public Date getAfter() {
        return after;
    }

    public void setAfter(Date after) {
        this.after = after;
    }

}

package com.example.weatherforecast.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.weatherforecast.R;
import com.example.weatherforecast.service.AutoUpdateService;
import com.example.weatherforecast.util.HttpUtil;
import com.example.weatherforecast.util.Utility;

import java.io.InputStream;

/**
 * Created by misaki on 2016/7/3.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {
private LinearLayout weatherInfoLayout;

    //用于显示城市名
    private TextView cityName;
    //用于显示发布时间
    private TextView publishText;
    //用于显示天气描述信息
    private TextView weatherDespText;
    //用于显示气温1
    private TextView temp1;
    //用于显示气温2
    private TextView temp2;
    //用于显示当前日期
    private TextView currentDateText;
    //切换城市按钮
    private Button switchCity;
    //更新天气按钮
    private View refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout= (LinearLayout) findViewById(R.id.weather_info_layout);
        cityName= (TextView) findViewById(R.id.city_name);
        publishText= (TextView) findViewById(R.id.publish_text);
        weatherDespText= (TextView) findViewById(R.id.weather_desp);
        temp1= (TextView) findViewById(R.id.temp1);
        temp2= (TextView) findViewById(R.id.temp2);
        currentDateText= (TextView) findViewById(R.id.current_date);
        switchCity= (Button) findViewById(R.id.swich_city);
        refreshWeather=findViewById(R.id.refresh_weather);
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
        String countryCode=getIntent().getStringExtra("country_code");
        if(!TextUtils.isEmpty(countryCode)){
            publishText.setText("正在同步中……");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityName.setVisibility(View.INVISIBLE);
            queryWeatherCode(countryCode);
        }
        else{
            showWeather();
        }
    }

    private void showWeather() {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        cityName.setText(sharedPreferences.getString("city_name",""));

        temp1.setText(sharedPreferences.getString("temp1",""));
        temp2.setText(sharedPreferences.getString("temp2",""));
        weatherDespText.setText(sharedPreferences.getString("weather_Desp",""));
        publishText.setText("今天"+sharedPreferences.getString("publish_Time","")+"发布");
        currentDateText.setText(sharedPreferences.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityName.setVisibility(View.VISIBLE);
        Intent intent=new Intent(WeatherActivity.this, AutoUpdateService.class);
       startService(intent);

    }

    private void queryWeatherCode(String countryCode) {
        String address="http://www.weather.com.cn/data/list3/city"+countryCode+".xml";

        queryFromServer(address, "countryCode");
    }

    private void queryFromServer(final String address, final String type) {

        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void OnFinish(String response) {
                if ("countryCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        final String[] array = response.split("\\|");


                        if (array.length == 2 && array != null) {
                            String weatherCode = array[1];

                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {

                    Utility.handleWeatherResponse(WeatherActivity.this, response);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            showWeather();
                        }
                    });
                }





    }




            @Override
            public void OnError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });

            }
        });
    }
    private void queryWeatherInfo(String weatherCode) {
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";

        queryFromServer(address,"weatherCode");

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.swich_city:
                Intent intent=new Intent(WeatherActivity.this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("正在同步中……");
                SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode=sharedPreferences.getString("weather_code","");
                if(!TextUtils.isEmpty(weatherCode)){
                queryWeatherInfo(weatherCode);}
                break;
            default:
                break;
        }

    }
}

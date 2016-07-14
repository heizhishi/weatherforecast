package com.example.weatherforecast.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.example.weatherforecast.model.City;
import com.example.weatherforecast.model.Country;
import com.example.weatherforecast.model.Province;
import com.example.weatherforecast.model.WeatherForecastDB;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by misaki on 2016/7/2.
 */
public class Utility {
    public synchronized static boolean handleProvincesResponcse(WeatherForecastDB weatherForecastDB,String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces=response.split(",");
            if(allProvinces!=null&&allProvinces.length>0){
                for(String p:allProvinces){
                    String[] array=p.split("\\|");
                    Province province=new Province();
                    province.setProvinceName(array[1]);
                    province.setProvinceCode(array[0]);
                    weatherForecastDB.saveProvince(province);
                }
                return  true;
            }
        }
        return false;
    }
    public synchronized  static boolean handleCitiesResponse(WeatherForecastDB weatherForecastDB,String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities=response.split(",");
            if(allCities!=null&allCities.length>0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityName(array[1]);
                    city.setCityCode(array[0]);
                    city.setProvinceId(provinceId);
                    weatherForecastDB.saveCity(city);
                }
                return true;
            }

        }
        return  false;
    }

    public synchronized  static boolean handleCountriesResponse (WeatherForecastDB weatherForecastDB,String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCountries=response.split(",");
            if(allCountries.length>0&&allCountries!=null){
                for(String c:allCountries) {
                    String[] array=c.split("\\|");
                    Country country = new Country();
                    country.setCountryCode(array[0]);
                    country.setCountryName(array[1]);
                    country.setCityId(cityId);
                    weatherForecastDB.saveCountry(country);

                }
                return  true;
            }
        }
        return false;
    }
    //解析服务器返回的json数据，并将解析出的数据存储到本地
    public  static void handleWeatherResponse(Context context,String response){

        try {
Log.e("infofo",response);
            JSONObject jsonObject=new JSONObject(response);

            JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
            String cityname=weatherInfo.getString("city");
            String weatherCode=weatherInfo.getString("cityid");
            String temp1=weatherInfo.getString("temp1");
            String temp2=weatherInfo.getString("temp2");
            String weatherDesp=weatherInfo.getString("weather");
            String publishTime=weatherInfo.getString("ptime");

            saveWeatherInfo(context, cityname, weatherCode, temp1, temp2, weatherDesp, publishTime);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void saveWeatherInfo(Context context, String cityname, String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityname);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_Desp",weatherDesp);
        editor.putString("publish_Time",publishTime);
        editor.putString("current_date",simpleDateFormat.format(new Date()));
        editor.commit();

    }
}

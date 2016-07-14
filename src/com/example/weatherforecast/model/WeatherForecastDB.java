package com.example.weatherforecast.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.ViewGroup;
import com.example.weatherforecast.db.WeatherForecastOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by misaki on 2016/7/1.
 */
public class WeatherForecastDB {
    //数据库名
    public static final String DB_NAME="weather_forecast";
    //数据库版本
    public static final int VERSION=1;
    private static WeatherForecastDB weatherForecastDB;
    private static SQLiteDatabase sqLiteDatabase;
    //将构造方法私有化
    private WeatherForecastDB(Context context) {
        WeatherForecastOpenHelper weatherForecastOpenHelper = new WeatherForecastOpenHelper(context, DB_NAME, null, VERSION);
        sqLiteDatabase = weatherForecastOpenHelper.getWritableDatabase();
    }
        //获取WeatherForecast的实例
        public static  WeatherForecastDB  getInstance(Context context){
        if(weatherForecastDB==null){
            synchronized (WeatherForecastDB.class){
                if(weatherForecastDB==null){
                    weatherForecastDB=new WeatherForecastDB(context);
                }
            }
        }
    return weatherForecastDB;
    }


    //将Province实例存储到数据库
    public void saveProvince(Province province){
        if(province!=null){
            ContentValues contentValues=new ContentValues();
            contentValues.put("province_name",province.getProvinceName());
            contentValues.put("province_code",province.getProvinceCode());
            sqLiteDatabase.insert("Province",null,contentValues);
        }
    }
    public List<Province> loadProvinces(){
        List<Province> list= new ArrayList<Province>();
        Cursor cursor=sqLiteDatabase.query("Province",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Province province=new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);

            }while(cursor.moveToNext());
        }
        if(cursor!=null)
        {
            cursor.close();
        }




        return list;
    }
    public void saveCity(City city){
        if(city!=null){
            ContentValues contentValues=new ContentValues();
            contentValues.put("city_name",city.getCityName());
            contentValues.put("city_code",city.getCityCode());
            contentValues.put("province_id",city.getProvinceId());
            sqLiteDatabase.insert("City",null,contentValues);
        }
    }
    public List<City> loadCities(int provinceId){
        List<City> list=new ArrayList<City>();
        Cursor cursor=sqLiteDatabase.query("City",null,"province_id=?",new String[]{String.valueOf(provinceId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                City city=new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                list.add(city);

            }while(cursor.moveToNext());
        }
        if(cursor!=null){
            cursor.close();
        }
        return list;
    }
    public void saveCountry(Country country){
        if(country!=null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("country_name", country.getCountryName());
            contentValues.put("country_code", country.getCountryCode());
            contentValues.put("city_id", country.getCityId());
            sqLiteDatabase.insert("Country", null, contentValues);
        }

    }
    public List<Country> loadCountries(int cityId){
        List<Country> list=new ArrayList<Country>();
       Cursor cursor= sqLiteDatabase.query("Country",null,"city_id=?",new String[]{String.valueOf(cityId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
              Country country=new Country();
                country.setId(cursor.getInt(cursor.getColumnIndex("id")));
                country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
               country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
               country.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
               list.add(country);

            }while(cursor.moveToNext());
        }
        if(cursor!=null){
            cursor.close();
        }
        return  list;
    }

}

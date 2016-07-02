package com.example.weatherforecast.util;

import android.text.TextUtils;
import com.example.weatherforecast.model.City;
import com.example.weatherforecast.model.Country;
import com.example.weatherforecast.model.Province;
import com.example.weatherforecast.model.WeatherForecastDB;

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
}

package com.example.weatherforecast.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.example.weatherforecast.R;
import com.example.weatherforecast.model.City;
import com.example.weatherforecast.model.Country;
import com.example.weatherforecast.model.Province;
import com.example.weatherforecast.model.WeatherForecastDB;
import com.example.weatherforecast.util.HttpUtil;
import com.example.weatherforecast.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by misaki on 2016/7/2.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTRY=2;

    private ProgressDialog progressDialog;
    private TextView textView;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private WeatherForecastDB weatherForecastDB;
    private List<String> dateList= new ArrayList<String>();

    //省列表
   private  List<Province>  provinceList;
    //市列表
    private List<City>  cityList;
    //县列表
    private List<Country> countryList;
//选中的省份
    private Province selectedProvince;
    //选中的城市
    private City selectedCity;
    //当前选中级别
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
         listView= (ListView) findViewById(R.id.list_view);
        textView= (TextView) findViewById(R.id.title_text);
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dateList);
        listView.setAdapter(arrayAdapter);
        weatherForecastDB=WeatherForecastDB.getInstance(this);
        queryProvinces();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCountries();
                }
            }


        });
    }
//查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器查询
    private void queryProvinces() {
        provinceList=weatherForecastDB.loadProvinces();
        if(provinceList.size()>0){
            dateList.clear();
            for(Province province:provinceList){
                dateList.add(province.getProvinceName());

            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText("中国");
            currentLevel=LEVEL_PROVINCE;
        }else{
            queryFromServer(null,"province");
        }
    }
    //查询某个省所有的市，优先从数据库查找，如果没有查询到再去服务器查询
    private void queryCities() {
        cityList=weatherForecastDB.loadCities(selectedProvince.getId());
        if(cityList.size()>0){
            dateList.clear();
            for(City city :cityList){
                dateList.add(city.getCityName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectedProvince.getProvinceName());
            currentLevel=LEVEL_CITY;
        }else{
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }

    }
    //查询某个市所有的县，优先从数据库查找，如果没有查询到再去服务器查询
    private void queryCountries() {
        countryList=weatherForecastDB.loadCountries(selectedCity.getId());
        if(countryList.size()>0){
            dateList.clear();
            for(Country country:countryList) {

                dateList.add(country.getCountryName());
            }
                arrayAdapter.notifyDataSetChanged();
                listView.setSelection(0);
            textView.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTRY;


        }else{
            queryFromServer(selectedCity.getCityCode(),"country");
        }


    }


//根据传入的代号与类型
    private void queryFromServer(String code, final String type) {
        String address;
        if(!TextUtils.isEmpty(code)){
            address="http://www.weather.com.cn/date/list3/city"+code+".xml";

        }else{
            address="http://www.weather.com.cn/date/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void OnFinish(String response) {
                boolean result=false;
                if("province".equals(type)){
                    result= Utility.handleProvincesResponcse(weatherForecastDB,response);

                }
                else if("city".equals(type)){
                    result=Utility.handleCitiesResponse(weatherForecastDB,response,selectedProvince.getId());
                }
                else if("country".equals(type)){
                    result=Utility.handleCountriesResponse(weatherForecastDB,response,selectedCity.getId());
                }
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }
                           else if("city".equals(type)){
                               queryCities();
                            }
                           else if("country".equals(type)){
                                queryCountries();
                            }
                        }


                    });
                }
            }

            @Override
            public void OnError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private void showProgressDialog() {
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog() {
if(progressDialog!=null){
    progressDialog.dismiss();
}

    }

    @Override
    public void onBackPressed() {
        if(currentLevel==LEVEL_COUNTRY)
        { queryCities();}
        else  if(currentLevel==LEVEL_CITY){
            queryProvinces();
        }
        else if(currentLevel==LEVEL_PROVINCE){
            finish();
        }
    }
}

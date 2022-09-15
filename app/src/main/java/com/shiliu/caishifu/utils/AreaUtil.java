package com.shiliu.caishifu.utils;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.shiliu.caishifu.cons.Constant;
import com.shiliu.caishifu.model.Area;
import com.shiliu.caishifu.model.area.City;
import com.shiliu.caishifu.model.area.District;
import com.shiliu.caishifu.model.area.Province;

import java.util.ArrayList;
import java.util.List;


public class AreaUtil {

    /**
     * 初始化地区信息
     */
    public static void initArea(Context context) {
        if (Area.count(Area.class) == 0) {
            initData(context);
        }
    }

    private static void initData(Context context) {
        String jsonData = new GetJsonDataUtil().getJson(context, "area-csf.json");//获取assets目录下的省-市-县数据
        List<Province> provinceList = JSONArray.parseArray(jsonData, Province.class);
        int provinceSeq = 0;
        List<Area> areaList = new ArrayList<>();
        for (Province province : provinceList) {
            provinceSeq++;
            // 省
            Area provinceArea = new Area(province.getName(), "", Constant.AREA_TYPE_PROVINCE, provinceSeq);
            areaList.add(provinceArea);
            int citySeq = 0;
            //市
            List<City> cityList = province.getCity();
            for (City city : cityList) {
                citySeq++;
                Area cityArea = new Area(city.getName(), province.getName(), Constant.AREA_TYPE_CITY, citySeq);
                areaList.add(cityArea);
                //县或区
                List<District> districtList = city.getDistrict();
                int districtSeq = 0;
                for (District district : districtList) {
                    districtSeq++;
                    Area districtArea = new Area(district.getName(), city.getName(),
                            Constant.AREA_TYPE_DISTRICT, districtSeq, district.getPostCode());
                    areaList.add(districtArea);
                }
            }
        }
        Area.saveInTx(areaList);
    }
}

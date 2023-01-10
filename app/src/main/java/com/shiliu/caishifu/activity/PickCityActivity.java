package com.shiliu.caishifu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.shiliu.caishifu.R;
import com.shiliu.caishifu.adapter.AreaAdapter;
import com.shiliu.caishifu.dao.AreaDao;
import com.shiliu.caishifu.model.Area;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 选择市
 *
 */
public class PickCityActivity extends CommonActivity {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.lv_area)
    ListView mCityLv;

    AreaAdapter mCityAdapter;
    AreaDao mAreaDao;
    String mProvinceName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.area_picker_activity);
        ButterKnife.bind(this);
        initStatusBar();
        initView();
        mAreaDao = new AreaDao();

        mProvinceName = getIntent().getStringExtra("provinceName");
        final List<Area> areaList = mAreaDao.getCityListByProvinceName(mProvinceName);
        mCityAdapter = new AreaAdapter(this, areaList);
        mCityLv.setAdapter(mCityAdapter);
        mCityLv.setOnItemClickListener((parent, view, position, id) -> {
            Area area = areaList.get(position);
            Intent intent = new Intent(PickCityActivity.this, PickDistrictActivity.class);
            intent.putExtra("provinceName", mProvinceName);
            intent.putExtra("cityName", area.getName());
            startActivity(intent);
        });

        // 压入销毁栈
        FinishActivityManager.getManager().addActivity(this);
    }

    public void back(View view) {
        finish();
        FinishActivityManager.getManager().finishActivity(this);
    }

    public void initView() {
        setTitleStrokeWidth(mTitleTv);
    }

}
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
 * 选择省
 *
 */
public class PickProvinceActivity extends CommonActivity {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.lv_area)
    ListView mProvinceLv;

    AreaAdapter mProvinceAdapter;
    AreaDao mAreaDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.area_picker_activity);
        ButterKnife.bind(this);
        initStatusBar();
        initView();
        mAreaDao = new AreaDao();

        final List<Area> areaList = mAreaDao.getProvinceList();
        mProvinceAdapter = new AreaAdapter(this, areaList);
        mProvinceLv.setAdapter(mProvinceAdapter);
        mProvinceLv.setOnItemClickListener((parent, view, position, id) -> {
            Area area = areaList.get(position);
            Intent intent = new Intent(PickProvinceActivity.this, PickCityActivity.class);
            intent.putExtra("provinceName", area.getName());
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
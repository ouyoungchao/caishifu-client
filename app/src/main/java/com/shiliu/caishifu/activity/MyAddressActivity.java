package com.shiliu.caishifu.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.shiliu.caishifu.R;
import com.shiliu.caishifu.adapter.MyAddressAdapter;
import com.shiliu.caishifu.cons.Constant;
import com.shiliu.caishifu.dao.AddressDao;
import com.shiliu.caishifu.model.Address;
import com.shiliu.caishifu.model.User;
import com.shiliu.caishifu.utils.NetworkUtil;

import java.util.List;

import butterknife.BindView;

/**
 * 我的地址
 *
 */
public class MyAddressActivity extends BaseActivity {

    @BindView(R.id.ll_root)
    LinearLayout mRootLl;

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.iv_add)
    ImageView mAddIv;

    @BindView(R.id.lv_address)
    ListView mAddressLv;

    MyAddressAdapter mMyAddressAdapter;
    User mUser;
    NetworkUtil networkUtil;
    AddressDao mAddressDao;
    // 弹窗
    PopupWindow mPopupWindow;

    @Override
    public int getContentView() {
        return R.layout.my_address_activity;
    }

    @Override
    public void initView() {
        initStatusBar();
        setTitleStrokeWidth(mTitleTv);
    }

    @Override
    public void initListener() {
        mAddIv.setOnClickListener(view ->
                startActivity(new Intent(MyAddressActivity.this, AddAddressActivity.class)));
    }

    @Override
    public void initData() {
        mUser = getUser();
        networkUtil = NetworkUtil.getInstance(this);
        mAddressDao = new AddressDao();

        final List<Address> addressList = mAddressDao.getAddressList();
        mMyAddressAdapter = new MyAddressAdapter(this, addressList);
        mAddressLv.setAdapter(mMyAddressAdapter);

        getAddressListByUserId(mUser.getUserId());
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 本地读取
        List<Address> addressList = mAddressDao.getAddressList();
        mMyAddressAdapter.setData(addressList);
        mMyAddressAdapter.notifyDataSetChanged();

        // 服务器读取
        getAddressListByUserId(mUser.getUserId());
    }

    private void getAddressListByUserId(String userId) {
        String url = Constant.BASE_URL + "users/" + userId + "/address";
        /*networkUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                final List<Address> addressList = JSONArray.parseArray(response, Address.class);
                if (null != addressList && addressList.size() > 0) {
                    // 持久化
                    mAddressDao.clearAddress();
                    for (Address address : addressList) {
                        if (null != address) {
                            mAddressDao.saveAddress(address);
                        }
                    }
                }
                mMyAddressAdapter.setData(addressList);
                mMyAddressAdapter.notifyDataSetChanged();
                mAddressLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        Address address = addressList.get(position);
                        showOperation(address);
                        return false;
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                final List<Address> addressList = mAddressDao.getAddressList();
                mMyAddressAdapter.setData(addressList);
                mMyAddressAdapter.notifyDataSetChanged();
                mAddressLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        Address address = addressList.get(position);
                        showOperation(address);
                        return false;
                    }
                });
            }
        });*/
    }

    private void deleteAddress(String userId, final String addressId) {
        String url = Constant.BASE_URL + "users/" + userId + "/address/" + addressId;
        /*networkUtil.httpDeleteRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mAddressDao.deleteAddressByAddressId(addressId);
                final List<Address> addressList = mAddressDao.getAddressList();
                mMyAddressAdapter.setData(addressList);
                mMyAddressAdapter.notifyDataSetChanged();
                mAddressLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        Address address = addressList.get(position);
                        showOperation(address);
                        return false;
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(MyAddressActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(MyAddressActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });*/
    }



    /**
     * 设置添加屏幕的背景透明度
     * 1.0完全不透明，0.0f完全透明
     *
     * @param bgAlpha 透明度值
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // 0.0-1.0
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

}
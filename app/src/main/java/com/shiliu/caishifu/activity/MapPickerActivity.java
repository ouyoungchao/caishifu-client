package com.bc.wechat.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.bc.wechat.R;
import com.bc.wechat.WechatApplication;
import com.bc.wechat.adapter.MapPickerAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.service.LocationService;
import com.bc.wechat.utils.BitmapLoaderUtil;
import com.bc.wechat.utils.FileUtil;
import com.bc.wechat.widget.ConfirmDialog;
import com.smarx.notchlib.NotchScreenManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;

/**
 * ???????????????
 *
 * @author zhou
 */
public class MapPickerActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = MapPickerActivity.class.getCanonicalName();
    private static final int REQUEST_PERMISSION_STORAGE = 0x01;

    List<PoiInfo> mPoiInfoList;
    Point mCenterPoint;
    // ????????????
    GeoCoder mGeoCoder;

    PoiInfo mCurentInfo;

    // ??????????????????????????????
    LatLng mLocationLatLng;

    @BindView(R.id.mv_map)
    MapView mMapView;

    LocationService locationService;
    BaiduMap mBaiduMap;

    @BindView(R.id.rl_map_holder)
    RelativeLayout mMapHolderRl;

    @BindView(R.id.btn_send_location)
    Button mSendLocationBtn;

    @BindView(R.id.tv_status)
    TextView mStatusTv;

    @BindView(R.id.lv_near_by)
    ListView mPoiLv;

    MapPickerAdapter mMapPickerAdapter;
    boolean mSendLocation;
    String mLocationType;

    int mWidth;
    int mHeight;
    float mDensity;
    int mDensityDpi;

    double mLatitude;
    double mLongitude;
    // ??????
    // ?????????"????????????????????????????????????158???"
    String mAddress;

    // ????????????
    String mAddressDetail;

    // ???
    String mProvince;
    // ???
    String mCity;
    // ?????????:"?????????"
    String mDistrict;
    // ????????????:"?????????"
    String mStreet;
    // ???????????????"158???"
    String mStreetNumber;
    String mName;
    NotchScreenManager notchScreenManager = NotchScreenManager.getInstance();

    @Override
    public int getContentView() {
        return R.layout.activity_map_picker;
    }

    @Override
    public void initView() {
        // ???????????????????????????
        notchScreenManager.setDisplayInNotch(this);
        getNotch();
        initStatusBar();

        locationService = WechatApplication.locationService;
        locationService.registerListener(mListener);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mDensity = displayMetrics.density;
        mDensityDpi = displayMetrics.densityDpi;
        mWidth = displayMetrics.widthPixels;
        mHeight = displayMetrics.heightPixels;
        initMap();

        mSendLocation = getIntent().getBooleanExtra("sendLocation", false);
        mLocationType = getIntent().getStringExtra("locationType");
        if (mSendLocation) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getResources().getDimensionPixelOffset(R.dimen.map_holder_height));
            mMapHolderRl.setLayoutParams(params);
        } else {
            // ????????????
            // ?????????????????????????????????
            locationService.unregisterListener(mListener);
            mSendLocationBtn.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mMapHolderRl.setLayoutParams(params);

            double latitude = getIntent().getDoubleExtra("latitude", 0);
            double longitude = getIntent().getDoubleExtra("longitude", 0);

            MyLocationData locationData = new MyLocationData.Builder()
                    .accuracy(100).direction(90.f).latitude(latitude).longitude(longitude).build();
            mBaiduMap.setMyLocationData(locationData);
            mBaiduMap.setMyLocationEnabled(true);

            LatLng ll = new LatLng(latitude, longitude);
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.mipmap.icon_current_location);
            OverlayOptions options = new MarkerOptions().position(ll).icon(descriptor).zIndex(10);
            mBaiduMap.addOverlay(options);

            // ??????????????????
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(latitude, longitude));
            mBaiduMap.animateMapStatus(u);
            mBaiduMap.clear();
            // ???????????????????????????
            mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
                    .location(new LatLng(latitude, longitude)));
        }

        mSendLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ??????????????????????????????
                String[] permissions = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
                requestPermissions(MapPickerActivity.this, permissions, REQUEST_PERMISSION_STORAGE);
            }
        });
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    private void initMap() {
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mMapView.showZoomControls(false);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(19.0f);
        mBaiduMap.setMapStatus(mapStatusUpdate);
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (null == mCenterPoint) {
                        return;
                    }
                    mCenterPoint = mBaiduMap.getMapStatus().targetScreen;
                    LatLng currentLatLng = mBaiduMap.getProjection().fromScreenLocation(mCenterPoint);
                    mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(currentLatLng));
                }
            }
        });

//        mBaiduMap.setOnMapTouchListener(null);
        // ?????????POI????????????
        mPoiInfoList = new ArrayList<>();
        // ???????????????mapView????????????????????????????????????????????????
        mCenterPoint = mBaiduMap.getMapStatus().targetScreen;
        mLocationLatLng = mBaiduMap.getMapStatus().target;
        // ??????
        mBaiduMap.setMyLocationEnabled(true);
        // ????????????logo zoomControl
        int count = mMapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ImageView || child instanceof ZoomControls) {
                child.setVisibility(View.INVISIBLE);
            }
        }

        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(mGeoListener);

        mPoiLv.setOnItemClickListener(this);

        mMapPickerAdapter = new MapPickerAdapter(MapPickerActivity.this, mPoiInfoList);
        mPoiLv.setAdapter(mMapPickerAdapter);
    }

    /**
     * ????????????????????????
     */
    private void getNotch() {
        // ?????????????????????
        notchScreenManager.getNotchInfo(this, notchScreenInfo -> {
            Log.i(TAG, "Is this screen notch? " + notchScreenInfo.hasNotch);
            if (notchScreenInfo.hasNotch) {
                for (Rect rect : notchScreenInfo.notchRects) {
                    Log.i(TAG, "notch screen Rect =  " + rect.toShortString());
                    // ???????????????TextView??????
                }
            }
        });
    }

    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                MyLocationData data = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        .latitude(location.getLatitude())
                        .longitude(location.getLongitude())
                        .build();
                mBaiduMap.setMyLocationData(data);

                // ?????????????????????
                MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
                mBaiduMap.setMyLocationConfiguration(config);
                mAddress = location.getAddrStr();
                mStreet = location.getStreet();
                mCity = location.getCity();

                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mLocationLatLng = currentLatLng;

                // ??????????????????
                MapStatusUpdate u = MapStatusUpdateFactory
                        .newLatLng(currentLatLng);
                mBaiduMap.animateMapStatus(u);
                mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
                        .location(currentLatLng));
            }
        }
    };

    OnGetGeoCoderResultListener mGeoListener = new OnGetGeoCoderResultListener() {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (null == result || result.error != SearchResult.ERRORNO.NO_ERROR) {
                mStatusTv.setText("????????????");
                mStatusTv.setVisibility(View.VISIBLE);
            } else {
                mStatusTv.setVisibility(View.GONE);
                // ??????????????????
                mLocationLatLng = result.getLocation();
                mAddress = result.getAddress();

                mStreet = result.getAddressDetail().street;
                mStreetNumber = result.getAddressDetail().streetNumber;

                mProvince = result.getAddressDetail().province;
                mCity = result.getAddressDetail().city;
                mDistrict = result.getAddressDetail().district;

                mAddressDetail = mDistrict + mStreet + mStreetNumber;

                mLatitude = result.getLocation().latitude;
                mLongitude = result.getLocation().longitude;

                mCurentInfo = new PoiInfo();
                mCurentInfo.address = result.getAddress();
                mCurentInfo.location = result.getLocation();
                mCurentInfo.name = "[????????????]";
                mPoiInfoList.clear();
                mPoiInfoList.add(mCurentInfo);

                // ???????????????????????????
                if (null != result.getPoiList()) {
                    mPoiInfoList.addAll(result.getPoiList());
                }
                mMapPickerAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationService.unregisterListener(mListener);
        locationService.stop();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationService.start();
        mMapView.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        mMapPickerAdapter.setNotifyTip(position);
        mMapPickerAdapter.notifyDataSetChanged();

        BitmapDescriptor mSelectIcon = BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_pick_map_geo);

        mBaiduMap.clear();

        PoiInfo poiInfo = mMapPickerAdapter.getItem(position);
        LatLng latLng = poiInfo.getLocation();
        // ????????????
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(mapStatusUpdate);

        // ???????????????
        OverlayOptions overlayOptions = new MarkerOptions().position(latLng).icon(mSelectIcon).anchor(0.5f, 0.5f);
        mBaiduMap.addOverlay(overlayOptions);

        mAddressDetail = poiInfo.address;
        mAddress = poiInfo.name;

        mLatitude = poiInfo.location.latitude;
        mLongitude = poiInfo.location.longitude;
    }

    public void back(View view) {
        finish();
    }

    /**
     * ????????????
     */
    public void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        // Android 6.0????????????????????????????????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> mPermissionList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(activity, permissions[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (mPermissionList.isEmpty()) {
                // ???????????????App????????????
                sendLocation();
            } else {
                // ??????????????????
                String[] requestPermissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                // ??????????????????onRequestPermissionsResult????????????
                ActivityCompat.requestPermissions(activity, requestPermissions, requestCode);
            }
        }
    }

    /**
     * requestPermissions?????????
     * ???????????????????????????????????????
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllGranted = true;
        // ??????????????????  ???????????????????????? ?????????????????????????????????
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                hasAllGranted = false;
                break;
            }
        }
        if (hasAllGranted) {
            sendLocation();
        } else {
            // ?????????????????????????????????????????????????????????
            handleRejectPermission(MapPickerActivity.this, permissions[0], requestCode);
        }
    }

    public void handleRejectPermission(final Activity context, String permission, int requestCode) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            final ConfirmDialog mConfirmDialog = new ConfirmDialog(MapPickerActivity.this, getString(R.string.request_permission),
                    getString(R.string.request_permission_storage),
                    getString(R.string.go_setting), getString(R.string.cancel), getColor(R.color.navy_blue));
            mConfirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                @Override
                public void onOkClick() {
                    mConfirmDialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getApplicationContext().getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }

                @Override
                public void onCancelClick() {
                    mConfirmDialog.dismiss();
                }
            });
            // ?????????????????????
            mConfirmDialog.setCancelable(false);
            mConfirmDialog.show();
        }
    }

    private void sendLocation() {
        if (Constant.LOCATION_TYPE_AREA.equals(mLocationType)) {
            // ???????????????????????????
            sendLocationArea();
        } else {
            // ??????????????????
            sendLocationMsg();
        }
    }

    /**
     * ??????????????????
     */
    private void sendLocationMsg() {
        if (null != mLocationLatLng) {
            int left = mWidth / 8;
            int top = (int) (mHeight - 1.5 * mWidth);
            // ????????????
            int width = mWidth - 2 * left;
            int height = (int) (0.6 * width);

            int right = mWidth - left;
            int bottom = top + height;


            Rect rect = new Rect(left, top, right, bottom);
            mBaiduMap.snapshotScope(rect, new BaiduMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap bitmap) {
                    String fileName = UUID.randomUUID().toString();
                    final String localPath = BitmapLoaderUtil.saveBitmapToLocal(bitmap, fileName);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<String> imageList = FileUtil.uploadFile(Constant.BASE_URL + "oss/file", localPath);
                            if (null != imageList && imageList.size() > 0) {
                                Intent intent = new Intent();
                                intent.putExtra("latitude", mLatitude);
                                intent.putExtra("longitude", mLongitude);
                                intent.putExtra("address", mAddress);
                                intent.putExtra("addressDetail", mAddressDetail);
                                intent.putExtra("path", imageList.get(0));
                                setResult(RESULT_OK, intent);
                                finish();
                                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);

                            }
                        }
                    }).start();

                }
            });
        }
    }

    /**
     * ??????????????????
     */
    private void sendLocationArea() {
        Intent intent = new Intent();
        // ???
        intent.putExtra("province", mProvince);
        // ???
        intent.putExtra("city", mCity);
        // ???
        intent.putExtra("district", mDistrict);
        // ????????????
        intent.putExtra("addressDetail", mAddressDetail);
        setResult(RESULT_OK, intent);
        finish();
    }

}
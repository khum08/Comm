package test.yzhk.com.comm.UI.activities;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import test.yzhk.com.comm.R;
import test.yzhk.com.comm.utils.ToastUtil;

public class MapActivity extends BaseActivity implements View.OnClickListener {

    public MapView mMapView;
    public BaiduMap mBaiduMap;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private BDLocation mCurrentLocation;
    //BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口
//原有BDLocationListener接口暂时同步保留。具体介绍请参考后文中的说明
    public FloatingActionButton mFab_add;
    private RelativeLayout rlAddBill;
    private boolean isAdd = false;
    private AnimatorSet addBillTranslate1;
    private AnimatorSet addBillTranslate2;
    private AnimatorSet addBillTranslate3;

    private int[] llId = new int[]{R.id.ll01, R.id.ll02, R.id.ll03};
    private LinearLayout[] ll = new LinearLayout[llId.length];
    private int[] fabId = new int[]{R.id.miniFab01, R.id.miniFab02, R.id.miniFab03};
    private FloatingActionButton[] fab = new FloatingActionButton[fabId.length];
    private MapStatusUpdate mMapStatusUpdate;
    private MyLocationConfiguration mConfig;
    private BitmapDescriptor mCurrentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSDK();
        setContentView(R.layout.activity_map);
        initTitle();
        initFloating();
    }


    //初始化sdk设置
    private void initSDK() {
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setScanSpan(0);
//可选，设置发起定位请求的间隔，int类型，单位ms
//如果设置为0，则代表单次定位，即仅定位一次，默认为0
//如果设置非0，需设置1000ms以上才有效
        option.setOpenGps(true);
//可选，设置是否使用gps，默认false
//使用高精度和仅用设备两种定位模式的，参数必须设置为true
        option.setLocationNotify(true);
//可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.setIgnoreKillProcess(false);
//可选，定位SDK内部是一个service，并放到了独立进程。
//设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
        option.SetIgnoreCacheException(false);
//可选，设置是否收集Crash信息，默认收集，即参数为false
        option.setIsNeedAddress(true);//设置是否需要地址信息，默认不需要
        option.setEnableSimulateGps(false);
//可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        mLocationClient.setLocOption(option);
//mLocationClient为第二步初始化过的LocationClient对象
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
//更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient.start();
    }


    //初始化标题
    private void initTitle() {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("千度地图");
        mMapView = (MapView) findViewById(R.id.bmapView);

    }

    //初始化浮动按钮
    private void initFloating() {
        mFab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        rlAddBill = (RelativeLayout) findViewById(R.id.rlAddBill);
        for (int i = 0; i < llId.length; i++) {
            ll[i] = (LinearLayout) findViewById(llId[i]);
        }
        for (int i = 0; i < fabId.length; i++) {
            fab[i] = (FloatingActionButton) findViewById(fabId[i]);
        }
        mFab_add.setOnClickListener(this);
        setDefaultValues();
        bindEvents();
    }

    private void setDefaultValues() {
        addBillTranslate1 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.add_bill_anim);
        addBillTranslate2 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.add_bill_anim);
        addBillTranslate3 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.add_bill_anim);
    }

    private void bindEvents() {
        mFab_add.setOnClickListener(this);
        for (int i = 0; i < fabId.length; i++) {
            fab[i].setOnClickListener(this);
        }
    }

    private void hideFABMenu() {
        rlAddBill.setVisibility(View.GONE);
        mFab_add.setImageResource(R.drawable.ic_more_horiz_white_24dp);
        isAdd = false;
    }

    //管理地图的生命周期方法。
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 当不需要定位图层时关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onPause();
    }

    //浮动按钮的点击侦听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
                mFab_add.setImageResource(isAdd ? R.drawable.ic_more_horiz_white_24dp : R.drawable.ic_more_detail);
                isAdd = !isAdd;
                rlAddBill.setVisibility(isAdd ? View.VISIBLE : View.GONE);
                if (isAdd) {
                    addBillTranslate1.setTarget(ll[0]);
                    addBillTranslate1.start();
                    addBillTranslate2.setTarget(ll[1]);
                    addBillTranslate2.setStartDelay(150);
                    addBillTranslate2.start();
                    addBillTranslate3.setTarget(ll[2]);
                    addBillTranslate3.setStartDelay(250);
                    addBillTranslate3.start();
                }
                break;
            case R.id.miniFab01:
                //点击了发送
                hideFABMenu();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable("location",mCurrentLocation);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
                ToastUtil.showToast(this, mCurrentLocation.getAddress().address);
                break;
            case R.id.miniFab02:
                //点击了搜索
                hideFABMenu();
                showSearchView();
                break;
            case R.id.miniFab03:
                //点击了我
                hideFABMenu();
                showMyLocation();
                break;
            default:
                hideFABMenu();
                break;
        }

    }

    //点击搜索后，弹出搜索对话框
    private void showSearchView() {
        //方法二
        final Dialog dialog = new Dialog(this,R.style.searchdialog);
        View searchview = View.inflate(this, R.layout.view_search, null);
        dialog.setContentView(searchview);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_MENU)
                dialog.dismiss();
                return false;
            }
        });
        Window window = dialog.getWindow();
        window.setGravity(Gravity.TOP);
        dialog.show();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchview,InputMethodManager.SHOW_FORCED);
        SearchView map_search = (SearchView) searchview.findViewById(R.id.map_search);
        map_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(TextUtils.isEmpty(query)){
                }else{
                    //// TODO: 2017/12/8 定位操作
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    //点击 我 后 重新显示我的位置
    private void showMyLocation() {
        mMapStatusUpdate = MapStatusUpdateFactory.zoomTo(16);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        mBaiduMap.setMyLocationConfiguration(mConfig);

        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        mMapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        private boolean isFirstIn = true;


        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            mCurrentLocation = location;
            mBaiduMap = mMapView.getMap();
            mMapStatusUpdate = MapStatusUpdateFactory.zoomTo(16);
            mBaiduMap.setMapStatus(mMapStatusUpdate);

            mBaiduMap.setMyLocationEnabled(true);
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(0).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);
            // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
            mCurrentMarker = BitmapDescriptorFactory
                    .fromResource(R.drawable.ic_location_on_purple_800_18dp);
            mConfig = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker);
            mBaiduMap.setMyLocationConfiguration(mConfig);

            if (isFirstIn) {
                showMyLocation();
                isFirstIn = false;
            }
        }
    }

}

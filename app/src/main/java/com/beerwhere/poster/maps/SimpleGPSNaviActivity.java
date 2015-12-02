package com.beerwhere.poster.maps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.beerwhere.poster.ItemDetail;
import com.beerwhere.poster.utils.AMapUtil;
import com.beerwhere.poster.utils.TTSController;
import com.beerwhere.poster.utils.ToastUtil;
import com.beerwhere.poster.utils.Utils;

/**
 * 
 * 实时导航界面
 * */
public class SimpleGPSNaviActivity extends Activity implements AMapNaviListener,AMapLocationListener,Runnable {

	//起点终点
	private NaviLatLng mNaviStart = null;
	private NaviLatLng mNaviEnd = new NaviLatLng(24.568535, 118.261575);
	
	private ProgressDialog mRouteCalculatorProgressDialog;// 路径规划过程显示状态

	private LocationManagerProxy aMapLocManager = null;
	//	private TextView myLocation;
	private AMapLocation aMapLocation;// 用于判断定位超时
	private Handler handler = new Handler();
	private AMapNavi mAMapNavi;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		aMapLocManager = LocationManagerProxy.getInstance(this);
		/*
		 * mAMapLocManager.setGpsEnable(false);//
		 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
		 * API定位采用GPS和网络混合定位方式
		 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
		 */
		aMapLocManager.requestLocationData(
				LocationProviderProxy.AMapNetwork, 2000, 10, this);
		handler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
		//语音播报开始
		TTSController.getInstance(this).startSpeaking();
		initView();
		 
	}

	private void initView() {
		mAMapNavi = AMapNavi.getInstance(this);
		mAMapNavi.setAMapNaviListener(this);

		mRouteCalculatorProgressDialog=new ProgressDialog(this);
		mRouteCalculatorProgressDialog.setCancelable(true);

		AMapNavi.getInstance(this).setAMapNaviListener(this);
	}

//---------------------导航回调--------------------
	@Override
	public void onArriveDestination() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onArrivedWayPoint(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCalculateRouteFailure(int arg0) {
		mRouteCalculatorProgressDialog.dismiss();

	}

	@Override
	public void onCalculateRouteSuccess() {
		mRouteCalculatorProgressDialog.dismiss();
		Intent intent = new Intent(SimpleGPSNaviActivity.this,
				SimpleNaviActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		Bundle bundle=new Bundle();
		bundle.putInt(Utils.ACTIVITYINDEX, Utils.SIMPLEGPSNAVI);
		bundle.putBoolean(Utils.ISEMULATOR, false);
		intent.putExtras(bundle);
		startActivity(intent);
        finish();
	}

	@Override
	public void onEndEmulatorNavi() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetNavigationText(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGpsOpenStatus(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitNaviFailure() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitNaviSuccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChange(AMapNaviLocation arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviInfoUpdated(AMapNaviInfo arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReCalculateRouteForTrafficJam() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReCalculateRouteForYaw() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartNavi(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTrafficStatusUpdate() {
		// TODO Auto-generated method stub

	}
	//---------------------导航View事件回调---------------------------实现AMapNaviViewListener接口的方法

	/**
	 *
	 * 覆盖AMapNaviViewListener接口的方法
	 */

	//返回键处理事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(SimpleGPSNaviActivity.this,
					ItemDetail.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
		 
			finish();
		}
		return super.onKeyDown(keyCode, event);
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy(); 
	  // 删除导航监听
		 
		AMapNavi.getInstance(this).removeAMapNaviListener(this);
	}


	@Override
	public void onNaviInfoUpdate(NaviInfo arg0) {
		  
		// TODO Auto-generated method stub  
		
	}


	@Override
	public void onLocationChanged(AMapLocation aMapLocation) {
		if (aMapLocation != null) {
			this.aMapLocation = aMapLocation;// 判断超时机制
			Double geoLat = aMapLocation.getLatitude();
			Double geoLng = aMapLocation.getLongitude();
			mNaviStart= new NaviLatLng(geoLat, geoLng);
			mAMapNavi.calculateWalkRoute(mNaviStart, mNaviEnd);

			String cityCode = "";
			String desc = "";
			Bundle locBundle = aMapLocation.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");
				desc = locBundle.getString("desc");
			}
			String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
					+ "\n精    度    :" + aMapLocation.getAccuracy() + "米"
					+ "\n定位方式:" + aMapLocation.getProvider() + "\n定位时间:"
					+ AMapUtil.convertToTime(aMapLocation.getTime()) + "\n城市编码:"
					+ cityCode + "\n位置描述:" + desc + "\n省:"
					+ aMapLocation.getProvince() + "\n市:" + aMapLocation.getCity()
					+ "\n区(县):" + aMapLocation.getDistrict() + "\n区域编码:" + aMapLocation
					.getAdCode());
			Log.w("======", str);
		}
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	protected void onPause() {
		super.onPause();
		stopLocation();// 停止定位
	}
	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		if (aMapLocManager != null) {
			aMapLocManager.removeUpdates(this);
			aMapLocManager.destroy();
		}
		aMapLocManager = null;
	}
	@Override
	public void run() {
		if (aMapLocation == null) {
			ToastUtil.show(this, "12秒内还没有定位成功，停止定位");
//			myLocation.setText("12秒内还没有定位成功，停止定位");
			stopLocation();// 销毁掉定位
		}
	}
}

package com.qiushengbao.sms.sms_action;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;


public class FindLocation {
	// amap
	private LocationManagerProxy aMapManager;
	public String locationString = null;
	public static String locationStringLatitude = null;
	public static String locationStringLongitude = null;
	public String locationStringAddress = null;
	public String locationStringAccuracy = null;

	Context context = null;
	public FindLocation(Context context){
		this.context = context;
//		Toast.makeText(context,"呵---------------------------------------------------呵", Toast.LENGTH_LONG).show();
		startAmap();
	}

	@SuppressWarnings("deprecation")
	private void startAmap() {

		aMapManager = LocationManagerProxy.getInstance(context);
		/*
		 * mAMapLocManager.setGpsEnable(false);
		 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
		 * API定位采用GPS和网络混合定位方式
		 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
		 */
		aMapManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork,
				2000, 10, mAMapLocationListener);
	}

	// 创建位置监听器
	private AMapLocationListener mAMapLocationListener = new AMapLocationListener() {

		// @Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		// @Override
		public void onProviderEnabled(String provider) {

		}

		// @Override
		public void onProviderDisabled(String provider) {

		}

		// @Override
		public void onLocationChanged(Location location) {

		}

		@Override
		public void onLocationChanged(AMapLocation location) {
			if (location != null) {
				Double geoLat = location.getLatitude();
				Double geoLng = location.getLongitude();
				String cityCode = "";
				String desc = "";
				Bundle locBundle = location.getExtras();
				if (locBundle != null) {
					cityCode = locBundle.getString("citycode");
					desc = locBundle.getString("desc");
				}
				StringBuilder str = new StringBuilder();
				str.append("http://m.amap.com/?q=");
				str.append(location.getLatitude());// location.getLatitude()
				str.append(",");
				str.append(location.getLongitude());// location.getLongitude()
				str.append("&name=help&src=24h");
				locationStringLatitude = ""+location.getLatitude();
				locationStringLongitude = ""+location.getLongitude();
				locationStringAddress = ""+location.getAddress();
				locationStringAccuracy = ""+location.getAccuracy();
				// str.append("&title=location&content=help!!!&output=html&src=24h");

				// str.append("&coord_type=gcj02&output=html&src");
				// String str1 =
				locationString = str.toString();

				// mTextView.setText("哈哈哈哈哈哈哈哈哈哈\n" + str);
			}
		}
	};


}

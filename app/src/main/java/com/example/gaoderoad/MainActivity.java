package com.example.gaoderoad;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.overlay.BusRouteOverlay;
import com.amap.api.maps2d.overlay.DrivingRouteOverlay;
import com.amap.api.maps2d.overlay.WalkRouteOverlay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.TMC;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import java.util.List;


public class MainActivity extends AppCompatActivity implements RouteSearch.OnRouteSearchListener{
    private MapView mapView;
    private AMap aMap;
    private LatLonPoint startPoint = new LatLonPoint(39.742295, 116.235891);
    private LatLonPoint endPoint = new LatLonPoint(39.995576, 116.481288);
    private RouteSearch routeSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState); //此方法必须重写
        init();
        addMarkers();
    }


    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
    }

    /**
     * 添加标记.
     */
    private void addMarkers() {
        LatLng start = new LatLng(startPoint.getLatitude(), startPoint.getLongitude());
        aMap.addMarker(new MarkerOptions()
                .position(start)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
        LatLng end = new LatLng(endPoint.getLatitude(), endPoint.getLongitude());
        aMap.addMarker(new MarkerOptions()
                .position(end)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
    }

    /**
     * 驾车模式.
     * @param view view
     */
    public void driveMode(View view) {
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                startPoint, endPoint);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(
                fromAndTo, //路径规划的起点和终点
                RouteSearch.DrivingDefault, //驾车模式
                null, //途经点
                null, //示避让区域
                "" //避让道路
        );
        routeSearch.calculateDriveRouteAsyn(query);
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }


    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }


    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    DrivePath drivePath = result.getPaths().get(0);


                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            MainActivity.this, aMap, drivePath,
                            result.getStartPos(),
                            result.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                } else {
                    Toast.makeText(MainActivity.this, "对不起，没有搜索到相关数据",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "对不起，没有搜索到相关数据",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "onDriveRouteSearched error.[" + errorCode + "]",
                    Toast.LENGTH_SHORT).show();
        }

        List<DrivePath> drivePathList = result.getPaths();
        DrivePath drivePath = drivePathList.get(0);
        List<DriveStep> steps = drivePath.getSteps();
        for (DriveStep step : steps) {
            List<LatLonPoint> polyline = step.getPolyline();
            List<TMC> tmcList = step.getTMCs();
            for(TMC tmc : tmcList) {
                String status = tmc.getStatus();
                List<LatLonPoint> polyline1 = tmc.getPolyline();
            }
        }

    }




    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


}

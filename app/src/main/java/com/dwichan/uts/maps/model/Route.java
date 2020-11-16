package com.dwichan.uts.maps.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public LatLng startLocation;
    public String startAddress;
    public List<LatLng> points;
}

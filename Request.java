package com.example.bader.qattah;

import android.support.annotation.NonNull;

import com.firebase.geofire.GeoLocation;

/**
 * Created by Abdurrahmane on 3/24/2018.
 */

public class Request implements Comparable {

    GeoLocation geoLocation;
    String key;
    Double distance;
    String pickAddress, dAddress;

    public Request(GeoLocation geoLocation, String key, Double distance, String pickaddress) {
        this.geoLocation = geoLocation;
        this.key = key;
        this.distance = distance;
        this.pickAddress = pickaddress;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        double distance = ((Request)o).distance;
        return (int) Math.round(((Request)this).distance - distance);
    }

    public void setdAddress(String dAddress){
        this.dAddress = dAddress;
    }
}

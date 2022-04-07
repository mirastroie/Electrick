package com.example.electrick;

import android.content.Context;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;


public class MarkerClusterRenderer extends DefaultClusterRenderer<EV> {


    public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<EV> clusterManager) {
        super(context, map, clusterManager);

    }

    @Override
    protected void onBeforeClusterItemRendered(EV item, MarkerOptions markerOptions) { // 5

        markerOptions.icon(BitmapDescriptorFactory.fromAsset("marker.bmp"));  // 8
        markerOptions.title(item.getTitle());
    }


    @Override
    protected boolean shouldRenderAsCluster(Cluster<EV> cluster){
        return cluster.getSize() > 1;
    }
}

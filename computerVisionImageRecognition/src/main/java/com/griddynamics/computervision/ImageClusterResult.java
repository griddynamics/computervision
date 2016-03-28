package com.griddynamics.computervision;

import org.opencv.core.Mat;

import java.util.Map;

/**
 * Created by npakhomova on 3/28/16.
 */
public class ImageClusterResult {
    Mat center;
    Mat cluster;
    private Map<Integer, Integer> counts;


    public ImageClusterResult(Mat center, Mat cluster, Map<Integer, Integer> counts) {
        this.center = center;
        this.cluster = cluster;
        this.counts = counts;
    }

    public Mat getCenter() {
        return center;
    }

    public Mat getCluster() {
        return cluster;
    }

    public Map<Integer, Integer> getCounts() {
        return counts;
    }
}

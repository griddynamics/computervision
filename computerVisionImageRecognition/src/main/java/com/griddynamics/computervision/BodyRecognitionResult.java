package com.griddynamics.computervision;

import org.opencv.core.Mat;

import java.util.List;
/**
 * Created by npakhomova on 4/13/16.
 */
public class BodyRecognitionResult {

    private final BodyParts bodyPart;
    private final List<Mat> partMats;
    private final Mat boundedPart;
    private String nameOfCas;

    public BodyRecognitionResult(BodyParts bodyPart, List<Mat> partMats, Mat boundedPart) {
        this.boundedPart = boundedPart;
        this.bodyPart = bodyPart;
        this.partMats = partMats;
    }

    public BodyRecognitionResult(BodyParts bodyPart, List<Mat> partMats, Mat boundedPart, String nameOfCas) {
        this.boundedPart = boundedPart;
        this.bodyPart = bodyPart;
        this.partMats = partMats;
        this.nameOfCas = nameOfCas;
    }

    public Mat getBoundedPart() {
        return boundedPart;
    }

    public List<Mat> getPartMat() {
        return partMats;
    }

    public BodyParts getBodyPart() {
        return bodyPart;
    }

    public String getNameOfCas() {
        return nameOfCas;
    }
}

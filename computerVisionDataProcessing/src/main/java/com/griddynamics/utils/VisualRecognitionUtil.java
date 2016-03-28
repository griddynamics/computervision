package com.griddynamics.utils;

import com.griddynamics.SqlQueryDataCollectionJob;
import com.griddynamics.computervision.ColorDescription;
import com.griddynamics.computervision.Colors;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by npakhomova on 3/28/16.
 */
public class VisualRecognitionUtil {

    //very naive algorithm to calculate status
    public static Integer evaluateRecognitionResult(String colorNormal, TreeSet<ColorDescription> computerVisionResult) {
        // edge case
        if (colorNormal == null) return -1;

        // process multy color
        if (colorNormal.equals("Multi")) {
            boolean isMulti = true;
            for (ColorDescription colorDesc: computerVisionResult){
//                // todo calculate it in more appropriate way
                isMulti = isMulti && ( colorDesc.getPercent() <= 34);
            }
            if (isMulti) {
                return 0;
            } else {
                return 1;
            }
        }


        for (ColorDescription description : computerVisionResult) {
            description.setDistanceFromColorNormal(Colors.COLORS_PALETTE.get(colorNormal.toLowerCase()));

        }

        Iterator<ColorDescription> colorIterator = computerVisionResult.iterator();
        ColorDescription dominantColor = colorIterator.next();
        if (dominantColor.getDistanceFromColorNormal() < SqlQueryDataCollectionJob.COLOR_SIMILARITY_TRESHOLD){
            return 0;
        }
        //check next color
        while (colorIterator.hasNext()){
            ColorDescription nextColor = colorIterator.next();
            if(nextColor.getPercent() >= 34 && nextColor.getDistanceFromColorNormal() < SqlQueryDataCollectionJob.COLOR_SIMILARITY_TRESHOLD){
                return 0;
            } if (10 <= nextColor.getPercent() && nextColor.getPercent() < 34 && nextColor.getDistanceFromColorNormal() < SqlQueryDataCollectionJob.COLOR_SIMILARITY_TRESHOLD){
                return 2;
            }

        }
        return 3;
    }
}

package gis.rahul.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul on 02-01-2018.
 */

public class AllFeatures {

    private static List<String> allFeatures = null;

    private static String[] features = {
            "House",
            "Tree"
    };

    public static List<String> getAllFeatures() {
        if (allFeatures == null) {
            allFeatures = new ArrayList<>();
            for (String feature : features) {
                allFeatures.add(feature);
            }
        }
        return allFeatures;
    }

}

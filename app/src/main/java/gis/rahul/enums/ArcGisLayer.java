package gis.rahul.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul on 02-01-2018.
 */

public enum ArcGisLayer {
    HOUSE_LAYER(0,"house"),
    TREE_LAYER(1,"tree"),
    REGION_LAYER(2,"madhapur"),
    STATE_LAYER(3,"state"),
    CITY_LAYER(4, "hyderabad");

    private int value;
    private String desc;

    private ArcGisLayer(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public ArcGisLayer arcValue(String value) {
        ArcGisLayer arcValue = null;
        for (ArcGisLayer val : ArcGisLayer.values()) {
            if (val.name().equals(value)) {
                arcValue = val;
                break;
            }
        }
        return arcValue;
    }

    public static List<String> getLayerDesc() {
        List<String> list = new ArrayList();
        for (ArcGisLayer val : ArcGisLayer.values()) {
            list.add(val.getDesc().toLowerCase());
        }
        return list;
    }

    public int getValue() {
        return value;
    }
    public String getDesc() {
        return desc;
    }

}

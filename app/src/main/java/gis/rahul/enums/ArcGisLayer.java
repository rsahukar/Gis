package gis.rahul.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul on 02-01-2018.
 */

public enum ArcGisLayer {
    STATE_LAYER(0,"state"),
    CITY_LAYER(1, "hyderabad"),
    REGION_LAYER(2,"madhapur"),
    HOUSE_LAYER(3,"house"),
    TREE_LAYER(4,"tree");

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

    public static ArcGisLayer getByDesc(String desc){
        ArcGisLayer layer = null;

        for(ArcGisLayer val : ArcGisLayer.values()) {
            if(val.getDesc().equals(desc)){
                layer = val;
                break;
            }
        }
        return layer;
    }

    public int getValue() {
        return value;
    }
    public String getDesc() {
        return desc;
    }

}

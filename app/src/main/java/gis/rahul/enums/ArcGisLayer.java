package gis.rahul.enums;

/**
 * Created by Rahul on 02-01-2018.
 */

public enum ArcGisLayer {
    HOUSE_LAYER(0),
    REGION_LAYER(1),
    STATE_LAYER(2),
    CITY_LAYER(3);

    private int value;

    private ArcGisLayer(int value) {
        this.value = value;
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

    public int getValue() {
        return value;
    }


}

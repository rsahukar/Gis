package gis.rahul.utils;

/**
 * Created by Rahul on 02-01-2018.
 */

public enum Action {

    ADD(0),
    VIEW(1),
    EDIT(2),
    DELETE(3);

    Action(int val) {
        this.value = val;
    }

    private int value;

    public int getValue(){
        return value;
    }

    public static Action valueOf(int v){
        Action action = null;
        for(Action a: Action.values()){
            if(v==a.getValue()){
                action = a;
            }
        }
        return action;
    }
}

package gis.rahul.fragment;

/**
 * Created by Rahul on 05-01-2018.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

import gis.rahul.enums.ArcGisLayer;

public class LayerSelectFragment extends DialogFragment {
    List<String> selectedLayers = new ArrayList<>();
    LayerSelectInterface dataPasser;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (LayerSelectInterface) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String[] items = {"House", "Tree", "Madhapur", "State", "Hyderabad"};


        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        builder.setTitle("Select")
                .setMultiChoiceItems(items, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog, int item, boolean isChecked) {
                                if (item == 0) {
                                    if (isChecked) selectedLayers.add(ArcGisLayer.HOUSE_LAYER.getDesc());
                                    else selectedLayers.remove(ArcGisLayer.HOUSE_LAYER.getDesc());
                                } else if (item == 1) {
                                    if (isChecked) selectedLayers.add(ArcGisLayer.TREE_LAYER.getDesc());
                                    else selectedLayers.remove(ArcGisLayer.TREE_LAYER.getDesc());
                                } else if (item == 2) {
                                    if (isChecked) selectedLayers.add(ArcGisLayer.REGION_LAYER.getDesc());
                                    else selectedLayers.remove(ArcGisLayer.REGION_LAYER.getDesc());
                                } else if (item == 3) {
                                    if (isChecked) selectedLayers.add(ArcGisLayer.STATE_LAYER.getDesc());
                                    else selectedLayers.remove(ArcGisLayer.STATE_LAYER.getDesc());
                                } else if (item == 4) {
                                    if (isChecked) selectedLayers.add(ArcGisLayer.CITY_LAYER.getDesc());
                                    else selectedLayers.remove(ArcGisLayer.CITY_LAYER.getDesc());
                                }

                            }
                        });

        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataPasser.onDataPass(selectedLayers);
                    }
                });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        return builder.create();
    }

    public interface LayerSelectInterface {
        public void onDataPass(List<String> layer);
    }
}
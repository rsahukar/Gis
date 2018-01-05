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

import gis.rahul.enums.ArcGisLayer;

public class FeatureSelectFragment extends DialogFragment {
    ArcGisLayer feature = null;
    FeatureSelectInterface dataPasser;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (FeatureSelectInterface) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String[] items = {ArcGisLayer.HOUSE_LAYER.getDesc(), ArcGisLayer.TREE_LAYER.getDesc()};

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        builder.setTitle("Select")
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String item = items[i];
                        feature = ArcGisLayer.getByDesc(item);
                    }
                });

        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataPasser.onDataPass(feature);
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

    public interface FeatureSelectInterface {
        void onDataPass(ArcGisLayer layer);
    }
}
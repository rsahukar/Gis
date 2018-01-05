package gis.rahul.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.esri.android.map.FeatureLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.ags.FeatureServiceInfo;
import com.esri.core.geodatabase.Geodatabase;
import com.esri.core.geodatabase.GeodatabaseFeature;
import com.esri.core.geodatabase.GeodatabaseFeatureTable;
import com.esri.core.geodatabase.GeodatabaseFeatureTableEditErrors;
import com.esri.core.geometry.Point;
import com.esri.core.io.UserCredentials;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.Feature;
import com.esri.core.table.TableException;
import com.esri.core.tasks.geodatabase.GenerateGeodatabaseParameters;
import com.esri.core.tasks.geodatabase.GeodatabaseStatusCallback;
import com.esri.core.tasks.geodatabase.GeodatabaseStatusInfo;
import com.esri.core.tasks.geodatabase.GeodatabaseSyncTask;
import com.esri.core.tasks.geodatabase.SyncGeodatabaseParameters;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import gis.rahul.com.gis.R;
import gis.rahul.enums.ArcGisLayer;
import gis.rahul.feature.House;
import gis.rahul.feature.Tree;
import gis.rahul.fragment.FeatureSelectFragment;
import gis.rahul.fragment.LayerSelectFragment;
import gis.rahul.utils.Action;
import gis.rahul.utils.IntentCode;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;


public class MainActivity extends AppCompatActivity implements LayerSelectFragment.LayerSelectInterface, FeatureSelectFragment.FeatureSelectInterface {

    private MapView mapView;
    private Geodatabase geodatabase = null;
    private static GeodatabaseSyncTask gdbSyncTask = null;

    private boolean addFeature = false;

    private int PERMISSION_REQUEST_CODE = 1;

    private String default_latitude = null;
    private String default_longitude = null;

    private String service_url = null;
    private String geodatabase_file = null;
    private String geodatabase_directory = null;
    private String geodatabase_path = null;

    private ArcGisLayer arcGisLayer = null;

    private Point touchedPoint = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        geodatabase_file = getString(R.string.geodatabase_file);
        geodatabase_directory = getString(R.string.geodatabase_directory);
        service_url = getString(R.string.featureservice_url);
        default_latitude = getString(R.string.default_latitude);
        default_longitude = getString(R.string.default_longitude);

        mapView = findViewById(R.id.mapview);
        //mapView.zoomToResolution(new Point(Double.parseDouble(default_latitude),Double.parseDouble(default_longitude)), 2);
        //mapView.centerAt(Double.parseDouble(default_latitude), Double.parseDouble(default_longitude), true);

        if (!checkPermission()) {
            requestPermission();
        }

        ArcGISTiledMapServiceLayer gis = new ArcGISTiledMapServiceLayer("http://server.arcgisonline.com/arcgis/rest/services/World_Topo_Map/MapServer");
        mapView.addLayer(gis);

        geodatabase_path = Environment.getExternalStorageDirectory().getPath() + "/" + geodatabase_directory + "/" + geodatabase_file;
        File file = new File(geodatabase_path);
        if (!file.exists()) {
            downloadData();
        } else {
            updateFeatureLayer(geodatabase_path);
        }

        mapView.setOnSingleTapListener(new MyOnSingleTapListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);

        return true;
    }

    @Override
    public void onDataPass(List<String> layers) {
        for (int i = 1; i < mapView.getLayers().length; i++) {
            mapView.getLayers()[i].setVisible(false);
        }
        for (int i = 1; i < mapView.getLayers().length; i++) {
            if (layers.contains(mapView.getLayers()[i].getName())) {
                mapView.getLayers()[i].setVisible(true);
            }
        }
    }

    @Override
    public void onDataPass(ArcGisLayer layer) {
        arcGisLayer = layer;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addfeature:
                addFeature = true;
                FragmentManager fragmentManager = getSupportFragmentManager();
                FeatureSelectFragment dialog1 = new FeatureSelectFragment();
                dialog1.show(fragmentManager, "FEATURE_SELECT");
                break;
            case R.id.filterfeature:
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                LayerSelectFragment dialog = new LayerSelectFragment();
                dialog.show(fragmentManager1, "LAYER_SELECT");
                break;
            case R.id.sync:
                try {
                    Toast.makeText(getApplicationContext(), "Syncing...", Toast.LENGTH_LONG).show();
                    syncOfflineEditsToServer();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

        return true;
    }

    private void syncOfflineEditsToServer() throws Exception {

        final SyncGeodatabaseParameters syncParams = geodatabase.getSyncParameters();

        GeodatabaseStatusCallback statusCallback = new GeodatabaseStatusCallback() {

            @Override
            public void statusUpdated(GeodatabaseStatusInfo status) {
            }
        };

        CallbackListener<Map<Integer, GeodatabaseFeatureTableEditErrors>> syncResponseCallback = new CallbackListener<Map<Integer, GeodatabaseFeatureTableEditErrors>>() {


            @Override
            public void onCallback(Map<Integer, GeodatabaseFeatureTableEditErrors> errorsMap) {
                if(errorsMap==null || errorsMap.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Successfully synced with the server", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Sync Error",Toast.LENGTH_LONG).show();
            }
        };

        if (gdbSyncTask == null) {
            UserCredentials uc = new UserCredentials();
            uc.setUserAccount("sahukarrahul", "Domination7");
            gdbSyncTask = new GeodatabaseSyncTask(service_url, uc);
        }
        gdbSyncTask.syncGeodatabase(syncParams, geodatabase, statusCallback, syncResponseCallback);
    }


    private class MyOnSingleTapListener implements OnSingleTapListener {

        @Override
        public void onSingleTap(float x, float y) {
            Feature selectedGraphic = null;
            GeodatabaseFeatureTable selectedLayer = null;
            touchedPoint = mapView.toMapPoint(x, y);

            if (!addFeature) {
                if (touchedPoint != null) {

                    for (Layer layer : mapView.getLayers()) {
                        if (layer == null)
                            continue;

                        if (layer instanceof FeatureLayer) {
                            GeodatabaseFeatureTable fLayer = (GeodatabaseFeatureTable) ((FeatureLayer) layer)
                                    .getFeatureTable();

                            selectedGraphic = GetFeature(
                                    (FeatureLayer) layer, x, y);

                            if (selectedGraphic != null
                                    && selectedGraphic.getAttributes() != null) {
                                int layerId = fLayer.getLayerServiceInfo()
                                        .getId();
                                ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                                if(layerId == ArcGisLayer.HOUSE_LAYER.getValue()) {
                                    House house = objectMapper.convertValue(selectedGraphic.getAttributes(), House.class);

                                    Intent intent = new Intent(MainActivity.this, HouseActivity.class);
                                    intent.putExtra("house", house);
                                    intent.putExtra("FEATUREID", Long.parseLong(selectedGraphic.getAttributeValue("OBJECTID").toString()));
                                    startActivityForResult(intent, IntentCode.VIEW_HOUSE);
                                }
                                else if(layerId == ArcGisLayer.TREE_LAYER.getValue()){
                                    Tree tree = objectMapper.convertValue(selectedGraphic.getAttributes(), Tree.class);
                                    System.out.println();

                                    Intent intent = new Intent(MainActivity.this, TreeActivity.class);
                                    intent.putExtra("tree", tree);
                                    intent.putExtra("FEATUREID", Long.parseLong(selectedGraphic.getAttributeValue("OBJECTID").toString()));
                                    startActivityForResult(intent, IntentCode.VIEW_TREE);
                                }
                            }
                        }
                    }
                }
            } else {

                if (arcGisLayer == ArcGisLayer.HOUSE_LAYER) {
                    Intent intent = new Intent(MainActivity.this, HouseActivity.class);
                    intent.putExtra("ACTION", Action.ADD.getValue());
                    startActivityForResult(intent, IntentCode.ADD_HOUSE);
                } else if (arcGisLayer == ArcGisLayer.TREE_LAYER) {
                    Intent intent = new Intent(MainActivity.this, TreeActivity.class);
                    intent.putExtra("ACTION", Action.ADD.getValue());
                    startActivityForResult(intent, IntentCode.ADD_TREE);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        House house = null;
        Tree tree = null;
        int layerId = 0;
        GeodatabaseFeatureTable layer = null;

        if (data != null) {
            Action action = Action.valueOf(data.getIntExtra("ACTION", Action.VIEW.getValue()));

            if (requestCode == IntentCode.ADD_HOUSE || requestCode == IntentCode.ADD_TREE) {
                if (requestCode == IntentCode.ADD_HOUSE) {
                    house = (House) data.getSerializableExtra("HOUSE");
                    layerId = ArcGisLayer.HOUSE_LAYER.getValue();
                } else if (requestCode == IntentCode.ADD_TREE) {
                    tree = (Tree) data.getSerializableExtra("TREE");
                    layerId = ArcGisLayer.TREE_LAYER.getValue();
                }
                layer = geodatabase
                        .getGeodatabaseFeatureTableByLayerId(layerId);
                if (layer != null) {
                    GeodatabaseFeature gdbFeature;
                    try {
                        Map<String, Object> attributes = null;

                        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        if (requestCode == IntentCode.ADD_HOUSE) {
                            attributes = objectMapper.convertValue(house, new TypeReference<Map<String, Object>>() {
                            });
                        } else if (requestCode == IntentCode.ADD_TREE) {
                            attributes = objectMapper.convertValue(tree, new TypeReference<Map<String, Object>>() {
                            });
                        }
                        gdbFeature = new GeodatabaseFeature(attributes, touchedPoint, layer);
                        layer.addFeature(gdbFeature);
                        Toast.makeText(getApplicationContext(), "Feature added", Toast.LENGTH_SHORT).show();
                    } catch (TableException e) {
                        e.printStackTrace();
                    }
                }
                addFeature = false;
            } else if (requestCode == IntentCode.VIEW_HOUSE || requestCode == IntentCode.VIEW_TREE) {
                if (action == Action.DELETE) {
                    Long featureId = data.getLongExtra("FEATUREID", 0);
                    if (requestCode == IntentCode.VIEW_HOUSE) {
                        layerId = ArcGisLayer.HOUSE_LAYER.getValue();
                    } else {
                        layerId = ArcGisLayer.TREE_LAYER.getValue();
                    }
                    layer = geodatabase
                            .getGeodatabaseFeatureTableByLayerId(layerId);
                    if (layer != null) {
                        try {
                            layer.deleteFeature(featureId);
                            Toast.makeText(getApplicationContext(), "Feature Deleted!!!", Toast.LENGTH_SHORT).show();
                        } catch (TableException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (action == Action.EDIT) {
                    Long featureid = data.getLongExtra("FEATUREID", 0);
                    if (requestCode == IntentCode.VIEW_HOUSE) {
                        house = (House) data.getSerializableExtra("HOUSE");
                        layerId = ArcGisLayer.HOUSE_LAYER.getValue();
                    } else if (requestCode == IntentCode.VIEW_TREE) {
                        tree = (Tree) data.getSerializableExtra("TREE");
                        layerId = ArcGisLayer.TREE_LAYER.getValue();
                    }
                    layer = geodatabase
                            .getGeodatabaseFeatureTableByLayerId(layerId);
                    if (layer != null) {
                        GeodatabaseFeature gdbFeature;
                        try {
                            Map<String, Object> attributes = null;

                            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                            if (requestCode == IntentCode.VIEW_HOUSE) {
                                attributes = objectMapper.convertValue(house, new TypeReference<Map<String, Object>>() {
                                });
                            } else if (requestCode == IntentCode.VIEW_TREE) {
                                attributes = objectMapper.convertValue(tree, new TypeReference<Map<String, Object>>() {
                                });
                            }
                            gdbFeature = new GeodatabaseFeature(attributes, touchedPoint, layer);
                            layer.updateFeature(featureid, gdbFeature);
                            Toast.makeText(getApplicationContext(), "Feature updated", Toast.LENGTH_SHORT).show();
                            addFeature = false;
                        } catch (TableException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        else {
            if(addFeature==true){
                addFeature = false;
            }
        }
    }

    private Feature GetFeature(FeatureLayer fLayer, float x, float y) {

        // Get the graphics near the Point.
        long[] ids = fLayer.getFeatureIDs(x, y, 10, 1);
        if (ids == null || ids.length == 0) {
            return null;
        }
        return fLayer.getFeature(ids[0]);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void downloadData() {

        UserCredentials uc = new UserCredentials();
        uc.setUserAccount("sahukarrahul", "Domination7");
        gdbSyncTask = new GeodatabaseSyncTask(service_url, uc);
        gdbSyncTask.fetchFeatureServiceInfo(new CallbackListener<FeatureServiceInfo>() {

            @Override
            public void onError(Throwable arg0) {
                Log.e(TAG, "Error fetching FeatureServiceInfo");
            }

            @Override
            public void onCallback(FeatureServiceInfo fsInfo) {
                //if (fsInfo.isSyncEnabled()) {
                createGeodatabase(fsInfo);
                //}
            }
        });

    }

    private void createGeodatabase(FeatureServiceInfo featureServerInfo) {
        // set up the parameters to generate a geodatabase
        GenerateGeodatabaseParameters params = new GenerateGeodatabaseParameters(
                featureServerInfo, mapView.getExtent(),
                mapView.getSpatialReference());

        // a callback which fires when the task has completed or failed.
        CallbackListener<String> gdbResponseCallback = new CallbackListener<String>() {
            @Override
            public void onError(final Throwable e) {
                Log.e(TAG, "Error creating geodatabase");
            }

            @Override
            public void onCallback(String path) {
                Log.i(TAG, "Geodatabase is: " + path);
                updateFeatureLayer(path);
                // log the path to the data on device
                Log.i(TAG, "path to geodatabase: " + path);
            }
        };

        // a callback which updates when the status of the task changes
        GeodatabaseStatusCallback statusCallback = new GeodatabaseStatusCallback() {
            @Override
            public void statusUpdated(final GeodatabaseStatusInfo status) {

                int x = 10;
            }
        };

        submitTask(params, geodatabase_path, statusCallback, gdbResponseCallback);
    }

    /**
     * Request database, poll server to get status, and download the file
     */
    private static void submitTask(GenerateGeodatabaseParameters params,
                                   String file, GeodatabaseStatusCallback statusCallback,
                                   CallbackListener<String> gdbResponseCallback) {
        // submit task
        gdbSyncTask.generateGeodatabase(params, file, false, statusCallback,
                gdbResponseCallback);
    }

    private void updateFeatureLayer(String featureLayerPath) {
        // create a new geodatabase
        try {
            geodatabase = new Geodatabase(featureLayerPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (geodatabase != null) {
            for (GeodatabaseFeatureTable gdbFeatureTable : geodatabase
                    .getGeodatabaseTables()) {
                if (gdbFeatureTable.hasGeometry()) {
                    FeatureLayer fl = new FeatureLayer(gdbFeatureTable);
                    fl.setVisible(true);
                    mapView.addLayer(fl);
                }
            }
        }
    }
}
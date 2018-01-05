package gis.rahul.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import gis.rahul.com.gis.R;
import gis.rahul.feature.Tree;
import gis.rahul.utils.Action;

public class TreeActivity extends AppCompatActivity {

    private boolean editing = false;

    private Menu activityMenu;

    private MenuItem editFeature;
    private MenuItem saveFeature;
    private MenuItem deleteFeature;
    private MenuItem closeEdit;

    private EditText treeName;
    private EditText treeType;
    private EditText latitude;
    private EditText longitude;
    private EditText street;
    private EditText city;
    private EditText state;
    private EditText country;
    private EditText zipcode;

    private Action action;

    private Tree tree;
    private Long featureId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tree);

        initViews();

        action = Action.valueOf(getIntent().getIntExtra("ACTION", Action.VIEW.getValue()));
        if (action == Action.ADD) {
            tree = new Tree();
            populateActivity(tree);
        } else if (action == Action.VIEW) {
            tree = (Tree) getIntent().getSerializableExtra("tree");
            featureId = getIntent().getLongExtra("FEATUREID", 0);
            if (tree != null) {
                populateActivity(tree);
            }
        }

    }

    private void initViews() {
        treeName = findViewById(R.id.treename);
        treeType = findViewById(R.id.treetype);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        street = findViewById(R.id.street);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        country = findViewById(R.id.country);
        zipcode = findViewById(R.id.zipcode);
    }

    private void populateActivity(Tree tree) {
        treeName.setText(tree.getName());
        treeType.setText(tree.getType());
        latitude.setText(String.valueOf(tree.getLatitude()));
        longitude.setText(String.valueOf(tree.getLongitude()));
        street.setText(tree.getStreet());
        city.setText(tree.getCity());
        state.setText(tree.getState());
        country.setText(tree.getCountry());
        zipcode.setText(String.valueOf(tree.getZipcode()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.housemenu, menu);

        activityMenu = menu;

        editFeature = activityMenu.findItem(R.id.editfeature);
        saveFeature = activityMenu.findItem(R.id.savefeature);
        deleteFeature = activityMenu.findItem(R.id.deletefeature);
        closeEdit = activityMenu.findItem(R.id.close);

        if (action == Action.ADD) {
            setMenuItemStatuses(true);
        } else if (action == Action.VIEW) {
            closeEdit.setVisible(false);
            saveFeature.setVisible(false);
        }

        saveFeature.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                tree.setName(treeName.getText().toString());
                tree.setType(treeType.getText().toString());
                tree.setLatitude(Float.parseFloat(latitude.getText().toString()));
                tree.setLongitude(Float.parseFloat(longitude.getText().toString()));
                tree.setStreet(street.getText().toString());
                tree.setCity(city.getText().toString());
                tree.setState(state.getText().toString());
                tree.setCountry(country.getText().toString());
                tree.setZipcode(Long.parseLong(zipcode.getText().toString()));
                Intent intent = new Intent();
                if (action == Action.ADD) {
                    intent.putExtra("ACTION", Action.ADD.getValue());
                } else {
                    intent.putExtra("FEATUREID", featureId);
                    intent.putExtra("ACTION", Action.EDIT.getValue());
                }
                intent.putExtra("TREE", tree);
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;
            }
        });

        deleteFeature.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TreeActivity.this);
                builder.setMessage("Delete this tree?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                return true;
            }
        });

        closeEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(action.equals(Action.VIEW)){
                    populateActivity(tree);
                    setMenuItemStatuses(false);
                } else if (action.equals(Action.ADD)){
                    finish();
                }
                return false;
            }
        });

        return true;
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int choice) {
            switch (choice) {
                case DialogInterface.BUTTON_POSITIVE:
                    Intent intent = new Intent();
                    intent.putExtra("ACTION", Action.DELETE.getValue());
                    intent.putExtra("FEATUREID", featureId);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editfeature:
                setMenuItemStatuses(true);
                break;
            case R.id.deletefeature:
                setMenuItemStatuses(true);
                break;
            case R.id.savefeature:
                setMenuItemStatuses(false);
                break;
            case R.id.close:
                setMenuItemStatuses(false);
                break;
            default:
                break;
        }

        return true;
    }

    private void setMenuItemStatuses(boolean status) {

        editing = status;

        editFeature.setVisible(!status);
        saveFeature.setVisible(status);
        closeEdit.setVisible(status);

        treeName.setEnabled(status);
        treeType.setEnabled(status);
        latitude.setEnabled(status);
        longitude.setEnabled(status);
        street.setEnabled(status);
        city.setEnabled(status);
        state.setEnabled(status);
        country.setEnabled(status);
        zipcode.setEnabled(status);
    }

}

package gis.rahul.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import gis.rahul.com.gis.R;
import gis.rahul.feature.House;
import gis.rahul.utils.Action;

public class HouseActivity extends AppCompatActivity {

    private boolean editing = false;

    private Menu activityMenu;

    private MenuItem editFeature;
    private MenuItem saveFeature;
    private MenuItem cancelEdit;

    private EditText latitude;
    private EditText longitude;
    private EditText address1;
    private EditText address2;
    private EditText street;
    private EditText city;
    private EditText state;
    private EditText country;
    private EditText zipcode;

    private Action action;

    private House house;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.house);

        initViews();

        action = Action.valueOf(getIntent().getIntExtra("ACTION", Action.VIEW.getValue()));
        if (action == Action.ADD) {
            house = new House();
            populateActivity(house);
        } else if (action == Action.VIEW) {
            house = (House) getIntent().getSerializableExtra("house");
            if (house != null) {
                populateActivity(house);
            }
        }

    }

    private void initViews() {
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        address1 = findViewById(R.id.addrline1);
        address2 = findViewById(R.id.addrline2);
        street = findViewById(R.id.street);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        country = findViewById(R.id.country);
        zipcode = findViewById(R.id.zipcode);
    }

    private void populateActivity(House house) {
        latitude.setText(String.valueOf(house.getLatitude()));
        longitude.setText(String.valueOf(house.getLongitude()));
        address1.setText(house.getAddress1());
        address2.setText(house.getAddress2());
        street.setText(house.getStreet());
        city.setText(house.getCity());
        state.setText(house.getState());
        country.setText(house.getCountry());
        zipcode.setText(String.valueOf(house.getZipcode()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.housemenu, menu);

        activityMenu = menu;

        editFeature = activityMenu.findItem(R.id.editfeature);
        saveFeature = activityMenu.findItem(R.id.savefeature);
        cancelEdit = activityMenu.findItem(R.id.cancel);

        if (action == Action.ADD) {
            setMenuItemStatuses(true);
        } else if (action == Action.VIEW) {
            cancelEdit.setVisible(false);
            saveFeature.setVisible(false);
        }

        saveFeature.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                house.setLatitude(Float.parseFloat(latitude.getText().toString()));
                house.setLongitude(Float.parseFloat(longitude.getText().toString()));
                house.setAddress1(address1.getText().toString());
                house.setAddress2(address2.getText().toString());
                house.setStreet(street.getText().toString());
                house.setCity(city.getText().toString());
                house.setState(state.getText().toString());
                house.setCountry(country.getText().toString());
                house.setZipcode(Long.parseLong(zipcode.getText().toString()));
                Intent intent = new Intent();
                intent.putExtra("HOUSE", house);
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editfeature:
                setMenuItemStatuses(true);
                break;
            case R.id.savefeature:
                setMenuItemStatuses(false);
                break;
            case R.id.cancel:
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
        cancelEdit.setVisible(status);

        latitude.setEnabled(status);
        longitude.setEnabled(status);
        address1.setEnabled(status);
        address2.setEnabled(status);
        street.setEnabled(status);
        city.setEnabled(status);
        state.setEnabled(status);
        country.setEnabled(status);
        zipcode.setEnabled(status);
    }

}

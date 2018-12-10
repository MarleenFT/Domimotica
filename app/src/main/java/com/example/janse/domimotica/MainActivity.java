package com.example.janse.domimotica;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Adapter;

public class MainActivity extends AppCompatActivity {
    // Create components
    private TextView home_currently_connected_name_box;
    private TextView home_currently_connected_amount_name_box;
    private TextView dashboard_name_box;
    private RadioButton dashboard_on_button;
    private RadioButton dashboard_off_button;
    private Spinner dashboard_node_select;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    homeSetup();
                    return true;
                case R.id.navigation_dashboard:
                    dashboardSetup();
                    return true;
                case R.id.navigation_config:
                    configSetup();
                    return true;
            }
            return false;
        }
    };

    private Spinner.OnItemSelectedListener spinnerListener
            = new Spinner.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String node = dashboard_node_select.getSelectedItem().toString();
            dashboard_name_box.setText(node);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init home screen components
        home_currently_connected_name_box = findViewById(
                R.id.home_currently_connected_name_box);
        home_currently_connected_amount_name_box = findViewById(
                R.id.home_currently_connected_amount_name_box);

        // Init dashboard screen components
        dashboard_name_box = findViewById(R.id.dashboard_name_box);
        dashboard_on_button = findViewById(R.id.dashboard_on_button);
        dashboard_off_button = findViewById(R.id.dashboard_off_button);
        dashboard_node_select = findViewById(R.id.dashboard_node_sellect);

        // Create the array adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dashboard_spinner_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        dashboard_node_select.setAdapter(adapter);

        // Set startup visibility values
        home_currently_connected_name_box.setVisibility(View.VISIBLE);
        home_currently_connected_amount_name_box.setVisibility(View.VISIBLE);

        dashboard_name_box.setVisibility(View.INVISIBLE);
        dashboard_on_button.setVisibility(View.INVISIBLE);
        dashboard_off_button.setVisibility(View.INVISIBLE);
        dashboard_node_select.setVisibility(View.INVISIBLE);

        // Init navigation bar
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        dashboard_node_select.setOnItemSelectedListener(spinnerListener);
    }

    // This function sets the visibility for the home screen
    private void homeSetup(){
        home_currently_connected_name_box.setVisibility(View.VISIBLE);
        home_currently_connected_amount_name_box.setVisibility(View.VISIBLE);

        dashboard_name_box.setVisibility(View.INVISIBLE);
        dashboard_on_button.setVisibility(View.INVISIBLE);
        dashboard_off_button.setVisibility(View.INVISIBLE);
        dashboard_node_select.setVisibility(View.INVISIBLE);
    }

    // This function sets the visibility for the dashboard screen
    public void dashboardSetup(){
        home_currently_connected_name_box.setVisibility(View.INVISIBLE);
        home_currently_connected_amount_name_box.setVisibility(View.INVISIBLE);

        String node = dashboard_node_select.getSelectedItem().toString();
        dashboard_name_box.setText(node);

        dashboard_name_box.setVisibility(View.VISIBLE);
        dashboard_on_button.setVisibility(View.VISIBLE);
        dashboard_off_button.setVisibility(View.VISIBLE);
        dashboard_node_select.setVisibility(View.VISIBLE);
    }

    // This function sets the visibility for the config screen
    public void configSetup(){
        home_currently_connected_name_box.setVisibility(View.INVISIBLE);
        home_currently_connected_amount_name_box.setVisibility(View.INVISIBLE);

        dashboard_name_box.setVisibility(View.INVISIBLE);
        dashboard_on_button.setVisibility(View.INVISIBLE);
        dashboard_off_button.setVisibility(View.INVISIBLE);
        dashboard_node_select.setVisibility(View.INVISIBLE);
    }
}
package com.hakkicaner.imgsearch.activities;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.hakkicaner.imgsearch.R;

public class SettingsActivity extends Activity {
    protected static final String SEARCH_SETTINGS = "SearchSettings";

    private ArrayAdapter<CharSequence> aImageSize;
    private ArrayAdapter<CharSequence> aColorFilter;
    private ArrayAdapter<CharSequence> aImageType;
    private Spinner sImageSize;
    private Spinner sColorFilter;
    private Spinner sImageType;
    private EditText etSite;
    private SharedPreferences searchSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_settings);

        searchSettings = getSharedPreferences(SEARCH_SETTINGS, MODE_PRIVATE);
        String imgsize = searchSettings.getString("imgsz", "");
        String imgcolor = searchSettings.getString("imgcolor", "");
        String imgtype = searchSettings.getString("imgtype", "");
        String site = searchSettings.getString("site", "");

        sImageSize = (Spinner) findViewById(R.id.sImageSize);
        aImageSize = ArrayAdapter.createFromResource(this,
                R.array.image_size_array, android.R.layout.simple_spinner_item);
        aImageSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sImageSize.setAdapter(aImageSize);
        sImageSize.setSelection(aImageSize.getPosition(imgsize));

        sColorFilter = (Spinner) findViewById(R.id.sColorFilter);
        aColorFilter = ArrayAdapter.createFromResource(this,
                R.array.color_filter_array, android.R.layout.simple_spinner_item);
        aColorFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sColorFilter.setAdapter(aColorFilter);
        sColorFilter.setSelection(aColorFilter.getPosition(imgcolor));

        sImageType = (Spinner) findViewById(R.id.sImageType);
        aImageType = ArrayAdapter.createFromResource(this,
                R.array.image_type_array, android.R.layout.simple_spinner_item);
        aImageType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sImageType.setAdapter(aImageType);
        sImageType.setSelection(aImageType.getPosition(imgtype));

        etSite = (EditText) findViewById(R.id.etSite);
        etSite.setText(site);
    }

    public void onSave(View v) {
        String imgsz = sImageSize.getItemAtPosition(sImageSize.getSelectedItemPosition()).toString();
        String imgcolor = sColorFilter.getItemAtPosition(sColorFilter.getSelectedItemPosition()).toString();
        String imgtype = sImageType.getItemAtPosition(sImageType.getSelectedItemPosition()).toString();
        String site = etSite.getText().toString();

        searchSettings = getSharedPreferences(SEARCH_SETTINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = searchSettings.edit();
        editor.putString("imgsz", imgsz);
        editor.putString("imgcolor", imgcolor);
        editor.putString("imgtype", imgtype);
        editor.putString("site", site);
        editor.commit();

        Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(SettingsActivity.this, SearchActivity.class);
        intent.putExtra("FilterSaved", true);
        startActivity(intent);
    }

    public void onReset(View v) {
        searchSettings = getSharedPreferences(SEARCH_SETTINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = searchSettings.edit();
        editor.putString("imgsz", "");
        editor.putString("imgcolor", "");
        editor.putString("imgtype", "");
        editor.putString("site", "");
        editor.commit();

        sImageSize.setSelection(aImageSize.getPosition(""));
        sColorFilter.setSelection(aColorFilter.getPosition(""));
        sImageType.setSelection(aImageType.getPosition(""));
        etSite.setText("");

        Toast.makeText(this, R.string.reset, Toast.LENGTH_SHORT).show();
    }

}

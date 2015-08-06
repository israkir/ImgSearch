package com.hakkicaner.imgsearch.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.hakkicaner.imgsearch.R;
import com.hakkicaner.imgsearch.adapters.ImageResultsAdapter;
import com.hakkicaner.imgsearch.listeners.EndlessScrollListener;
import com.hakkicaner.imgsearch.models.ImageResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchActivity extends ActionBarActivity {

    private EditText etQuery;
    private GridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;
    private SharedPreferences searchSettings;
    String imgsize;
    String imgcolor;
    String imgtype;
    String site;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
        imageResults = new ArrayList<ImageResult>();
        aImageResults = new ImageResultsAdapter(this, imageResults);
        gvResults.setAdapter(aImageResults);
    }

    private void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);

        setSearchSettings();

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchActivity.this, ImageDisplayActivity.class);
                ImageResult imageResult = imageResults.get(i);
                intent.putExtra("result", imageResult);
                startActivity(intent);
            }
        });
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMore(page);
            }
        });

        String lastQuery = searchSettings.getString("last_query", "");
        if (lastQuery.length() > 0) {
            etQuery.setText(lastQuery);
            search(lastQuery, 0);
        }
        Log.d("last query", lastQuery);

    }

    private void loadMore(int page) {
        String query = etQuery.getText().toString();
        search(query, page);
    }

    public void onImageSearch(View v) {
        imageResults.clear();
        String query = etQuery.getText().toString();
        SharedPreferences.Editor editor = searchSettings.edit();
        editor.putString("last_query", query);
        editor.commit();
        search(query, 0);
    }

    public void search(String query, int page) {
        if (isNetworkAvailable()) {
            setSearchSettings();
            String url = buildUrl(query, String.valueOf(page), imgsize, imgcolor, imgtype, site);
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONArray imageResultsJson;
                    try {
                        imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                        if (imageResultsJson.length() == 0) {
                            Toast.makeText(getBaseContext(), R.string.no_data, Toast.LENGTH_SHORT).show();
                        }
                        aImageResults.addAll(ImageResult.fromJsonArray(imageResultsJson));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
        }
    }

    private void setSearchSettings() {
        searchSettings = getSharedPreferences(SettingsActivity.SEARCH_SETTINGS, MODE_PRIVATE);
        imgsize = searchSettings.getString("imgsz", "");
        imgcolor = searchSettings.getString("imgcolor", "");
        imgtype = searchSettings.getString("imgtype", "");
        site = searchSettings.getString("site", "");
    }

    private String buildUrl(String query, String start, String imgsz, String imgcolor, String imgtype, String site) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("ajax.googleapis.com")
                .appendPath("ajax")
                .appendPath("services")
                .appendPath("search")
                .appendPath("images")
                .appendQueryParameter("v", "1.0")
                .appendQueryParameter("rsz", "8")
                .appendQueryParameter("q", query)
                .appendQueryParameter("start", start)
                .appendQueryParameter("imgsz", imgsz)
                .appendQueryParameter("imgcolor", imgcolor)
                .appendQueryParameter("imgtype", imgtype)
                .appendQueryParameter("as_sitesearch", site);
        Log.d("url: ", builder.build().toString());
        return builder.build().toString();
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private void openSettings() {
        Intent intent = new Intent(SearchActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

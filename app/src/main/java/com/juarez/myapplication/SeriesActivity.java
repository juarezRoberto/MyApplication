package com.juarez.myapplication;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.juarez.myapplication.adapters.SeriesAdapter;
import com.juarez.myapplication.model.Data;
import com.juarez.myapplication.model.IServices;
import com.juarez.myapplication.model.Key;
import com.juarez.myapplication.model.Series;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SeriesActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TOKEN = "token";
    public String token;
    public static String TAG = "SeriesActivity";
    private Button btnSearch;
    private EditText edtSearch;
    private String searchValue;
    RecyclerView recyclerSeries;
    private SeriesAdapter mAdapter;
    ArrayList<Series> myDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);
        showToolbar();
        btnSearch = findViewById(R.id.btnSearch);
        edtSearch = findViewById(R.id.edtSearchview);
        //recyclerview
        recyclerSeries = findViewById(R.id.recyclerSeries);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerSeries.setLayoutManager(linearLayoutManager);

        //dummy data
        /*ArrayList<Series> series = new ArrayList<>();
        series.add(new Series(new String[1],"a","a",2,"s","a","game of thrones","s","s"));
        series.add(new Series(new String[2],"s","ss",2,"we","rt","the big bang theory","saa","22"));
        SeriesAdapter madapter = new SeriesAdapter(this,series);
        recyclerSeries.setAdapter(madapter);*/




        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchValue = edtSearch.getText().toString();
                if (searchValue.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "debes ingresar algo", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "campo vacio");
                } else {
                    //web search
                    loadToken();
                    getSearchRequest();
                }
            }
        });
    }

    public void loadToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        token = sharedPreferences.getString(TOKEN, "");
        Log.e(TAG, "loadtoken: " + token);

    }

    //
    private void refreshDataSet() {

        if (recyclerSeries == null) {
            return;
        }
        if (mAdapter == null) {
            mAdapter = new SeriesAdapter(this, myDataSet);
            recyclerSeries.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void getSearchRequest() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://" + LoginActivity.endpoint + "/")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        IServices services = retrofit.create(IServices.class);
        Call<Data> call = services.getSeries(searchValue, "Bearer " + token);
        Log.e(TAG, "valor de busqueda: " + searchValue);
        Log.e(TAG, "useTokenAuth: " + token);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {

                if (response.isSuccessful()) {
                    myDataSet = new ArrayList<Series>();
                    myDataSet.clear();//limpio el Arralist
                    int i= 0;
                    Log.e(TAG, "Series Found: " + String.valueOf(response.body().getSeries()));
                    for (Series series : response.body().getSeries()){

                        Log.e(TAG, series.getIdSerie()+" nombre de la serie: " + series.getSeriesName());

                        myDataSet.add(series);
                    }
                    refreshDataSet();

                } else {
                    Log.e(TAG, "no se encontraron coincidencias con : " + searchValue);
                    Toast.makeText(getApplicationContext(), "no se encontraron coincidencias con :" + searchValue, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });


    }

    private void showToolbar() {
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle.setText("medio\nmelon");

    }
}
package com.arcias.covid_19.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arcias.covid_19.R;
import com.arcias.covid_19.model.CovidData;
import com.arcias.covid_19.reciver.CheckInternetConnection;
import com.arcias.covid_19.reciver.ConnectionApplication;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.List;

public class StateDataActivity extends AppCompatActivity implements CheckInternetConnection.ConnectionListener {
     private TextView totalCases,totalRecovered,totalDeaths,newCases,newRecovered,newDeaths,state;
     private BarChart barChart;
     private CovidData covidData;
     private String stateName;
    private String tCases,tDeaths,tRecovered,nCases,nDeaths,nRecovered;
    private PieChart pieChart;
    private LinearLayout noInternetLayout;
    private SwipeRefreshLayout refreshLayout;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_data);
        totalCases=findViewById(R.id.total_cases);
        totalRecovered=findViewById(R.id.total_recovered);
        totalDeaths=findViewById(R.id.total_deaths);
        newCases=findViewById(R.id.new_cases);
        newRecovered=findViewById(R.id.new_recovered);
        newDeaths=findViewById(R.id.new_deaths);
        barChart=findViewById(R.id.state_bar_chart);
        state=findViewById(R.id.state_name);
        pieChart=findViewById(R.id.state_pie_chart);
        noInternetLayout=findViewById(R.id.no_internet_layout);
        refreshLayout=findViewById(R.id.swipe_refresh);


//for add
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter=new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        CheckInternetConnection checkInternetConnection=new CheckInternetConnection();
        this.registerReceiver(checkInternetConnection,filter);
        ConnectionApplication.getInstance().setConnectivityListener(this);
    }
    @Override
    public void onConnectionChange(boolean isConnected) {
        if(!isConnected)
        {
            Log.d("tag","no internet");
            noInternetLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            noInternetLayout.setVisibility(View.GONE);
            Log.d("tag"," internet available");
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null)
        {
            covidData=(CovidData) bundle.getSerializable("state_name");
            tCases=covidData.getTotalCases();
            tDeaths=covidData.getTotalDeaths();
            tRecovered=covidData.getTotalRecovered();
            totalCases.setText(tCases);
            stateName=covidData.getCountry();
            state.setText(stateName);
            totalDeaths.setText(tDeaths);
            totalRecovered.setText(tRecovered);
            newDeaths.setText("not found");
            newRecovered.setText("not found");
            newCases.setText("not found");
            nCases="0";
            nDeaths="0";
            nRecovered="0";
            getStateBarData();
            getStatePieGraph();

        }
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent=getIntent();
                finish();
                startActivity(intent);
                refreshLayout.setRefreshing(false);
            }
        });
    }
    private void getStatePieGraph()
    {
        List<PieEntry> data=new ArrayList<>();
        data.add(new PieEntry(Integer.parseInt(tCases),"Total Cases"));
        data.add(new PieEntry(Integer.parseInt(tRecovered),"Total Recovered"));
        data.add(new PieEntry(Integer.parseInt(tDeaths),"Total Deaths"));

        PieDataSet pieDataSet=new PieDataSet(data,"Covid-19 Data");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(8f);

        PieData pieData=new PieData(pieDataSet);
        pieData.setValueTextSize(8f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText(stateName+" Covid-19 data");
        pieChart.animate();

    }


    private void getStateBarData()
    {
        List<BarEntry> data=new ArrayList<>();
        data.add(new BarEntry(0,Integer.parseInt(tCases)));
        data.add(new BarEntry(2,Integer.parseInt(tRecovered)));
        data.add(new BarEntry(4,Integer.parseInt(tDeaths)));
        data.add(new BarEntry(6,Integer.parseInt(nCases)));
        data.add(new BarEntry(8,Integer.parseInt(nRecovered)));
        data.add(new BarEntry(10,Integer.parseInt(nDeaths)));
        BarDataSet barDataSet=new BarDataSet(data,"Covid-19 Data");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(5f);

        BarData barData=new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText(stateName+" Covid Data");
        barChart.animateY(2000);
        XAxis axis=barChart.getXAxis();
        axis.setTextSize(6f);
        axis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                String temp="";
                switch((int)value)
                {
                    case 0:
                    {
                        temp="TCases";
                        break;
                    }
                    case 2:
                    {
                        temp="TRecovered";
                        break;
                    }
                    case 4:
                    {
                        temp="TDeaths";
                        break;
                    }
                    case 6:
                    {
                        temp="NCases";
                        break;
                    }
                    case 8:
                    {
                        temp="NRecovered";
                        break;
                    }
                    case 10:
                    {
                        temp="NDeaths";
                        break;
                    }
                }
                return temp;
            }
        });
    }

}
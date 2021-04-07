package com.arcias.covid_19.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.arcias.covid_19.R;
import com.arcias.covid_19.adapter.StateDataAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CountryDataActivity extends AppCompatActivity implements StateDataAdapter.ClickListener, CheckInternetConnection.ConnectionListener {
    private TextView countryName,totalCases,totalRecovered,totalDeaths,newCases,newRecovered,newDeaths,stateName;
   private CovidData covidData;
    private Bundle bundle;
    private List<CovidData>dataList;
    private RecyclerView recyclerView;
    private StateDataAdapter adapter;
    private String country;
    private LinearLayout linearLayout;
    private CardView statesCardViewByBar,countryCardViewByBar,statesCardViewByPie,countryCardViewByPie;
    private BarChart countryBarChart,statesBarChart;
    private Map<String,CovidData>map;
    private AutoCompleteTextView autoCompleteTextView;
    private ImageView search;
    private List<String>statesList;
    private PieChart countryPieChart,statesPieChart,countryPieChartNew;
    private Switch switch1,switch2;
    private LinearLayout noInternetLayout;
    private SwipeRefreshLayout refreshLayout;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_data);
        countryName=findViewById(R.id.country_name);
        totalCases=findViewById(R.id.total_cases);
        totalRecovered=findViewById(R.id.total_recovered);
        totalDeaths=findViewById(R.id.total_deaths);
        newCases=findViewById(R.id.new_cases);
        newRecovered=findViewById(R.id.new_recovered);
        newDeaths=findViewById(R.id.new_deaths);
        stateName=findViewById(R.id.state_title);
        recyclerView=findViewById(R.id.recycler_view_state_data);
        linearLayout=findViewById(R.id.linear_states);
        statesCardViewByBar=findViewById(R.id.states_card_view_bar_chart);
        countryCardViewByPie=findViewById(R.id.country_card_view_by_pie);
        statesCardViewByPie=findViewById(R.id.states_card_view_pie_chart);
        countryCardViewByBar=findViewById(R.id.country_card_view_by_bar);
        countryBarChart=findViewById(R.id.country_bar_chart);
        statesBarChart=findViewById(R.id.states_bar_chart);
        countryPieChart=findViewById(R.id.country_pie_chart);
        statesPieChart=findViewById(R.id.states_pie_chart);
        countryPieChartNew=findViewById(R.id.country_pie_chart_new);
        autoCompleteTextView=findViewById(R.id.search_bar);
        noInternetLayout=findViewById(R.id.no_internet_layout);
        refreshLayout=findViewById(R.id.swipe_refresh);
        switch1=findViewById(R.id.switch1);
        switch2=findViewById(R.id.switch2);

        search=findViewById(R.id.search);
        map=new HashMap<>();
        dataList=new ArrayList<>();
        statesList=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter=new StateDataAdapter(dataList,this);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(this);


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
    private void searchOperation()
    {

        ArrayAdapter adapter=new ArrayAdapter(this, android.R.layout.select_dialog_item,statesList);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setHint(country+"' States..");
        autoCompleteTextView.setAdapter(adapter);
        final Intent intent=new Intent(this, StateDataActivity.class);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = autoCompleteTextView.getText().toString();
                temp=String.valueOf(temp.charAt(0)).toUpperCase()+temp.substring(1).toLowerCase();
                if(!temp.isEmpty()&&statesList.contains(temp))
                {
                    intent.putExtra("state_name",map.get(temp));
                    startActivity(intent);
                    autoCompleteTextView.setText("");
                }

            }
        });
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s= adapterView.getItemAtPosition(i).toString();
                if(!s.isEmpty())
                {
                    intent.putExtra("state_name", map.get(s));
                    startActivity(intent);
                    autoCompleteTextView.setText("");

                }
            }
        });
    }
    private void getApiData()
    {
     final SweetAlertDialog   alertDialog=new SweetAlertDialog(CountryDataActivity.this,SweetAlertDialog.PROGRESS_TYPE);
        alertDialog.setTitleText("Loading..");
        alertDialog.show();
       String url="https://covid-api.mmediagroup.fr/v1/cases";
        RequestQueue queue= Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject obj=response.getJSONObject(country);
                    JSONArray array=obj.toJSONArray(obj.names());
                    Iterator iterator=obj.keys();
                    String states[]=new String[array.length()];
                    int count=0;
                    dataList.clear();
                    if(array.length()>1)
                    {
                        linearLayout.setVisibility(View.VISIBLE);
                       stateName.setText(country+" `Top five States  Based on Total Cases");
                       statesCardViewByBar.setVisibility(View.VISIBLE);
                    }
                    else stateName.setText(country+" has no states data found");
                    while(iterator.hasNext())
                    {
                        states[count]=(String)iterator.next();
                        if(count!=0)
                        {
                            covidData=new CovidData();
                            JSONObject sub=array.getJSONObject(count);
                            covidData.setCountry(states[count]);
                            covidData.setTotalCases(sub.getString("confirmed"));
                            covidData.setTotalDeaths(sub.getString("deaths"));
                            covidData.setTotalRecovered(sub.getString("recovered"));
                            covidData.setDate(sub.getString("updated"));
                            map.put(states[count],covidData);
                            statesList.add(states[count]);
                            dataList.add(covidData);
                        }
                        count++;
                    }
                    Collections.sort(dataList, new Comparator<CovidData>() {
                        @Override
                        public int compare(CovidData t1, CovidData t2) {
                            if(Integer.parseInt(t1.getTotalCases())<Integer.parseInt(t2.getTotalCases()))return 1;
                            return -1;
                        }
                    });
                   adapter.notifyDataSetChanged();

                   ///calling of states bar data graph


                   getStateBarData();
                   searchOperation();
                   getSatesPieData();
                   alertDialog.dismissWithAnimation();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bundle=getIntent().getExtras();
        if(bundle!=null)
        {   covidData=(CovidData)bundle.getSerializable("countries_data");
            countryName.setText(covidData.getCountry());
            totalCases.setText(covidData.getTotalCases());
            totalDeaths.setText(covidData.getTotalDeaths());
            totalRecovered.setText(covidData.getTotalRecovered());
            newCases.setText(covidData.getNewCases());
            newDeaths.setText(covidData.getNewDeaths());
            newRecovered.setText(covidData.getNewRecovered());
            country=covidData.getCountry();
            //calling of country bar data
            getCountryBarData();
            getCountryPieData();
            getCountryPieDataNew();

        }
        getApiData();
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    countryCardViewByBar.setVisibility(View.GONE);
                    switch1.setText("Pie Chart Data");
                    countryCardViewByPie.setVisibility(View.VISIBLE);
                }
                else
                {      countryCardViewByBar.setVisibility(View.VISIBLE);
                    switch1.setText("Bar Chart Data");
                    countryCardViewByPie.setVisibility(View.GONE);

                }
            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                  if(b)
                  {
                      statesCardViewByBar.setVisibility(View.GONE);
                      switch2.setText("Pie Chart Data");
                      statesCardViewByPie.setVisibility(View.VISIBLE);
                  }
                  else
                  {
                      statesCardViewByBar.setVisibility(View.VISIBLE);
                      switch2.setText("Pie Chart Data");
                      statesCardViewByPie.setVisibility(View.GONE);

                  }
            }
        });
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
    private void getCountryPieData()
    {
        List<PieEntry> data=new ArrayList<>();
        data.add(new PieEntry(Integer.parseInt(covidData.getTotalCases()),"Total Cases"));
        data.add(new PieEntry(Integer.parseInt(covidData.getTotalRecovered()),"Total Recovered"));
        data.add(new PieEntry(Integer.parseInt(covidData.getTotalDeaths()),"Total Deaths"));
        PieDataSet pieDataSet=new PieDataSet(data,"Covid-19 Data");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(8f);

        PieData pieData=new PieData(pieDataSet);
        pieData.setValueTextSize(8f);
        countryPieChart.setEntryLabelColor(Color.BLACK);
        countryPieChart.setData(pieData);
        countryPieChart.getDescription().setEnabled(false);
        countryPieChart.setCenterText(covidData.getCountry()+" Covid-19 data");
        countryPieChart.animate();
    }
    private void getCountryPieDataNew()
    {
        List<PieEntry> data=new ArrayList<>();
        data.add(new PieEntry(Integer.parseInt(covidData.getNewCases()),"New Cases"));
        data.add(new PieEntry(Integer.parseInt(covidData.getNewRecovered()),"New Recovered"));
        data.add(new PieEntry(Integer.parseInt(covidData.getNewDeaths()),"New Death"));
        PieDataSet pieDataSet=new PieDataSet(data,"Covid-19 Data");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(8f);

        PieData pieData=new PieData(pieDataSet);
        pieData.setValueTextSize(8f);
        countryPieChartNew.setEntryLabelColor(Color.BLACK);
        countryPieChartNew.setData(pieData);
        countryPieChartNew.getDescription().setEnabled(false);
        countryPieChartNew.setCenterText(covidData.getCountry()+" Covid-19 data");
        countryPieChartNew.animate();

    }

    private void getStateBarData()
    {
            List<BarEntry>data=new ArrayList<>();
            for(int i=0;i<5;i++)
            {
                data.add(new BarEntry((i),Integer.parseInt(dataList.get(i).getTotalCases())));

            }
        BarDataSet barDataSet=new BarDataSet(data,"States Covid-19 Data");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(8f);
        BarData barData=new BarData(barDataSet);
        statesBarChart.setFitBars(true);
        statesBarChart.setData(barData);
        statesBarChart.getDescription().setText(country+" Covid Data");
        statesBarChart.animateY(2000);
        XAxis axis=statesBarChart.getXAxis();
        axis.setTextSize(6f);

        axis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {

                return dataList.get((int)(value)%dataList.size()).getCountry();

            }
        });
    }
    private void getSatesPieData()
    {
        List<PieEntry> data=new ArrayList<>();
        for(int i=0;i<5;i++)
        {
            data.add(new PieEntry(Integer.parseInt(dataList.get(i).getTotalCases()),dataList.get(i).getCountry()));
        }
        PieDataSet pieDataSet=new PieDataSet(data,"Covid-19 Data");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(8f);

        PieData pieData=new PieData(pieDataSet);
        pieData.setValueTextSize(8f);
        statesPieChart.setEntryLabelColor(Color.BLACK);
        statesPieChart.setData(pieData);
        statesPieChart.getDescription().setEnabled(false);
        statesPieChart.setCenterText(country+" States Data");
        statesPieChart.animate();
    }
    private void getCountryBarData()
    {
        List<BarEntry>data=new ArrayList<>();
        data.add(new BarEntry(0,Integer.parseInt(covidData.getTotalCases())));
        data.add(new BarEntry(2,Integer.parseInt(covidData.getTotalRecovered())));
        data.add(new BarEntry(4,Integer.parseInt(covidData.getTotalDeaths())));
        data.add(new BarEntry(6,Integer.parseInt(covidData.getNewCases())));
        data.add(new BarEntry(8,Integer.parseInt(covidData.getNewRecovered())));
        data.add(new BarEntry(10,Integer.parseInt(covidData.getNewDeaths())));
        BarDataSet barDataSet=new BarDataSet(data,"Covid-19 Data");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(8f);

        BarData barData=new BarData(barDataSet);
        countryBarChart.setFitBars(true);
        countryBarChart.setData(barData);
        countryBarChart.getDescription().setText(covidData.getCountry()+" Covid Data");
        countryBarChart.animateY(2000);
        XAxis axis=countryBarChart.getXAxis();
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

    @Override
    public void onClickListener(int position) {
        covidData=dataList.get(position);
        Intent intent=new Intent(this, StateDataActivity.class);
        intent.putExtra("state_name",map.get(covidData.getCountry()));
        startActivity(intent);

    }
}
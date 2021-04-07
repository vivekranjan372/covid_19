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
import com.arcias.covid_19.adapter.CovidDataAdapter;

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
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements CovidDataAdapter.ClickListener, CheckInternetConnection.ConnectionListener {

  private  RecyclerView recyclerView;
  private  CovidDataAdapter adapter;
  private List<CovidData>dataList;
  private TextView totalCases,totalRecovered,totalDeaths,newCases,newRecovered,newDeaths;
  private AutoCompleteTextView autoCompleteTextView;
  private ImageView search;
  private List<String>countryList;
  private Map<String,CovidData>map;
  private BarChart barChart,barChart1;
  private String tCases,tDeaths,tRecovered,nCases,nDeaths,nRecovered;
  private Switch switch1,switch2;
  private CardView worldCardByBar,worldCardByPie,topFiveByBar,topFiveByPie;
  private PieChart worldPieChart,worldPieChartNew,topFivePieChart;
  private SweetAlertDialog alertDialog;
  private LinearLayout noInternetLayout;
  private boolean isConnected;
  private SwipeRefreshLayout refreshLayout;
  private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        totalCases=findViewById(R.id.total_cases);
        totalRecovered=findViewById(R.id.total_recovered);
        totalDeaths=findViewById(R.id.total_deaths);
        newCases=findViewById(R.id.new_cases);
        newRecovered=findViewById(R.id.new_recovered);
        newDeaths=findViewById(R.id.new_deaths);
        autoCompleteTextView=findViewById(R.id.search_bar);
        search=findViewById(R.id.search);
        barChart=findViewById(R.id.bar_chart);
        worldCardByBar=findViewById(R.id.world_data_card_view_by_bar);
        worldCardByPie=findViewById(R.id.world_data_card_view_by_pie);
        switch1=findViewById(R.id.switch1);
        switch2=findViewById(R.id.switch2);
        topFiveByBar=findViewById(R.id.card_view_top_five_by_bar);
        topFiveByPie=findViewById(R.id.card_view_top_five_by_pie);
        worldPieChart=findViewById(R.id.pie_chart);
        worldPieChartNew=findViewById(R.id.pie_chart_new);
        topFivePieChart=findViewById(R.id.pie_chart_top_five);
        noInternetLayout=findViewById(R.id.no_internet_layout);
        barChart1=findViewById(R.id.bar_chart_top_five);//top five country list
        refreshLayout=findViewById(R.id.swipe_refresh);
        //recyclerview with adapter
        recyclerView=findViewById(R.id.recycler_view_covid_data);
        map=new HashMap<>();
        dataList=new ArrayList<>();
        countryList=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setHasFixedSize(true);
        adapter=new CovidDataAdapter(this,dataList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        getApiData();

        searchOperation();


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
    protected void onStart() {
        super.onStart();
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b)
                    {
                        switch1.setText("Pie Chart Data");
                        worldCardByPie.setVisibility(View.VISIBLE);
                        worldCardByBar.setVisibility(View.GONE);
                    }
                    else
                    {
                        switch1.setText("Bar Chart Data");
                        worldCardByPie.setVisibility(View.GONE);
                        worldCardByBar.setVisibility(View.VISIBLE);

                    }
            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    switch2.setText("Pie Chart Data");
                    topFiveByPie.setVisibility(View.VISIBLE);
                    topFiveByBar.setVisibility(View.GONE);
                }
                else
                {
                    switch2.setText("Bar Chart Data");
                    topFiveByPie.setVisibility(View.GONE);
                    topFiveByBar.setVisibility(View.VISIBLE);
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
        this.isConnected=isConnected;
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
    private void getTopCountry()
    {
        List<BarEntry>data=new ArrayList<>();
        data.add(new BarEntry(0,Integer.parseInt(dataList.get(0).getNewCases())));
        data.add(new BarEntry(2,Integer.parseInt(dataList.get(1).getNewCases())));
        data.add(new BarEntry(4,Integer.parseInt(dataList.get(2).getNewCases())));
        data.add(new BarEntry(6,Integer.parseInt(dataList.get(3).getNewCases())));
        data.add(new BarEntry(8,Integer.parseInt(dataList.get(4).getNewCases())));
        BarDataSet barDataSet=new BarDataSet(data,"Countries Covid-19 Data");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(8f);

        BarData barData=new BarData(barDataSet);
        barChart1.setFitBars(true);
        barChart1.setData(barData);
        barChart1.getDescription().setText("Top Five Countries  Covid Data");
        barChart1.animateY(2000);
        XAxis axis=barChart1.getXAxis();
        axis.setTextSize(6f);
        axis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                String temp="";
                switch((int)value)
                {
                    case 0:
                    {
                        temp=dataList.get(0).getCountry();
                        break;
                    }
                    case 2:
                    {
                        temp=dataList.get(1).getCountry();
                        break;
                    }
                    case 4:
                    {
                        temp=dataList.get(2).getCountry();
                        break;
                    }
                    case 6:
                    {
                        temp=dataList.get(3).getCountry();
                        break;
                    }
                    case 8:
                    {
                        temp=dataList.get(4).getCountry();
                        break;
                    }

                }
                return temp;
            }
        });
    }
    private void getBarData()
    {
        List<BarEntry>data=new ArrayList<>();
        data.add(new BarEntry(0,Integer.parseInt(tCases)));
        data.add(new BarEntry(2,Integer.parseInt(tRecovered)));
        data.add(new BarEntry(4,Integer.parseInt(tDeaths)));
        data.add(new BarEntry(6,Integer.parseInt(nCases)));
        data.add(new BarEntry(8,Integer.parseInt(nRecovered)));
        data.add(new BarEntry(10,Integer.parseInt(nDeaths)));
        BarDataSet barDataSet=new BarDataSet(data,"Covid-19 Data");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(8f);

        BarData barData=new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("World Covid Data");
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

    private void getApiData()
    {

        alertDialog=new SweetAlertDialog(MainActivity.this,SweetAlertDialog.PROGRESS_TYPE);
        alertDialog.setTitleText("Loading..");
        alertDialog.show();
        final RequestQueue queue= Volley.newRequestQueue(this);
         String url="https://api.covid19api.com/summary";
    final JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {

            try {
                JSONObject    sub = response.getJSONObject("Global");
                tCases=sub.getString("TotalConfirmed");
                tRecovered=sub.getString("TotalRecovered");
                tDeaths=sub.getString("TotalDeaths");
                nCases=sub.getString("NewConfirmed");
                nDeaths=sub.getString("NewDeaths");
                nRecovered=sub.getString("NewRecovered");
               totalCases.setText(tCases);
               totalRecovered.setText(tRecovered);
                totalDeaths.setText(tDeaths);
               newCases.setText(nCases);
             newDeaths.setText(nDeaths);
             newRecovered.setText(nRecovered);
             //calling of bar chart data
                getBarData();
                getPieData();
                getPieDataNew();
                JSONArray arr=response.getJSONArray("Countries");
                for(int i=0;i<arr.length();i++)
                {
                    JSONObject obj=arr.getJSONObject(i);
                    CovidData covidData=new CovidData();
                    covidData.setCountry(obj.getString("Country"));
                    covidData.setTotalCases(obj.getString("TotalConfirmed"));
                    covidData.setTotalRecovered(obj.getString("TotalRecovered"));
                    covidData.setTotalDeaths(obj.getString("TotalDeaths"));
                    covidData.setNewCases(obj.getString("NewConfirmed"));
                    covidData.setNewDeaths(obj.getString("NewDeaths"));
                    covidData.setNewRecovered(obj.getString("NewRecovered"));
                    covidData.setDate(obj.getString("Date"));
                    //adding country name to list
                    countryList.add(obj.getString("Country"));
                    map.put(obj.getString("Country"),covidData);
                    dataList.add(covidData);
                }
                Collections.sort(dataList, new Comparator<CovidData>() {
                    @Override
                    public int compare(CovidData t1, CovidData t2) {
                        int i=Integer.parseInt(t1.getNewCases());
                        int j=Integer.parseInt(t2.getNewCases());
                        if(i<j)return 1;
                        else return -1;
                    }
                });
                adapter.notifyDataSetChanged();
                //calling of top five countries based on new cases
                getTopCountry();
                getTopCountryForPie();
                alertDialog.dismissWithAnimation();
            } catch (JSONException e) {
                e.printStackTrace();
                alertDialog.dismiss();
            }

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    });
        queue.add(jsonObjectRequest);

    }
    private void getTopCountryForPie()
    {
        List<PieEntry> data=new ArrayList<>();
        data.add(new PieEntry(Integer.parseInt(dataList.get(0).getNewCases()),dataList.get(0).getCountry()));
        data.add(new PieEntry(Integer.parseInt(dataList.get(1).getNewCases()),dataList.get(1).getCountry()));
        data.add(new PieEntry(Integer.parseInt(dataList.get(2).getNewCases()),dataList.get(2).getCountry()));
        data.add(new PieEntry(Integer.parseInt(dataList.get(3).getNewCases()),dataList.get(3).getCountry()));
        data.add(new PieEntry(Integer.parseInt(dataList.get(4).getNewCases()),dataList.get(4).getCountry()));
        PieDataSet pieDataSet=new PieDataSet(data,"Covid-19 Data");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(8f);

        PieData pieData=new PieData(pieDataSet);
        pieData.setValueTextSize(8f);
        topFivePieChart.setEntryLabelColor(Color.BLACK);
        topFivePieChart.setData(pieData);
        topFivePieChart.getDescription().setEnabled(false);
        topFivePieChart.setCenterText("Top Five Countries");
        topFivePieChart.animate();

    }
    private void getPieData()
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
        worldPieChart.setEntryLabelColor(Color.BLACK);
        worldPieChart.setData(pieData);
        worldPieChart.getDescription().setEnabled(false);
        worldPieChart.setCenterText("World Covid-19 data");
        worldPieChart.animate();

    }
    private void getPieDataNew()
    {
        List<PieEntry> data=new ArrayList<>();

        data.add(new PieEntry(Integer.parseInt(nCases),"New Cases"));
        data.add(new PieEntry(Integer.parseInt(nRecovered),"New Recovered"));
        data.add(new PieEntry(Integer.parseInt(nDeaths),"New Death"));
        PieDataSet pieDataSet=new PieDataSet(data,"Covid-19 Data");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(8f);

        PieData pieData=new PieData(pieDataSet);
        pieData.setValueTextSize(8f);
        worldPieChartNew.setEntryLabelColor(Color.BLACK);
        worldPieChartNew.setData(pieData);
        worldPieChartNew.getDescription().setEnabled(false);
        worldPieChartNew.setCenterText("World Covid-19 data");
        worldPieChartNew.animate();

    }
    private void searchOperation()
    {
        ArrayAdapter adapter=new ArrayAdapter(this, android.R.layout.select_dialog_item,countryList);
        autoCompleteTextView.setThreshold(1);

        autoCompleteTextView.setAdapter(adapter);
        final Intent intent=new Intent(MainActivity.this, CountryDataActivity.class);
           search.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   String temp = autoCompleteTextView.getText().toString();
                   temp=String.valueOf(temp.charAt(0)).toUpperCase()+temp.substring(1).toLowerCase();
                   if(!temp.isEmpty()&&countryList.contains(temp))
                   {
                       intent.putExtra("countries_data",map.get(temp));
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
                         intent.putExtra("countries_data", map.get(s));
                         startActivity(intent);
                         autoCompleteTextView.setText("");

                     }
               }
           });
    }


    @Override
    public void onItemClick(int position) {
            CovidData covidData=dataList.get(position);
            Intent intent=new Intent(MainActivity.this, CountryDataActivity.class);
            intent.putExtra("countries_data",map.get(covidData.getCountry()));
            startActivity(intent);
    }


}
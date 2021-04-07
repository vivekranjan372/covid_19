package com.arcias.covid_19.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arcias.covid_19.R;
import com.arcias.covid_19.model.CovidData;

import java.util.List;

public class CovidDataAdapter extends RecyclerView.Adapter<CovidDataAdapter.ViewHolder>  {
    private Context context;
    private List<CovidData>dataList;
    private ClickListener clickListener;
    public interface ClickListener
    {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(ClickListener clickListener)
    {
        this.clickListener=clickListener;
    }
    public CovidDataAdapter(Context context,List<CovidData>dataList)
    {
        this.context=context;
        this.dataList=dataList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.covid_data,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       CovidData covidData=dataList.get(position);
       holder.country.setText(covidData.getCountry());

        holder.totalCases.setText(covidData.getTotalCases());
        holder.totalDeaths.setText(covidData.getTotalDeaths());
        holder.totalRecovered.setText(covidData.getTotalRecovered());
        holder.newCases.setText(covidData.getNewCases());

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView country;
        public TextView totalCases;
        public TextView totalDeaths;
        public TextView totalRecovered;
        public TextView newCases;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           country=itemView.findViewById(R.id.country);
           totalCases=itemView.findViewById(R.id.total_cases);
           totalDeaths=itemView.findViewById(R.id.total_deaths);
           totalRecovered=itemView.findViewById(R.id.total_recovered);
           newCases=itemView.findViewById(R.id.new_cases);
         itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                      if(clickListener!=null)
                      {
                          int position =getAdapterPosition();
                          if(position!=RecyclerView.NO_POSITION)
                          {
                              clickListener.onItemClick(position);
                          }
                      }
             }
         });

        }
    }

}

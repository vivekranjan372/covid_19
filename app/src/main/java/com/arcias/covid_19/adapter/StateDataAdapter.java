package com.arcias.covid_19.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arcias.covid_19.R;
import com.arcias.covid_19.model.CovidData;

import java.util.List;

public class StateDataAdapter extends RecyclerView.Adapter<StateDataAdapter.ViewHolder>{

  private List<CovidData>dataList;
  private Context context;
  private ClickListener clickListener;
  public StateDataAdapter(List<CovidData>dataList,Context context)
  {
      this.dataList=dataList;
      this.context=context;
  }

  public void setOnClickListener(ClickListener clickListener)
  {
      this.clickListener=clickListener;
  }
  public interface ClickListener
  {
      void onClickListener(int position);
  }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.state_data,parent,false);
        return new StateDataAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      CovidData covidData=dataList.get(position);
holder.date.setText(covidData.getDate());
holder.totalCases.setText(covidData.getTotalCases());
holder.totalRecovered.setText(covidData.getTotalRecovered());
holder.totalDeaths.setText(covidData.getTotalDeaths());
holder.stateName.setText(covidData.getCountry());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView stateName,totalCases,totalDeaths,totalRecovered,date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stateName=itemView.findViewById(R.id.state_name);
            totalCases=itemView.findViewById(R.id.total_cases);
            totalDeaths=itemView.findViewById(R.id.total_deaths);
            totalRecovered=itemView.findViewById(R.id.total_recovered);
            date=itemView.findViewById(R.id.date);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   if(clickListener!=null)
                   {
                       if(getAdapterPosition()!=RecyclerView.NO_POSITION)
                       {
                           clickListener.onClickListener(getAdapterPosition());
                       }
                   }

                }
            });
        }
    }
}

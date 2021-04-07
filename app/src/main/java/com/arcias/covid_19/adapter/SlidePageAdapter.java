package com.arcias.covid_19.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.arcias.covid_19.model.OnBoardingContent;
import com.arcias.covid_19.R;

import java.util.List;

public class SlidePageAdapter extends PagerAdapter {
private Context context;
private List<OnBoardingContent> contentList;

    public SlidePageAdapter(Context context, List<OnBoardingContent> contentList) {
        this.context = context;
        this.contentList = contentList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater=(LayoutInflater) context.getSystemService
                (context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.slides_item,container,false);
        ImageView imageView=view.findViewById(R.id.slide_image);
        TextView title=view.findViewById(R.id.slide_title);
        TextView description=view.findViewById(R.id.slide_description);
        imageView.setImageResource(contentList.get(position).getImageId());
        title.setText(contentList.get(position).getTitle());
        description.setText(contentList.get(position).getDescription());
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return contentList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return view==object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}

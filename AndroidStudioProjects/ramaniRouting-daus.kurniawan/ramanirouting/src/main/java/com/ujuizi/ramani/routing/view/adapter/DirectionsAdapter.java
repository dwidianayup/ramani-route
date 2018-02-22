package com.ujuizi.ramani.routing.view.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ujuizi.ramani.routing.R;
import com.ujuizi.ramani.routing.helper.JourneyModel;

/**
 * Created by ujuizi on 1/9/17.
 */

public class DirectionsAdapter extends BaseAdapter {

    private JourneyModel listData;
    private Activity mActivity;
    private LayoutInflater mInflater;

    public DirectionsAdapter(Activity activity, JourneyModel data) {
        mActivity = activity;
        listData = data;

        mInflater = LayoutInflater.from(mActivity);
    }

    @Override
    public int getCount() {
        return listData.getInstructionObj().size();
    }

    @Override
    public Object getItem(int position) {
        return listData.getInstructionObj().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.directions_layout_item, null);
            holder = new ViewHolder();
            holder.textDistance = (TextView) convertView.findViewById(R.id.directions_distance_value_item);
            holder.textRoadName = (TextView) convertView.findViewById(R.id.directions_roadname_item);
            holder.directionImage = (ImageView) convertView.findViewById(R.id.directions_image_item);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (listData.getInstructionObj().get(position).getTitle().equals("")) {
            holder.textRoadName.setText(listData.getInstructionObj().get(position).getManuver() + " No Road Name");
        } else {
            holder.textRoadName.setText(listData.getInstructionObj().get(position).getManuver() + " " + listData.getInstructionObj().get(position).getTitle());
        }

        holder.textDistance.setText(listData.getInstructionObj().get(position).getDistanceName());
        try {
            holder.directionImage.setImageResource(listData.getInstructionObj().get(position).getIcon());
        } catch (Exception e) {
            Log.e("NotificationAdapter", "Failure to get drawable id.", e);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView textDistance;
        TextView textRoadName;
        ImageView directionImage;
    }
}

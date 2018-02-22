package com.ujuizi.ramani.routing.view.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ujuizi.ramani.routing.R;
import com.ujuizi.ramani.routing.listener.RoutingListener;
import com.ujuizi.ramani.routing.helper.SearchAddressModel;
import com.ujuizi.ramani.routing.view.SearchAddressView;

import java.util.ArrayList;

/**
 * Created by ujuizi on 1/10/17.
 */

public class SearchViewAdapter extends BaseAdapter {

    private RoutingListener.SearchAddressListener mListener;
    private int mStatus;
    private LayoutInflater mInflater;
    private ArrayList<SearchAddressModel> listData;
    private Activity mActivity;

    /**
     * @param activity set the activity
     * @param data arraylist from {@link SearchAddressModel}
     * @param status Status search address from @{@link SearchAddressView STATUS_START_POINT , STATUS_END_POINT, STATUS_EMPTY}
     * @param listener set RoutingListener.SearchAddressListener
     */
    public SearchViewAdapter(Activity activity, ArrayList<SearchAddressModel> data, int status,
                             RoutingListener.SearchAddressListener listener) {
        mActivity = activity;
        listData = data;
        mStatus = status;
        mListener = listener;
        mInflater = LayoutInflater.from(mActivity);

    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.search_view_item, null);
            holder = new ViewHolder();
            holder.viewAddress = (RelativeLayout) convertView.findViewById(R.id.view_search_item);
            holder.textDisplayName = (TextView) convertView.findViewById(R.id.displayName_item);
            holder.textTypePlace = (TextView) convertView.findViewById(R.id.type_place_item);
            holder.textClassPlace = (TextView) convertView.findViewById(R.id.class_place_item);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.viewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mStatus == SearchAddressView.STATUS_START_POINT) {
//
//                } else if (mStatus == SearchAddressView.STATUS_START_POINT) {
//
//                } else if (mStatus == SearchAddressView.STATUS_EMPTY) {
//
//                }

                if (mListener != null)
                    mListener.onSelectedItemSearch(listData.get(position), mStatus);

            }
        });

        holder.textDisplayName.setText(listData.get(position).getDisplayName());

        holder.textTypePlace.setText("Type : " + listData.get(position).getTypePlace());
        holder.textClassPlace.setText("Class : "+listData.get(position).getClassType());

        return convertView;
    }



    private static class ViewHolder {
        RelativeLayout viewAddress;
        TextView textDisplayName;
        TextView textTypePlace;
        TextView textClassPlace;
    }
}

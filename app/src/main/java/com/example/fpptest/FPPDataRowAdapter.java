package com.example.fpptest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FPPDataRowAdapter extends ArrayAdapter<FPPData> {
    private int layoutResource;

    public FPPDataRowAdapter(Context context, int layoutResource, List<FPPData> fpp_list) {
        super(context, layoutResource, fpp_list);
        this.layoutResource = layoutResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
        }
        final FPPData fpp_data = getItem(position);

        if (fpp_data != null) {
            TextView ipTextView = (TextView) view.findViewById(R.id.ip);
            TextView hostTextView = (TextView) view.findViewById(R.id.host);
            TextView modeTextView = (TextView) view.findViewById(R.id.mode);
            TextView versionTextView = (TextView) view.findViewById(R.id.version);
            TextView platformTextView = (TextView) view.findViewById(R.id.platform);
            TextView statusTextView = (TextView) view.findViewById(R.id.status);

            if (ipTextView != null) {
                ipTextView.setText(fpp_data.getIP());
            }
            if (hostTextView != null) {
                hostTextView.setText(fpp_data.getHost());
            }
            if (modeTextView != null) {
                modeTextView.setText(fpp_data.getMode());
            }
            if (versionTextView != null) {
                versionTextView.setText(fpp_data.getPrettyVersion());
            }
            if (platformTextView != null) {
                platformTextView.setText(fpp_data.getPlatform());
            }
            if (statusTextView != null) {
                statusTextView.setText(fpp_data.getStatus());
            }
        }

        return view;
    }
}

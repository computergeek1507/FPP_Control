package com.example.fpptest;

import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class FPPWiringRowAdapter extends ArrayAdapter<FPPOutput>{
        private int layoutResource;
        public FPPWiringRowAdapter(Context context, int layoutResource, List<FPPOutput> string_list) {
                super(context, layoutResource, string_list);
                this.layoutResource = layoutResource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;

                if (view == null) {
                        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                        view = layoutInflater.inflate(layoutResource, null);
                }
                final FPPOutput string_data = getItem(position);

                if (string_data != null) {
                        TextView portTextView = (TextView) view.findViewById(R.id.port);
                        TextView modelsTextView = (TextView) view.findViewById(R.id.models);

                        if (portTextView != null) {
                                portTextView.setText(string_data.getPortNumberStr());
                        }
                        if (modelsTextView != null) {
                                modelsTextView.setText(string_data.getPortModelStr());
                        }

                }

                return view;
        }
}

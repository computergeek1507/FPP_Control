package com.example.fpptest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WiringViewActivity extends AppCompatActivity {
    RequestQueue queue;
    FPPWiringRowAdapter array_Adapter;
    ListView wiring_list;
    final List<FPPOutput> fpp_outputs = new ArrayList<FPPOutput>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiring_view);

        wiring_list = findViewById(R.id.wirelist);
        array_Adapter = new FPPWiringRowAdapter(this, R.layout.wiring_row_item, fpp_outputs );
        wiring_list.setAdapter( array_Adapter);

        queue = Volley.newRequestQueue(WiringViewActivity.this);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        FPPData data = (FPPData)intent.getSerializableExtra(MainActivity.FPP_DATA);
        setTitle(data.getHost() + " - " + data.getIP() );
        GetOutputList(data);
    }

    private void GetOutputList(FPPData fpp) {
        String file = "co-pixelStrings";
        if (fpp.getPlatform().contains("Beagle")) {
            file = "co-bbbStrings";
        }

        final String string_url = String.format("http://%s/fppjson.php?command=getChannelOutputs&file=%s", fpp.getIP(), file);
        final JsonObjectRequest string_jsonRequest = new JsonObjectRequest(Request.Method.GET, string_url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AddOutputTypes(fpp, response);
                    }
                }, null);
        queue.add(string_jsonRequest);

        final String other_url = String.format("http://%s/fppjson.php?command=getChannelOutputs&file=co-other", fpp.getIP());
        final JsonObjectRequest other_jsonRequest = new JsonObjectRequest(Request.Method.GET, other_url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AddOutputTypes(fpp, response);
                    }
                }, null);
        queue.add(other_jsonRequest);

        final String channel_url = String.format("http://%s/fppjson.php?command=getChannelOutputs&file=channelOutputsJSON", fpp.getIP());
        final JsonObjectRequest channel_jsonRequest = new JsonObjectRequest(Request.Method.GET, channel_url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AddOutputTypes(fpp, response);
                    }
                }, null);
        queue.add(channel_jsonRequest);
    }

    private void AddOutputTypes(FPPData fpp, JSONObject json) {
        WiringData wiringData = new WiringData(json);

        fpp_outputs.addAll(wiringData.getOutputs());

        array_Adapter.notifyDataSetChanged();
    }
}
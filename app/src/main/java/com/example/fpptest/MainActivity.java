package com.example.fpptest;

//import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;

public class MainActivity extends AppCompatActivity {
    ListView list_view;
    final List<FPPData> fpp_list = new ArrayList<FPPData>();
    SharedPreferences pref;
    ArrayAdapter<FPPData> array_Adapter;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getApplicationContext().getSharedPreferences("FPP_Test_Pref", MODE_PRIVATE);
        queue = Volley.newRequestQueue(MainActivity.this);

        list_view = findViewById(R.id.list);
        array_Adapter = new ArrayAdapter<FPPData>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                fpp_list);
        list_view.setAdapter(array_Adapter );

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                int pos = parent.getPositionForView(v);
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                //
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        FPPData fpp = array_Adapter.getItem(position);

                        switch (item.getItemId()) {
                            case R.id.starttest:
                                SetTesting(fpp.getIP(),1);
                                return true;
                            case R.id.stoptest:
                                SetTesting(fpp.getIP(),0);
                                return true;
                            case R.id.open:
                                Uri uriUrl = Uri.parse("http://" + fpp.getIP());
                                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                                startActivity(launchBrowser);
                                return true;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
        LoadFPPList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add:
                Show_Input();
                return true;
            //case R.id.action_settings:
               //showHelp();
             //   return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void Show_Input()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add FPP");
        alert.setMessage("Enter Host Name or IP Address:");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Probe_FPP(input.getText().toString() ,true);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(getApplicationContext(), "User Cancelled", Toast.LENGTH_LONG).show();
            }
        });

        alert.show();
    }

    void Probe_FPP(String ip, boolean probeForOthers)
    {
        String url=String.format("http://%s/fppjson.php?command=getSysInfo", ip);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        add_fpp_device(response, probeForOthers);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "That didn't work!\n" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }
    
    void add_fpp_device(String json, boolean probeForOthers)
    {
        FPPData fpp = new FPPData(json);
        if(!fpp_list.contains(fpp)) {
            fpp_list.add(fpp);
            Collections.sort(fpp_list, new FPPDataComparator());
            array_Adapter.notifyDataSetChanged();

            if(probeForOthers) {
                find_other_fpp_devices(fpp.getIP());
            }
            SaveFPPList();
        }
    }

    void add_fpp_devices(String json)
    {
        try {
            JSONArray reader = new JSONArray(json);
            for(int i = 0; i <reader.length(); ++i)
            {
                FPPData fpp = new FPPData( reader.getJSONObject(i));
                if(!fpp_list.contains(fpp)) {
                    fpp_list.add(fpp);
                }
            }
            Collections.sort(fpp_list, new FPPDataComparator());

            array_Adapter.notifyDataSetChanged();
            SaveFPPList();
        }
        catch (JSONException ex) {

        }
    }
    
    void find_other_fpp_devices(String ip)
    {
        String url = String.format("http://%s/fppjson.php?command=getFPPSystems", ip);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        add_fpp_devices(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "That didn't work!\n" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

    void SaveFPPList()
    {
        List<String> ip_list = new ArrayList<String>();

        for (FPPData fpp : fpp_list) {
            ip_list.add(fpp.getIP());
        }
        String ips = TextUtils.join(",", ip_list);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Prev_IPs", ips);
        editor.apply();
    }

    void LoadFPPList()
    {
        String ips = pref.getString("Prev_IPs", "");
        String[] arrOfIps = ips.split(",");

        for (String ip : arrOfIps) {
            ip = ip.trim();//not needed probaby
            if(ip.isEmpty()) {
                continue;
            }
            Probe_FPP(ip,false);
        }
    }

    void SetTesting(String ip, int enable)
    {
        try {
            JSONObject testSet = new JSONObject();
            testSet.put("cycleMS", 500);
            testSet.put("enabled", enable);
            testSet.put("channelSet", "1-1048576");
            testSet.put("channelSetType", "channelRange");

            testSet.put("mode", "RGBChase");
            testSet.put("subMode", "RGBChase-RGB");
            testSet.put("colorPattern", "FF000000FF000000FF");

            String data = testSet.toString();

            String url = String.format("http://%s/fppjson.php", ip);
            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(), "Sent Successfully" , Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error!\n" + error.getMessage() , Toast.LENGTH_LONG).show();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("command", "setTestMode");
                    MyData.put("data", data);
                    return MyData;
                }
            };
            queue.add(MyStringRequest);
        }
        catch (Exception ex)
        {
        }
    }
}
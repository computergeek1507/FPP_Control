package com.example.fpptest;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;

public class MainActivity extends AppCompatActivity {
    ListView list_view;
    final List<FPPData> fpp_list = new ArrayList<FPPData>();
    SharedPreferences pref;
    RequestQueue queue;
    List<String> ip_list = new ArrayList<String>();
    FPPDataRowAdapter array_Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getApplicationContext().getSharedPreferences("FPP_Test_Pref", MODE_PRIVATE);
        queue = Volley.newRequestQueue(MainActivity.this);

        list_view = findViewById(R.id.list);
        array_Adapter = new FPPDataRowAdapter(this, R.layout.row_item, fpp_list);
        list_view.setAdapter(array_Adapter);

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        FPPData fpp = array_Adapter.getItem(position);

                        switch (item.getItemId()) {
                            case R.id.startplaylist:
                                GetPlaylists(fpp);
                                return true;
                            case R.id.stopplaylist:
                                StopPlaylistGracefully(fpp);
                                return true;
                            case R.id.stopplaylistnow:
                                StopPlaylistNow(fpp);
                                return true;
                            case R.id.starttest:
                                SetTesting(fpp,1);
                                return true;
                            case R.id.starttestmodel:
                                SetTestingModel(fpp);
                                return true;
                            case R.id.stoptest:
                                SetTesting(fpp,0);
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
                Show_IP_Input();
                return true;
            case R.id.action_start_all_test:
                for (FPPData fpp : fpp_list) {
                    SetTesting(fpp,1);
                }
                return true;
            case R.id.action_stop_all_test:
                for (FPPData fpp : fpp_list) {
                    SetTesting(fpp,0);
                }
                return true;
            case R.id.action_clear_ips:
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("Prev_IPs", "");
                editor.apply();
                return true;

            //case R.id.action_settings:
               //showHelp();
             //   return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void Show_IP_Input() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add FPP Device");
        alert.setMessage("Enter Host Name or IP Address:");

        final EditText input = new EditText(this);
        alert.setView(input);

        input.setText( pref.getString("Last_IP", ""));

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String ip = input.getText().toString().trim();
                Probe_FPP(ip ,true);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("Last_IP", ip);
                editor.apply();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(getApplicationContext(), "User Cancelled", Toast.LENGTH_LONG).show();
            }
        });

        alert.show();
    }

    void Probe_FPP(String ip, boolean probeForOthers) {
        final String url = String.format("http://%s/fppjson.php?command=getSysInfo", ip);
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        add_fpp_device(response, probeForOthers);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "That didn't work!\n" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(jsonRequest);
    }
    
    void add_fpp_device(JSONObject json, boolean probeForOthers) {
        FPPData fpp = new FPPData(json);
        if(!fpp_list.contains(fpp)) {
            fpp_list.add(fpp);

            if(!ip_list.contains(fpp.getIP())) {
                ip_list.add(fpp.getIP());
            }

            Collections.sort(fpp_list, new FPPDataComparator());
            array_Adapter.notifyDataSetChanged();

            if(probeForOthers) {
                find_other_fpp_devices(fpp.getIP());
            }
            SaveFPPList();
        }
    }

    void add_fpp_devices(JSONArray jsonArray) {
        try {
            for(int i = 0; i <jsonArray.length(); ++i) {
                FPPData fpp = new FPPData( jsonArray.getJSONObject(i));
                if(!fpp_list.contains(fpp)) {
                    fpp_list.add(fpp);
                }

                if(!ip_list.contains(fpp.getIP())) {
                    ip_list.add(fpp.getIP());
                }
            }
            Collections.sort(fpp_list, new FPPDataComparator());

            array_Adapter.notifyDataSetChanged();
            SaveFPPList();
        }
        catch (JSONException ex) {
            Toast.makeText(getApplicationContext(), "That didn't work!\n" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    void find_other_fpp_devices(String ip) {
        final String url = String.format("http://%s/fppjson.php?command=getFPPSystems", ip);
        final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        add_fpp_devices(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "That didn't work!\n" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(jsonRequest);
    }

    void SaveFPPList() {
        final String ips = TextUtils.join(",", ip_list);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Prev_IPs", ips);
        editor.apply();
    }

    void LoadFPPList() {
        final String ips = pref.getString("Prev_IPs", "");
        final String[] arrOfIps = ips.split(",");

        for (String ip : arrOfIps) {
            ip = ip.trim();//not needed probably
            if(ip.isEmpty()) {
                continue;
            }
            Probe_FPP(ip, true);
        }
    }

    void SetTesting(FPPData fpp, int enable) {
        SetTesting(fpp,enable,"1-1048576");
    }

    void SetTesting(FPPData fpp, int enable, String channels) {
        try {
            JSONObject testSet = new JSONObject();
            testSet.put("cycleMS", 500);
            testSet.put("enabled", enable);
            testSet.put("channelSet", channels);
            testSet.put("channelSetType", "channelRange");

            testSet.put("mode", "RGBChase");
            testSet.put("subMode", "RGBChase-RGB");
            testSet.put("colorPattern", "FF000000FF000000FF");

            final  String data = testSet.toString();

            if(fpp.getVerMajor() > 4) {
                final String url = String.format("http://%s/api/testmode", fpp.getIP());
                Send_HTTP_POST_Request(url, data);
            } else {
                final String url = String.format("http://%s/fppjson.php", fpp.getIP());
                final StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
        }
        catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "That didn't work!\n" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void GetPlaylists(FPPData fpp) {
        final String url = String.format("http://%s/api/playlists", fpp.getIP());
        final JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        SelectPlaylist(fpp, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "That didn't work!\n" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

    private void SelectPlaylist(FPPData fpp, JSONArray jsonArray) {
        try {
            List<String> playLists = new ArrayList<String>();
            for(int i = 0; i < jsonArray.length(); ++i) {
                playLists.add(jsonArray.getString(i));
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Playlist")
                    .setItems(playLists.toArray(new String[0]), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            StartPlaylist(fpp, playLists.get(which));
                        }
                    });
            builder.show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "That didn't work!\n" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void StartPlaylist(FPPData fpp, String playlist) {
        try {
            if(fpp.getVerMajor() > 4) {
                JSONObject testSet = new JSONObject();
                testSet.put("command", "Start Playlist");

                JSONArray array = new JSONArray();
                array.put(playlist);
                array.put("true");//repeat
                array.put("false");//start if running
                testSet.put("args", array);

                final String data = testSet.toString();
                final String url = String.format("http://%s/api/command", fpp.getIP());

                Send_HTTP_POST_Request(url, data);
            } else {
                final String url = String.format("http://%s/fppxml.php?command=startPlaylist&playList=%s&repeat=1", fpp.getIP(), playlist);
                Send_HTTP_Request(url);
            }

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "That didn't work!\n" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void StopPlaylistNow(FPPData fpp) {
        if( fpp.getVerMajor() > 4 ) {
            Send_HTTP_Request(String.format("http://%s/api/playlists/stop", fpp.getIP()));
        } else {
            Send_HTTP_Request(String.format("http://%s/fppxml.php?command=stopNow", fpp.getIP()));
        }
    }

    private void StopPlaylistGracefully(FPPData fpp) {
        if( fpp.getVerMajor() > 4 ) {
            Send_HTTP_Request(String.format("http://%s/api/playlists/stopgracefully", fpp.getIP()));
        } else {
            Send_HTTP_Request(String.format("http://%s/fppxml.php?command=stopGracefully", fpp.getIP()));
        }
    }

    private void SetTestingModel(FPPData fpp) {
        final String url = String.format("http://%s/api/models", fpp.getIP());
        final JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        SelectModel(fpp, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "That didn't work!\n" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

    private void SelectModel(FPPData fpp, JSONArray jsonArray) {
        try {
            List<FPPModel> models = new ArrayList<FPPModel>();
            List<String> str_models = new ArrayList<String>();
            for(int i = 0; i < jsonArray.length(); ++i) {
                FPPModel model = new FPPModel(jsonArray.getJSONObject(i));
                models.add(model);
                str_models.add(model.getName());
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Model")
                    .setItems(str_models.toArray(new String[0]), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            TestModel(fpp, models.get(which));
                        }
                    });
            builder.show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "That didn't work!\n" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void TestModel(FPPData fpp, FPPModel model) {
        final String channels = String.format("%d-%d", model.getStartChannel(),model.getEndChannel());
        SetTesting(fpp,1, channels);
    }

    private void Send_HTTP_Request(String url) {
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Sent Successfully" , Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "That didn't work!\n" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

    private void Send_HTTP_POST_Request(String url, String data) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Sent Successfully" , Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "That didn't work!\n" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return data == null ? null : data.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", data, "utf-8");
                    return null;
                }
            }
        };
        queue.add(stringRequest);
    }
}
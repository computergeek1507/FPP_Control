package com.example.fpptest;
import android.os.AsyncTask;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class StatusUpdater  extends AsyncTask<FPPData, Void, Pair<FPPData, String>>{

    protected MainActivity mainActivity;

    public StatusUpdater(MainActivity mainActivityRef) {
        mainActivity = mainActivityRef;
    }

    @Override
    protected Pair<FPPData, String> doInBackground(FPPData... fpps) {
        StringBuffer webPageContentStringBuffer = new StringBuffer();

        if (fpps.length <= 0) {
            return new Pair<FPPData,String>(new FPPData(), "Invalid IP");
        }

        try {
            String ip = fpps[0].getIP();

            String url = String.format("http://%s/fppjson.php?command=getFPPstatus", ip);

            if(fpps[0].getVerMajor() > 4) {
                url = String.format("http://%s/api/system/status", ip);
            }
            URL webUrl = new URL(url);
            InputStream webPageDataStream = webUrl.openStream();
            InputStreamReader webPageDataReader = new InputStreamReader(webPageDataStream);
            int maxBytesToRead = 1024;
            char[] buffer = new char[maxBytesToRead];
            int bytesRead =  webPageDataReader.read(buffer);

            while(bytesRead != -1) {
                webPageContentStringBuffer.append(buffer, 0, bytesRead);
                bytesRead = webPageDataReader.read(buffer);
            }

            return new Pair<FPPData,String>(fpps[0],webPageContentStringBuffer.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Pair<FPPData,String>(new FPPData(),"Failed to get FPP Status");
    }

    @Override
    protected void onProgressUpdate(Void... progress) {
        super.onProgressUpdate();
    }

    @Override
    protected void onPostExecute(Pair<FPPData, String> content) {
        super.onPostExecute(content);
        if (mainActivity != null) {
            try {
                JSONObject reader = new JSONObject(content.second);
                mainActivity.SetFPPStatus(content.first, reader.getString("status_name"));
            }
            catch (JSONException ex) {

            }
        }
    }
}

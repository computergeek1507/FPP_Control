package com.example.fpptest;

import org.json.JSONException;
import org.json.JSONObject;

public class OtherOutput implements FPPOutput {

    public String getPortNumberStr() {
        return String.format("%s", m_type );
    }

    public String getPortModelStr() {
        return String.format("Start Channel:%d, Channel Count:%d",m_startChannel, m_channelCount);
    }

    private String m_type = "";
    private int m_startChannel = -1;
    private int m_channelCount = 0;

    public OtherOutput() {
    }

    public OtherOutput(JSONObject json) {
        readJson(json);
    }

    protected void readJson(JSONObject json) {
        try {
            if(json.has("type") ) {
                m_type = json.getString("type");
            }

            if(json.has("channelCount") ) {
                m_channelCount = json.getInt("channelCount");
            }
            if(json.has("startChannel") ) {
                m_startChannel = json.getInt("startChannel");
            }
        } catch(JSONException ex) {

        }
    }

    public String toString()
    {
        return String.format("%s", m_type );
    }
}
package com.example.fpptest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SerialOutput implements FPPOutput {

    public int getOutputNumber() {
        return m_outputNumber;
    }

    public String getPortNumberStr() {
        return String.format("Serial Port:%d",m_outputNumber + 1);
    }

    public String getPortModelStr() {
        return String.format("Start Channel:%d, Channel Count:%d",m_startChannel, m_channelCount);
    }

    private int m_outputNumber = -1;
    private int m_startChannel = -1;
    private int m_channelCount = 0;

    public SerialOutput() {
    }

    public SerialOutput(JSONObject json) {
        readJson(json);
    }

    protected void readJson(JSONObject json) {
        try {

            if(json.has("outputNumber") ) {
                m_outputNumber = json.getInt("outputNumber");
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
        return String.format("Serial Port:%d",m_outputNumber + 1);
    }
}
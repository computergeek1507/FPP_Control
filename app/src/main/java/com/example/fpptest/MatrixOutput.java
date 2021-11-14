package com.example.fpptest;

import org.json.JSONException;
import org.json.JSONObject;

public class MatrixOutput implements FPPOutput {

    public String getPortNumberStr() {
        return "Panel Matrix";
    }

    public String getPortModelStr() {
        return String.format("%s H:%dxW:%d Start Channel:%d Channel Count:%d Color Order:%s", m_subType, m_height, m_width, m_startChannel, m_channelCount, m_colorOrder);
    }

    private String m_subType = "";
    private int m_startChannel = -1;
    private int m_channelCount = 0;
    private String m_colorOrder = "";
    private int m_width = 0;
    private int m_height = 0;

    public MatrixOutput() {
    }

    public MatrixOutput(JSONObject json) {
        readJson(json);
    }

    protected void readJson(JSONObject json) {
        try {
            if(json.has("subType") ) {
                m_subType = json.getString("subType");
            }
            if(json.has("channelCount") ) {
                m_channelCount = json.getInt("channelCount");
            }
            if(json.has("startChannel") ) {
                m_startChannel = json.getInt("startChannel");
            }
            if(json.has("colorOrder") ) {
                m_colorOrder = json.getString("colorOrder");
            }
            if(json.has("panelWidth") ) {
                m_width = json.getInt("panelWidth");
            }
            if(json.has("panelHeight") ) {
                m_height = json.getInt("panelHeight");
            }
        } catch(JSONException ex) {

        }
    }

    public String toString() {
        return String.format("%s:%d", m_subType ,m_startChannel);
    }
}
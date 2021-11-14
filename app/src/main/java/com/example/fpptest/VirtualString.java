package com.example.fpptest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VirtualString {

    private String m_Description = "";
    private int m_StartChannel = -1;
    private int m_PixelCount = 0;
    private int m_GroupCount = 0;
    private int m_Reverse = 0;
    private String m_ColorOrder = "";
    private int m_NullNodes = 0;
    private int m_ZigZag = 0;
    private int m_Brightness = 100;
    private double m_Gamma = 1.0;
    private String m_SmartRemote = "";
    private int m_ExpansionType = 0;


    public VirtualString(){}
    public VirtualString(JSONObject json) {
        readJson(json);
    }

    public String getDescription() {
        return m_Description;
    }

    public int getStartChannel() {
        return m_StartChannel;
    }

    public int getPixelCount() {
        return m_PixelCount;
    }

    public int getGroupCount() {
        return m_GroupCount;
    }

    public int getReverse() {
        return m_Reverse;
    }

    public String getColorOrder() {
        return m_ColorOrder;
    }

    public int getNullNodes() {
        return m_NullNodes;
    }

    public int getZigZag() {
        return m_ZigZag;
    }

    public int getBrightness() {
        return m_Brightness;
    }

    public double getGamma() {
        return m_Gamma;
    }

    public String getSmartRemote() {
        return m_SmartRemote;
    }

    public void setSmartRemote(String smartRemote) {
        this.m_SmartRemote = smartRemote;
    }

    public int getM_ExpansionType() {
        return m_ExpansionType;
    }

    public void setExpansionType(int expansionType) {
        this.m_ExpansionType = expansionType;
    }

    void readJson(JSONObject json) {
        try {
            if(json.has("description") ) {
                m_Description = json.getString("description");
            }
            if(json.has("startChannel") ) {
                m_StartChannel = json.getInt("startChannel");
            }
            if(json.has("pixelCount") ) {
                m_PixelCount = json.getInt("pixelCount");
            }
            if(json.has("groupCount") ) {
                m_GroupCount = json.getInt("groupCount");
            }
            if(json.has("reverse") ) {
                m_Reverse = json.getInt("reverse");
            }
            if(json.has("colorOrder") ) {
                m_ColorOrder = json.getString("colorOrder");
            }
            if(json.has("nullNodes") ) {
                m_NullNodes = json.getInt("nullNodes");
            }
            if(json.has("zigZag") ) {
                m_ZigZag = json.getInt("zigZag");
            }
            if(json.has("brightness") ) {
                m_Brightness = json.getInt("brightness");
            }
            if(json.has("gamma") ) {
                m_Gamma = Double.parseDouble(json.getString("gamma"));
            }

        } catch(JSONException ex) {

        }
    }

    public String toString(){
        String returnValue = m_Description;
        if(m_ExpansionType!= 0){
            returnValue +=":";
            returnValue += m_SmartRemote;
        }
        return returnValue;
    }
}

package com.example.fpptest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FPPModel {

    private String m_Name = "";
    private int m_StartChannel = 1;
    private int m_ChannelCount = 1;

    public FPPModel() {
    }

    public FPPModel(JSONObject json) {
        readJson(json);
    }

    public String getName() {
        return m_Name;
    }

    public int getStartChannel() {
        return m_StartChannel;
    }

    public int getChannelCount() {
        return m_ChannelCount;
    }

    public int getEndChannel() {
        return m_StartChannel + m_ChannelCount;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof FPPModel)) {
            return false;
        }
        FPPModel other = (FPPModel) obj;
        return this.getName().equals( other.getName()) ;
    }

    @Override
    public String toString() {
         return  m_Name ;
    }

    void readJson(JSONObject json) {
        try {
            if(json.has("Name") ) {
                m_Name = json.getString("Name");
            }

            if (json.has("StartChannel") ) {
                m_StartChannel = json.getInt("StartChannel");
            }

            if (json.has("ChannelCount") ) {
                m_ChannelCount = json.getInt("ChannelCount");
            }

        } catch(JSONException ex) {

        }
    }
}

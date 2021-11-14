package com.example.fpptest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PixelOutput implements FPPOutput {

    public int getPortNumber() {
        return m_PortNumber;
    }

    public String getPortNumberStr() {
        return String.format("Pixel Port:%d",m_PortNumber + 1);
    }

    public String getPortModelStr() {
        String returnValue = "";

        for (VirtualString string : m_strings) {
            returnValue += string.toString();
            returnValue +=",";
        }
        if(returnValue.length() > 0){
            returnValue = returnValue.substring(0, returnValue.length() - 1);
        }
        return returnValue;
    }

    public String getProtocol() {
        return m_Protocol;
    }

    public int getExpansionType() {
        return m_ExpansionType;
    }

    public List<VirtualString> getStrings() {
        return m_strings;
    }

    private int m_PortNumber = -1;
    private String m_Protocol = "";
    private int m_ExpansionType = -1;
    private List<VirtualString> m_strings = new ArrayList<VirtualString>();

    public PixelOutput() {
    }

    public PixelOutput(JSONObject json) {
        readJson(json);
    }

    void readJson(JSONObject json) {
        try {

            if(json.has("portNumber") ) {
                m_PortNumber = json.getInt("portNumber");
            }
            if(json.has("protocol") ) {
                m_Protocol = json.getString("protocol");
            }
            if(json.has("expansionType") ) {
                m_ExpansionType = json.getInt("expansionType");
            }

            for (int i = 0; i < 6; i++) {
                String key = String.format("virtualStrings%d", i);
                if(i == 0) {key = "virtualStrings";}

                if(json.has(key) ) {
                    if (json.get(key) instanceof JSONArray) {
                        JSONArray strings = json.getJSONArray(key);
                        for(int j = 0; j < strings.length(); ++j) {
                            VirtualString vs = new VirtualString(strings.getJSONObject(i));
                            m_strings.add(vs);
                        }
                    }
                } else
                {
                    break;
                }
            }

        } catch(JSONException ex) {

        }
    }

    public String toString()
    {
        String returnValue = String.format("Pixel Port:%d",m_PortNumber + 1);

        for (VirtualString string : m_strings) {

            returnValue +=",";
            returnValue += string.toString();
        }
        return returnValue;
    }
}

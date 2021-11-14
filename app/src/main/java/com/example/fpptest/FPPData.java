package com.example.fpptest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class FPPData implements Serializable {

    private String m_IP = "";
    private String m_Host = "";
    private String m_Version = "";
    private String m_Mode = "";
    private String m_Platform = "";
    private String m_Status = "";
    private String m_GitBranch = "";
    private String m_FPPD = "";
    private int m_VerMajor = -1;
    private int m_VerMinor = -1;
    private int m_typeId = 0;

    public FPPData() {
    }

    public FPPData(JSONObject json) {
        readJson(json);
    }

    public String getIP() {
        return m_IP;
    }

    public String getHost() {
        return m_Host;
    }

    public String getVersion() {
        return m_Version;
    }

    public String getMode() {
        return m_Mode;
    }

    public String getPlatform() {
        return m_Platform;
    }

    public String getStatus() {
        return m_Status;
    }

    public void setStatus(String status) {
        m_Status = status;
    }

    public String getFPPD() {
        return m_FPPD;
    }

    public int getVerMajor() {
        return m_VerMajor;
    }

    public int getVerMinor() {
        return m_VerMinor;
    }

    public int getTypeID() {
        return m_typeId;
    }

    public boolean IsFPPDevice() {
        return m_typeId > 0x00 && m_typeId < 0x7F;
    }

    public boolean IsPlayer() {
        return m_Mode.equalsIgnoreCase("player") || m_Mode.equalsIgnoreCase("master");
    }

    public String getPrettyVersion() {
        return String.format("%d.%d", m_VerMajor, m_VerMinor);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof FPPData)) {
            return false;
        }
        FPPData other = (FPPData) obj;
        return this.getHost().equals( other.getHost()) && this.getIP().equals(other.getIP()) ;
    }

    @Override
    public String toString() {
        //return "IP:" + m_IP + " Host:" + m_Host + " Ver:" + m_VerMajor + "." + m_VerMinor + " Type:" + m_Platform ;
        return "IP:" + m_IP + " Mode:" + m_Mode + " Ver:" + m_VerMajor + "." + m_VerMinor + " Type:" + m_Platform ;
    }

    void readJson(JSONObject json) {
        try {
            if(json.has("HostName") ) {
                m_Host = json.getString("HostName");
            }

            if (json.has("Version") ) {
                m_Version = json.getString("Version");
            }

            if (json.has("version") ) {
                m_Version = json.getString("version");
            }

            if (json.has("fppMode") ) {
                m_Mode =json.getString("fppMode");
            }

            if (json.has("Mode") ) {
                m_Mode = json.getString("Mode");
            }

            if (json.has("Platform") ) {
                m_Platform = json.getString("Platform");
            }

            if (json.has("Branch") ) {
                m_GitBranch = json.getString("Branch");
            }

            if (json.has("majorVersion") ) {
                m_VerMajor = json.getInt("majorVersion");
            }

            if (json.has("minorVersion") ) {
                m_VerMinor = json.getInt("minorVersion");
            }

            if (json.has("typeId") ) {
                m_typeId = json.getInt("typeId");
            }

            if (json.has("IPs") ) {
                if (json.get("IPs") instanceof JSONArray) {
                    m_IP = json.getJSONArray("IPs").getString(0);
                }
            }
            if (json.has("IP")) {
                m_IP = json.getString("IP");
            }
        } catch(JSONException ex) {

        }
    }
}

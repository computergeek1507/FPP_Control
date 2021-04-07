package com.example.fpptest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FPPData{

    private String m_IP;
    private String m_Host;
    private String m_Version;
    private String m_Mode;
    private String m_Platform;
    private String m_Status;
    private String m_FPPD;
    private int m_VerMajor = -1;
    private int m_VerMinor = -1;

    public FPPData()
    {

    }

    public FPPData(String json)
    {
        readJson(json);
    }

    public FPPData(JSONObject json)
    {
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

    public String getFPPD() {
        return m_FPPD;
    }

    public int getVerMajor() {
        return m_VerMajor;
    }

    public int getVerMinor() {
        return m_VerMinor;
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
       //return false;
    }

    /*@Override
    public int compareTo(FPPData arg0) {

        return this.getIP().compareTo(arg0.getIP());
    }*/

    @Override
    public String toString() {
        return "IP:" + m_IP + " Host:" + m_Host + " Ver:" + m_VerMajor + "." + m_VerMinor + " Type:" + m_Platform ;
    }

    void readJson(String json) {
        try {
            JSONObject reader = new JSONObject(json);
            readJson(reader);
        }
        catch (JSONException ex) {

        }
    }

    void readJson(JSONObject json)
    {
        try {
            if(json.has("HostName") ){
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

            if (json.has("majorVersion") ) {
                m_VerMajor = json.getInt("majorVersion");
            }

            if (json.has("minorVersion") ) {
                m_VerMinor = json.getInt("minorVersion");
            }
            /*
            if (intervention instanceof JSONArray) {
                // It's an array
                interventionJsonArray = (JSONArray)intervention;
            }
            else if (intervention instanceof JSONObject) {
                // It's an object
                interventionObject = (JSONObject)intervention;
            }*/

            if (json.has("IPs") )
            {
                if (json.get("IPs") instanceof JSONArray) {
                    m_IP = json.getJSONArray("IPs").getString(0);
                }
            }


            if (json.has("IP")) {
                m_IP = json.getString("IP");
            }
        }
        catch(JSONException ex)
        {

        }
    }
}

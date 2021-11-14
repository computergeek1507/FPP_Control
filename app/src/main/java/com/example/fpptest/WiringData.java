package com.example.fpptest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WiringData {

    //public List<PixelOutput> getPixelOutputs() {
    //    return m_pixelOutputs;
    //}

   // public List<SerialOutput> getSerialOutputs() {
   //     return m_serialOutputs;
   // }

   // public List<OtherOutput> getOtherOutputs() {
  //      return m_otherOutputs;
   // }

    //List<PixelOutput> m_pixelOutputs = new ArrayList<PixelOutput>();

    //List<SerialOutput> m_serialOutputs = new ArrayList<SerialOutput>();

    //List<OtherOutput> m_otherOutputs = new ArrayList<OtherOutput>();

    public List<FPPOutput> getOutputs() {
              return m_outputs;
    }

    List<FPPOutput> m_outputs = new ArrayList<FPPOutput>();

    public WiringData() {
    }

    public WiringData(JSONObject json) {
        readJson(json);
    }

    void readJson(JSONObject json) {
        try {
            if (json.has("channelOutputs") ) {
                if (json.get("channelOutputs") instanceof JSONArray) {
                    JSONArray channelOutputs = json.getJSONArray("channelOutputs");

                    for(int j = 0; j < channelOutputs.length(); ++j) {
                        JSONObject channelOutput = channelOutputs.getJSONObject(j);
                        String type = channelOutput.getString("type");
                        if(type.equalsIgnoreCase("BBB48String") || type.equalsIgnoreCase("RPIWS281x") ) {
                            if (channelOutput.has("outputs") ) {
                                if (channelOutput.get("outputs") instanceof JSONArray) {
                                    JSONArray outputs = channelOutput.getJSONArray("outputs");
                                    for(int i = 0; i < outputs.length(); ++i) {
                                        m_outputs.add(new PixelOutput(outputs.getJSONObject(i)));
                                    }
                                }
                            }
                        } else if(type.equalsIgnoreCase("BBBSerial")) {
                            if (channelOutput.has("outputs") ) {
                                if (channelOutput.get("outputs") instanceof JSONArray) {
                                    JSONArray outputs = channelOutput.getJSONArray("outputs");
                                    for(int i = 0; i < outputs.length(); ++i) {
                                        m_outputs.add(new SerialOutput(outputs.getJSONObject(i)));
                                    }
                                }
                            }
                        } else if(type.equalsIgnoreCase("LEDPanelMatrix")) {
                            m_outputs.add(new MatrixOutput(channelOutput));
                        } else {
                            m_outputs.add(new OtherOutput(channelOutput));
                        }
                    }
                }
            }
        } catch(JSONException ex) {

        }
    }
}

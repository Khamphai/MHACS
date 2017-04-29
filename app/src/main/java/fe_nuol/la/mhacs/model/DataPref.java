package fe_nuol.la.mhacs.model;

import android.content.Context;
import android.content.SharedPreferences;

public class DataPref {

    private static final String MY_PREFERENCE = "data";
    private static final String HUMIDITY = "HUMIDITY";
    private static final String TEMPERATURE = "TEMPERATURE";
    private static final String DATETIME = "DATETIME";
    private static final String LOGS = "LOGS";
    private static final String PINCODE = "PINCODE";
    private static final String USERNAME = "USERNAME";

    private SharedPreferences pref;
    private SharedPreferences.Editor prefEditor;

    private static DataPref instance;

    public static DataPref getInstance() {
        if (instance == null) {
            instance = new DataPref();
        }
        return instance;
    }

    private Context mContext;

    public DataPref() {
        mContext = Contextor.getInstance().getContext();
        pref = mContext.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        prefEditor = pref.edit();
    }


    public float getHumidity() {
        return pref.getFloat(HUMIDITY, 0);
    }

    public float getTemperature() {
        return pref.getFloat(TEMPERATURE, 0);
    }

    public String getDateTime() {
        return pref.getString(DATETIME, "N/A");
    }

    public String getLogs() {
        return pref.getString(LOGS, null);
    }

    public String getPincode() {
        return pref.getString(PINCODE, "N/A");
    }

    public String getUsername() {
        return pref.getString(USERNAME, "N/A");
    }

    public void setHumidity(float humidity) {
        prefEditor.putFloat(HUMIDITY, humidity).commit();
    }

    public void setTemperature(float temperature) {
        prefEditor.putFloat(TEMPERATURE, temperature).commit();
    }

    public void setDateTime(String dateTime) {
        prefEditor.putString(DATETIME, dateTime).commit();
    }

    public void setLogs(String logs) {
        prefEditor.putString(LOGS, logs).commit();
    }

    public void setPincode(String pincode) {
        prefEditor.putString(PINCODE, pincode).commit();
    }

    public void setUsername(String username) {
        prefEditor.putString(USERNAME, username).commit();
    }



//    Gson gson = new Gson();
//    String json = gson.toJson(billDetailsDAO);
//    dataPref.setLastBillSale(json);

} //end SavePref

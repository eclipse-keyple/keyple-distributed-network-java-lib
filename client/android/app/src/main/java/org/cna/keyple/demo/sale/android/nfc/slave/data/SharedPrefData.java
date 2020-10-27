/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data;

import javax.inject.Inject;
import org.cna.keyple.demo.sale.android.nfc.slave.di.scopes.AppScoped;
import android.content.SharedPreferences;

@AppScoped
public class SharedPrefData {

    private static final String SERVER_IP_KEY = "server_ip_key";
    private static final String SERVER_PORT_KEY = "server_port_key";
    private static final String SERVER_PROTOCOL_KEY = "server_protocol_key";
    private static final String DEVICE_TYPE = "device_type";

    private static final String DEFAULT_SERVER_IP_KEY = "192.168.11.179";
    // private static final String DEFAULT_SERVER_IP_KEY = "192.168.0.14";
    // private static final String DEFAULT_SERVER_IP_KEY = "192.168.1.5";
    private static final Integer DEFAULT_PORT = 8881;
    private static final String DEFAULT_PROTOCOL = "http://";

    SharedPreferences prefs;

    @Inject
    public SharedPrefData(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public void saveServerIP(String serverIp) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SERVER_IP_KEY, serverIp);
        editor.apply();
    }

    public String loadServerIP() {
        return prefs.getString(SERVER_IP_KEY, DEFAULT_SERVER_IP_KEY);
    }

    public void saveServerPort(Integer serverPort) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SERVER_PORT_KEY, serverPort);
        editor.apply();
    }

    public Integer loadServerPort() {
        return prefs.getInt(SERVER_PORT_KEY, DEFAULT_PORT);
    }

    public void saveServerProtocol(String serverProtocol) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SERVER_PROTOCOL_KEY, serverProtocol);
        editor.apply();
    }

    public String loadServerProtocol() {
        return prefs.getString(SERVER_PROTOCOL_KEY, DEFAULT_PROTOCOL);
    }

    public void saveDeviceType(String deviceType) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(DEVICE_TYPE, deviceType);
        editor.apply();
    }

    public String loadDeviceType() {
        return prefs.getString(DEVICE_TYPE, "");
    }
}

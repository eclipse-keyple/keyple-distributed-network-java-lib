/*
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 *
 * See the NOTICE file(s) distributed with this work for additional information
 * regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.keyple.demo.remote.data

import android.content.SharedPreferences
import javax.inject.Inject
import org.eclipse.keyple.demo.remote.di.scopes.AppScoped

@AppScoped
class SharedPrefData @Inject constructor(var prefs: SharedPreferences) {
    fun saveServerIP(serverIp: String?) {
        val editor = prefs.edit()
        editor.putString(SERVER_IP_KEY, serverIp)
        editor.apply()
    }

    fun loadServerIP(): String? {
        return prefs.getString(SERVER_IP_KEY, DEFAULT_SERVER_IP_KEY)
    }

    fun saveServerPort(serverPort: Int?) {
        val editor = prefs.edit()
        editor.putInt(SERVER_PORT_KEY, serverPort!!)
        editor.apply()
    }

    fun loadServerPort(): Int {
        return prefs.getInt(SERVER_PORT_KEY, DEFAULT_PORT)
    }

    fun saveServerProtocol(serverProtocol: String?) {
        val editor = prefs.edit()
        editor.putString(SERVER_PROTOCOL_KEY, serverProtocol)
        editor.apply()
    }

    fun loadServerProtocol(): String? {
        return prefs.getString(SERVER_PROTOCOL_KEY, DEFAULT_PROTOCOL)
    }

    fun saveDeviceType(deviceType: String?) {
        val editor = prefs.edit()
        editor.putString(DEVICE_TYPE, deviceType)
        editor.apply()
    }

    fun loadDeviceType(): String? {
        return prefs.getString(DEVICE_TYPE, "")
    }

    fun saveContactlessConfigurationVisibility(visibility: Visibility){
        val editor = prefs.edit()
        editor.putString(SETTING_CONTACTLESS_VISIBILITY, visibility.text)
        editor.apply()
    }

    fun loadContactlessConfigurationVisibility(): Visibility {
        return Visibility.valueOf(prefs.getString(SETTING_CONTACTLESS_VISIBILITY, Visibility.ENABLE.text)!!
            .toUpperCase())
    }

    fun saveSimConfigurationVisibility(visibility: Visibility) {
        val editor = prefs.edit()
        editor.putString(SETTING_SIM_VISIBILITY, visibility.text)
        editor.apply()
    }

    fun loadSimConfigurationVisibility(): Visibility {
        return Visibility.valueOf(prefs.getString(SETTING_SIM_VISIBILITY, Visibility.ENABLE.text)!!
            .toUpperCase())
    }

    fun saveWearableConfigurationVisibility(visibility: Visibility) {
        val editor = prefs.edit()
        editor.putString(SETTING_WEARABLE_VISIBILITY, visibility.text)
        editor.apply()
    }

    fun loadWearableConfigurationVisibility(): Visibility {
        return Visibility.valueOf(prefs.getString(SETTING_WEARABLE_VISIBILITY, Visibility.ENABLE.text)!!
            .toUpperCase())
    }

    fun saveEmbeddedConfigurationVisibility(visibility: Visibility) {
        val editor = prefs.edit()
        editor.putString(SETTING_EMBEDDED_VISIBILITY, visibility.text)
        editor.apply()
    }

    fun loadEmbeddedConfigurationVisibility(): Visibility {
        return Visibility.valueOf(prefs.getString(SETTING_EMBEDDED_VISIBILITY, Visibility.ENABLE.text)!!
            .toUpperCase())
    }

    companion object {
        private const val SERVER_IP_KEY = "server_ip_key"
        private const val SERVER_PORT_KEY = "server_port_key"
        private const val SERVER_PROTOCOL_KEY = "server_protocol_key"
        private const val DEVICE_TYPE = "device_type"
        private const val DEFAULT_SERVER_IP_KEY = "192.168.11.179"

        // private static final String DEFAULT_SERVER_IP_KEY = "192.168.0.14";
        // private static final String DEFAULT_SERVER_IP_KEY = "192.168.1.5";
        private const val DEFAULT_PORT = 8881
        private const val DEFAULT_PROTOCOL = "http://"

        private const val SETTING_CONTACTLESS_VISIBILITY = "setting_contactless_visibility"
        private const val SETTING_SIM_VISIBILITY = "setting_sim_visibility"
        private const val SETTING_WEARABLE_VISIBILITY = "setting_wearable_visibility"
        private const val SETTING_EMBEDDED_VISIBILITY = "setting_embedded_visibility"

        enum class Visibility constructor(val text: String) { ENABLE("enable"), DISABLE("disable"), HIDE("hide")}
    }
}
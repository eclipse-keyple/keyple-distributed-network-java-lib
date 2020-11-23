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

package org.eclipse.keyple.demo.remote.ui

import android.content.Intent
import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_server_settings.restart
import org.eclipse.keyple.demo.remote.R

class ServerSettingsActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_settings)

        restart.setOnClickListener { restartApp() }
    }

    private fun restartApp() {
        startActivity(
            Intent(applicationContext, SplashScreenActivity::class.java)
        )
        System.exit(0)
    }
}
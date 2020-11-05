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
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_payment_validated.animation
import kotlinx.android.synthetic.main.activity_payment_validated.chargeBtn
import org.eclipse.keyple.demo.remote.R
import org.eclipse.keyple.demo.remote.data.SharedPrefData
import org.eclipse.keyple.demo.remote.data.model.DeviceEnum
import org.eclipse.keyple.demo.remote.data.model.DeviceEnum.Companion.getDeviceEnum

class PaymentValidatedActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var prefData: SharedPrefData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_validated)

        val device = getDeviceEnum(prefData.loadDeviceType()!!)
        val ticketNumber: Int = intent.getIntExtra(TICKETS_NUMBER, 0)
        chargeBtn.text = getString(R.string.load_card)
        chargeBtn.setOnClickListener {
            val intent = Intent(this, ChargeActivity::class.java)
            intent.putExtra(TICKETS_NUMBER, ticketNumber)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        animation.setAnimation("tick_anim.json")
        animation.repeatCount = 0
        animation.playAnimation()
    }

    companion object {
        const val TICKETS_NUMBER = "ticketsNumber"
    }
}
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
import kotlinx.android.synthetic.main.activity_checkout.selectionLabel
import kotlinx.android.synthetic.main.activity_checkout.selectionPrice
import kotlinx.android.synthetic.main.activity_checkout.validateBtn
import org.eclipse.keyple.demo.remote.R

class CheckoutActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        val ticketNumberCount: Int = intent.getIntExtra(TICKETS_NUMBER, 0)
        val seasonPass: Boolean = intent.getBooleanExtra(SEASON_PASS, false)

        if(seasonPass){
            selectionLabel.text =
                getString(R.string.season_pass_title)
            selectionPrice.text = getString(R.string.ticket_price, 20)
        } else {
            selectionLabel.text =
                resources.getQuantityString(
                    R.plurals.x_tickets,
                    ticketNumberCount,
                    ticketNumberCount
                )
            selectionPrice.text = getString(R.string.ticket_price, ticketNumberCount)
        }
        validateBtn.setOnClickListener {
            val intent = Intent(this, PaymentValidatedActivity::class.java)
            intent.putExtra(TICKETS_NUMBER, ticketNumberCount)
            startActivity(intent)
        }
    }

    companion object {
        const val TICKETS_NUMBER = "ticketsNumber"
        const val SEASON_PASS = "seasonPass"
    }
}
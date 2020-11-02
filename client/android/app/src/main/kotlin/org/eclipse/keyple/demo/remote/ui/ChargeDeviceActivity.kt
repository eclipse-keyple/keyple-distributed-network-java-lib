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
import com.airbnb.lottie.LottieDrawable
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_charge_card.caseFail
import kotlinx.android.synthetic.main.activity_charge_card.caseSuccess
import kotlinx.android.synthetic.main.activity_charge_card.loadingAnimation
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.CardReaderResponse
import org.eclipse.keyple.demo.remote.data.model.Status
import org.eclipse.keyple.demo.remote.R
import org.eclipse.keyple.demo.remote.di.scopes.ActivityScoped

@ActivityScoped
class ChargeDeviceActivity : DaggerAppCompatActivity() {
    var ticketNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charge_device)
        ticketNumber = intent.getIntExtra(PaymentValidatedActivity.TICKETS_NUMBER, 0)

        caseSuccess.setOnClickListener {
            changeDisplay(
                CardReaderResponse(
                    Status.SUCCESS,
                    5,
                    "",
                    "",
                    ""
                )
            )
        }

        caseFail.setOnClickListener {
            changeDisplay(
                CardReaderResponse(
                    Status.ERROR,
                    0,
                    "",
                    "",
                    ""
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        loadingAnimation.setAnimation("loading_anim.json")
        loadingAnimation.repeatCount = LottieDrawable.INFINITE
        loadingAnimation.playAnimation()
    }

    fun changeDisplay(cardReaderResponse: CardReaderResponse?) {
        if (cardReaderResponse != null) {
            if (cardReaderResponse.status != Status.LOADING) {
                loadingAnimation.cancelAnimation()
                val intent = Intent(this, ChargeResultActivity::class.java)
                intent.putExtra(ChargeResultActivity.TICKETS_NUMBER, ticketNumber)
                intent.putExtra(ChargeResultActivity.STATUS, cardReaderResponse.status.toString())
                startActivity(intent)
            }
        }
    }
}
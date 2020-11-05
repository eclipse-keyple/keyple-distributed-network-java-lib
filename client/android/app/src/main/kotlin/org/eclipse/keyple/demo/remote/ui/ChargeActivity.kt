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
import android.view.View
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_card_reader.cardAnimation
import kotlinx.android.synthetic.main.activity_card_reader.loadingAnimation
import kotlinx.android.synthetic.main.activity_card_reader.presentTxt
import kotlinx.android.synthetic.main.activity_charge.caseFail
import kotlinx.android.synthetic.main.activity_charge.caseSuccess
import org.eclipse.keyple.demo.remote.R
import org.eclipse.keyple.demo.remote.data.SharedPrefData
import org.eclipse.keyple.demo.remote.data.model.CardReaderResponse
import org.eclipse.keyple.demo.remote.data.model.DeviceEnum
import org.eclipse.keyple.demo.remote.data.model.Status
import org.eclipse.keyple.demo.remote.di.scopes.ActivityScoped

@ActivityScoped
class ChargeActivity : DaggerAppCompatActivity() {
    var ticketNumber = 0

    @Inject
    lateinit var prefData: SharedPrefData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charge)
        ticketNumber = intent.getIntExtra(PaymentValidatedActivity.TICKETS_NUMBER, 0)

        if (DeviceEnum.getDeviceEnum(prefData.loadDeviceType()!!) != DeviceEnum.CONTACTLESS_CARD) {
            changeDisplay(
                CardReaderResponse(
                    Status.LOADING,
                    "",
                    0,
                    arrayListOf(),
                    arrayListOf(),
                    ""
                )
            )
        }

        caseSuccess.setOnClickListener {
            changeDisplay(
                CardReaderResponse(
                    Status.SUCCESS,
                    "",
                    5,
                    arrayListOf(),
                    arrayListOf(),
                    ""
                )
            )
        }

        caseFail.setOnClickListener {
            changeDisplay(
                CardReaderResponse(
                    Status.ERROR,
                    "",
                    0,
                    arrayListOf(),
                    arrayListOf(),
                    ""
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        cardAnimation.playAnimation()
    }

    private fun changeDisplay(cardReaderResponse: CardReaderResponse?) {
        if (cardReaderResponse != null) {
            if (cardReaderResponse.status == Status.LOADING) {
                loadingAnimation.visibility = View.VISIBLE
                loadingAnimation.playAnimation()
                cardAnimation.cancelAnimation()
                cardAnimation.visibility = View.GONE
                presentTxt.text = getString(R.string.read_in_progress)
            } else {
                loadingAnimation.cancelAnimation()
                cardAnimation.cancelAnimation()
                val intent = Intent(this, ChargeResultActivity::class.java)
                intent.putExtra(ChargeResultActivity.TICKETS_NUMBER, ticketNumber)
                intent.putExtra(ChargeResultActivity.STATUS, cardReaderResponse.status.toString())
                startActivity(intent)
            }
        }
    }
}
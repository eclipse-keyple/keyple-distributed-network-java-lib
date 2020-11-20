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
import com.airbnb.lottie.LottieDrawable
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_card_reader.caseEmpty
import kotlinx.android.synthetic.main.activity_card_reader.caseInvalid
import kotlinx.android.synthetic.main.activity_card_reader.caseValid
import kotlinx.android.synthetic.main.activity_card_reader.loadingAnimation
import kotlinx.android.synthetic.main.activity_card_reader.presentTxt
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.CardReaderResponse
import org.eclipse.keyple.demo.remote.data.model.Status
import org.eclipse.keyple.demo.remote.R
import org.eclipse.keyple.demo.remote.di.scopes.ActivityScoped

@ActivityScoped
class CardReaderActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_reader)

        caseEmpty.setOnClickListener {
            changeDisplay(
                CardReaderResponse(
                    Status.EMPTY_CARD,
                    0,
                    "",
                    "",
                    ""
                )
            )
        }

        caseInvalid.setOnClickListener {
            changeDisplay(
                CardReaderResponse(
                    Status.INVALID_CARD,
                    0,
                    "invalid card",
                    "",
                    ""
                )
            )
        }

        caseValid.setOnClickListener {
            changeDisplay(
                CardReaderResponse(
                    Status.TICKETS_FOUND,
                    5,
                    "",
                    "Place d'italie - 12/10/20 18:05",
                    "SEASON valid until 31/01/2022"
                )
            )
        }
    }

    private fun changeDisplay(cardReaderResponse: CardReaderResponse?) {
        if (cardReaderResponse != null) {
            if (cardReaderResponse.status == Status.LOADING) {
                loadingAnimation.setAnimation("loading_anim.json")
                loadingAnimation.playAnimation()
                loadingAnimation.repeatCount = LottieDrawable.INFINITE
                presentTxt.visibility = View.INVISIBLE
            } else {
                loadingAnimation.cancelAnimation()
                val intent = Intent(this, CardSummaryActivity::class.java)
                intent.putExtra(CardSummaryActivity.STATUS_KEY, cardReaderResponse.status.toString())
                intent.putExtra(CardSummaryActivity.TICKETS_KEY, cardReaderResponse.ticketsNumber)
                intent.putExtra(CardSummaryActivity.CARD_TYPE, cardReaderResponse.cardType)
                intent.putExtra(CardSummaryActivity.LAST_VALIDATION, cardReaderResponse.lastValidation)
                intent.putExtra(CardSummaryActivity.SEASON_PASS_EXPIRY_DATE, cardReaderResponse.seasonPassExpiryDate)
                startActivity(intent)
            }
        }
    }
}
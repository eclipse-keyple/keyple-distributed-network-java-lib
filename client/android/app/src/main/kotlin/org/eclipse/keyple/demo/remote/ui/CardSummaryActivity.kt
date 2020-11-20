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
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_card_summary.animation
import kotlinx.android.synthetic.main.activity_card_summary.bigText
import kotlinx.android.synthetic.main.activity_card_summary.buyBtn
import kotlinx.android.synthetic.main.activity_card_summary.cardContentSummary
import kotlinx.android.synthetic.main.activity_card_summary.lastValidationContent
import kotlinx.android.synthetic.main.activity_card_summary.lastValidationText
import kotlinx.android.synthetic.main.activity_card_summary.seasonPassSummary
import kotlinx.android.synthetic.main.activity_card_summary.smallDesc
import kotlinx.android.synthetic.main.activity_card_summary.titleSummary
import org.eclipse.keyple.demo.remote.data.model.Status
import org.eclipse.keyple.demo.remote.R
import org.eclipse.keyple.demo.remote.data.SharedPrefData
import org.eclipse.keyple.demo.remote.data.model.DeviceEnum.Companion.getDeviceEnum
import timber.log.Timber

class CardSummaryActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var prefData: SharedPrefData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_summary)

        val status = Status.getStatus(intent.getStringExtra(STATUS_KEY))
        val ticketNumber: Int = intent.getIntExtra(TICKETS_KEY, 0)
        val device = getDeviceEnum(prefData.loadDeviceType()!!)
        val cardType: String? = intent.getStringExtra(CARD_TYPE)
        val lastValidation: String? = intent.getStringExtra(LAST_VALIDATION)
        val seasonPassExpiryDate: String? = intent.getStringExtra(SEASON_PASS_EXPIRY_DATE)

        when (status) {
            Status.INVALID_CARD -> {
                animation.setAnimation("error_orange_anim.json")
                animation.playAnimation()
                bigText.setText(R.string.card_invalid_label)
                bigText.setTextColor(resources.getColor(R.color.orange))
                smallDesc.text = String.format(getString(R.string.card_invalid_desc), cardType)
                smallDesc.setTextColor(resources.getColor(R.color.orange))
                buyBtn.visibility = View.INVISIBLE
                cardContentSummary.visibility = View.GONE
            }
            Status.TICKETS_FOUND, Status.SUCCESS -> {
                cardContentSummary.visibility = View.VISIBLE
                titleSummary.text = resources.getQuantityString(
                    R.plurals.trips_lefts,
                    ticketNumber, ticketNumber
                )
                animation.visibility = View.GONE
                bigText.visibility = View.GONE
                smallDesc.visibility = View.INVISIBLE
                buyBtn.visibility = View.VISIBLE
                displaySeasonAndLastValidation(lastValidation, seasonPassExpiryDate)
            }
            Status.EMPTY_CARD -> {
                animation.setAnimation("error_anim.json")
                animation.playAnimation()
                bigText.text = getString(R.string.no_valid_label, getString(device.textId))
                bigText.setTextColor(resources.getColor(R.color.red))
                smallDesc.visibility = View.INVISIBLE
                buyBtn.visibility = View.VISIBLE
                cardContentSummary.visibility = View.GONE
                displaySeasonAndLastValidation(lastValidation, seasonPassExpiryDate)
            }
            else -> {
                animation.setAnimation("error_anim.json")
                animation.playAnimation()
                bigText.setText(R.string.error_label)
                bigText.setTextColor(resources.getColor(R.color.red))
                smallDesc.visibility = View.INVISIBLE
                buyBtn.visibility = View.INVISIBLE
                cardContentSummary.visibility = View.GONE
            }
        }
        animation.playAnimation()

        // Play sound
        val mp: MediaPlayer = MediaPlayer.create(this, R.raw.reading_sound)
        mp.start()
        buyBtn.setOnClickListener {
            val intent = Intent(this, SelectTicketsActivity::class.java)
            startActivity(intent)
        }
    }

    fun displaySeasonAndLastValidation(lastValidation: String?, seasonPassExpiryDate: String?) {
        Timber.d(
            TAG, "lastValidation:" + lastValidation + " - seasonPassExpiryDate:"
                    + seasonPassExpiryDate
        )
        if (lastValidation != null && lastValidation.isNotEmpty()) {
            lastValidationContent.visibility = View.VISIBLE
            lastValidationText.text = lastValidation
        } else {
            lastValidationContent.visibility = View.GONE
        }
        if (seasonPassExpiryDate != null && seasonPassExpiryDate.isNotEmpty()
            && seasonPassExpiryDate.contains("SEASON")
        ) {
            seasonPassSummary.visibility = View.VISIBLE
            // seasonPassSummary.setText(String.format(getString(R.string.season_pass_valid),
            // seasonPassExpiryDate));
            seasonPassSummary.text = String.format(seasonPassExpiryDate)
        } else {
            seasonPassSummary.visibility = View.GONE
        }
    }

    companion object {
        private val TAG: String = CardSummaryActivity::class.java.simpleName
        const val STATUS_KEY = "status"
        const val TICKETS_KEY = "tickets"
        const val LAST_VALIDATION = "lastValidation"
        const val CARD_TYPE = "cardtype"
        const val SEASON_PASS_EXPIRY_DATE = "seasonPassExpiryDate"
    }
}
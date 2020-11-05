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
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_card_reader.cardAnimation
import kotlinx.android.synthetic.main.activity_card_reader.caseEmpty
import kotlinx.android.synthetic.main.activity_card_reader.caseInvalid
import kotlinx.android.synthetic.main.activity_card_reader.caseValid
import kotlinx.android.synthetic.main.activity_card_reader.loadingAnimation
import kotlinx.android.synthetic.main.activity_card_reader.presentTxt
import org.eclipse.keyple.demo.remote.data.model.Status
import org.eclipse.keyple.demo.remote.R
import org.eclipse.keyple.demo.remote.data.SharedPrefData
import org.eclipse.keyple.demo.remote.data.model.CardReaderResponse
import org.eclipse.keyple.demo.remote.data.model.CardTitle
import org.eclipse.keyple.demo.remote.data.model.DeviceEnum
import org.eclipse.keyple.demo.remote.data.model.Validation
import org.eclipse.keyple.demo.remote.di.scopes.ActivityScoped

@ActivityScoped
class CardReaderActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var prefData: SharedPrefData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_reader)

        if (DeviceEnum.getDeviceEnum(prefData.loadDeviceType()!!) != DeviceEnum.CONTACTLESS_CARD){
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

        // TODO: implement Keyple reader

        // TODO: remove when Keyple implemented
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        caseEmpty.setOnClickListener {
            changeDisplay(
                CardReaderResponse(
                    Status.EMPTY_CARD,
                    "",
                    0,
                    arrayListOf(),
                    arrayListOf(
                        Validation("Titre", "Place d'Italie", parser.parse("2020-05-18T08:27:30")),
                        Validation(
                            "Titre",
                            "Place d'Italie",
                            parser.parse("2020-01-14T09:55:00")
                        )
                    ),
                    ""
                )
            )
        }

        caseInvalid.setOnClickListener {
            changeDisplay(
                CardReaderResponse(
                    Status.INVALID_CARD,
                    "invalid card",
                    0,
                    arrayListOf(),
                    arrayListOf(),
                    ""
                )
            )
        }

        caseValid.setOnClickListener {
            changeDisplay(
                CardReaderResponse(
                    Status.TICKETS_FOUND,
                    "valid card",
                    2,
                    arrayListOf(
                        CardTitle("Multi trip", "2 trips left", true),
                        CardTitle("Season pass", "From 1st April 2020 to 1st August 2020", false)
                    ),
                    arrayListOf(
                        Validation("Titre", "Place d'Italie", parser.parse("2020-05-18T08:27:30")),
                        Validation(
                            "Titre",
                            "Place d'Italie",
                            parser.parse("2020-01-14T09:55:00")
                        )
                    ),
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
                val intent = Intent(this, CardSummaryActivity::class.java)
                intent.putExtra(CardSummaryActivity.CARD_CONTENT, cardReaderResponse)
                startActivity(intent)
            }
        }
    }
}
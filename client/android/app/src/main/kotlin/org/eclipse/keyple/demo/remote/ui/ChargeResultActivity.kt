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
import com.airbnb.lottie.LottieDrawable
import dagger.android.support.DaggerAppCompatActivity
import java.util.Timer
import java.util.TimerTask
import kotlinx.android.synthetic.main.activity_charge_result.animation
import kotlinx.android.synthetic.main.activity_charge_result.bigText
import kotlinx.android.synthetic.main.activity_charge_result.smallDesc
import kotlinx.android.synthetic.main.activity_charge_result.tryBtn
import org.eclipse.keyple.demo.remote.data.model.Status
import org.eclipse.keyple.demo.remote.R

class ChargeResultActivity : DaggerAppCompatActivity() {
    private val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charge_result)
        val status = Status.getStatus(intent.getStringExtra(STATUS))
        val ticketNumber: Int = intent.getIntExtra(TICKETS_NUMBER, 0)
        tryBtn.setOnClickListener { onBackPressed() }
        when (status) {
            Status.LOADING -> {
                animation.setAnimation("loading_anim.json")
                animation.repeatCount = LottieDrawable.INFINITE
                bigText.visibility = View.INVISIBLE
                smallDesc.visibility = View.INVISIBLE
                tryBtn.visibility = View.INVISIBLE
            }
            Status.SUCCESS -> {
                animation.setAnimation("tick_anim.json")
                animation.repeatCount = 0
                animation.playAnimation()
                bigText.setText(R.string.charging_success_label)
                bigText.visibility = View.VISIBLE
                bigText.setTextColor(resources.getColor(R.color.green))
                smallDesc.text = resources.getQuantityString(
                    R.plurals.charging_success_description, ticketNumber, ticketNumber
                )
                smallDesc.setTextColor(resources.getColor(R.color.green))
                smallDesc.visibility = View.VISIBLE
                tryBtn.visibility = View.INVISIBLE
                val intent = Intent(this, HomeActivity::class.java)
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        runOnUiThread {
                            startActivity(intent)
                        }
                    }
                }, RETURN_DELAY_MS.toLong())
            }
            else -> {
                animation.setAnimation("error_anim.json")
                animation.repeatCount = 0
                animation.playAnimation()
                bigText.setText(R.string.transaction_cancelled_label)
                bigText.visibility = View.VISIBLE
                bigText.setTextColor(resources.getColor(R.color.red))
                smallDesc.setText(R.string.transaction_cancelled_desc)
                smallDesc.setTextColor(resources.getColor(R.color.red))
                smallDesc.visibility = View.VISIBLE
                tryBtn.visibility = View.VISIBLE
            }
        }

        // Play sound
        val mp: MediaPlayer = MediaPlayer.create(this, R.raw.reading_sound)
        mp.start()
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    companion object {
        private const val RETURN_DELAY_MS = 10000
        const val TICKETS_NUMBER = "ticketsNumber"
        const val STATUS = "status"
    }
}
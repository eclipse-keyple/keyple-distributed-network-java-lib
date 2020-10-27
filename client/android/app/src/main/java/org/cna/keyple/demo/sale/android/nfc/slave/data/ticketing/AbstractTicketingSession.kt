/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 *
 * See the NOTICE file(s) distributed with this work for additional information regarding copyright
 * ownership.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data.ticketing

import org.eclipse.keyple.calypso.command.sam.SamRevision
import org.eclipse.keyple.calypso.transaction.*
import org.eclipse.keyple.calypso.transaction.PoSecuritySettings.PoSecuritySettingsBuilder
import org.eclipse.keyple.calypso.transaction.PoTransaction.SessionSetting.AccessLevel
import org.eclipse.keyple.core.selection.SeResource
import org.eclipse.keyple.core.selection.SeSelection
import org.eclipse.keyple.core.selection.SelectionsResult
import org.eclipse.keyple.core.seproxy.MultiSeRequestProcessing
import org.eclipse.keyple.core.seproxy.SeReader
import org.eclipse.keyple.core.seproxy.event.ObservableReader
import org.eclipse.keyple.core.seproxy.exception.KeypleReaderException
import org.eclipse.keyple.core.seproxy.protocol.SeCommonProtocols
import timber.log.Timber

abstract class AbstractTicketingSession protected constructor(
        val seReader: SeReader, val samReader: SeReader?) {

    protected lateinit var calypsoPo: CalypsoPo
    protected lateinit var seSelection: SeSelection
    var poTypeName: String? = null
        protected set
    var cardContent: CardContent = CardContent()
        protected set
    protected var currentPoSN: ByteArray? = null
    protected var calypsoPoIndex = 0
    protected lateinit var efEnvironmentHolder: ElementaryFile
    protected lateinit var efEventLog: ElementaryFile
    protected lateinit var efCounter: ElementaryFile
    protected lateinit var efContractParser: ElementaryFile

    protected fun pad(text: String, c: Char, length: Int): String {
        val sb = StringBuffer(length)
        sb.append(text)
        for (i in text.length until length) {
            sb.append(c)
        }
        return sb.toString()
    }

    fun processSelectionsResult(selectionsResult: SelectionsResult) {
        val selectionIndex = selectionsResult.matchingSelections.keys.first()

        if (selectionIndex == calypsoPoIndex) {
            calypsoPo = selectionsResult.activeMatchingSe as CalypsoPo
            poTypeName = "CALYPSO"
            efEnvironmentHolder = calypsoPo.getFileBySfi(CalypsoInfo.SFI_EnvironmentAndHolder)
            efEventLog = calypsoPo.getFileBySfi(CalypsoInfo.SFI_EventLog)
            efCounter = calypsoPo.getFileBySfi(CalypsoInfo.SFI_Counter)
            efContractParser = calypsoPo.getFileBySfi(CalypsoInfo.SFI_Contracts)
        } else {
            poTypeName = "OTHER"
        }
        Timber.i("PO type = $poTypeName")
    }

    val poIdentification: String
        get() = (calypsoPo.applicationSerialNumber + ", " +
                calypsoPo.revision.toString())

    /**
     * initial PO content analysis
     *
     * @return
     */
    fun analyzePoProfile(): Boolean {
        var status = false
        if (calypsoPo.startupInfo != null) {
            currentPoSN = calypsoPo.applicationSerialNumberBytes
            cardContent.serialNumber = currentPoSN
            cardContent.poRevision = calypsoPo.revision.toString()
            cardContent.environment = efEnvironmentHolder.data.allRecordsContent
            cardContent.eventLog = efEventLog.data.allRecordsContent
            cardContent.counters = efCounter.data.allCountersValue
            cardContent.contracts = efContractParser.data.allRecordsContent
            status = true
        }
        return status
    }

    fun notifySeProcessed() {
        (seReader as ObservableReader).finalizeSeProcessing()
    }

    @Throws(KeypleReaderException::class, IllegalStateException::class)
    protected fun checkSamAndOpenChannel(samReader: SeReader): SeResource<CalypsoSam> {
        /*
         * check the availability of the SAM doing a ATR based selection, open its physical and
         * logical channels and keep it open
         */
        val samSelection = SeSelection(MultiSeRequestProcessing.FIRST_MATCH)

        val samSelector = SamSelector.builder()
                .seProtocol(SeCommonProtocols.PROTOCOL_ISO14443_4)
                .samRevision(SamRevision.C1)
                .build()

        samSelection.prepareSelection(SamSelectionRequest(samSelector))

        return try {
            if (samReader.isSePresent) {
                val selectionResult = samSelection.processExplicitSelection(samReader)
                if (selectionResult.hasActiveSelection()) {
                    val calypsoSam = selectionResult.activeMatchingSe as CalypsoSam
                    SeResource(samReader, calypsoSam)
                } else {
                    throw IllegalStateException("Sam selection failed")
                }
            } else {
                throw IllegalStateException("Sam is not present in the reader")
            }
        } catch (e: KeypleReaderException) {
            throw IllegalStateException("Reader exception: " + e.message)
        }
    }

    open fun getSecuritySettings(samResource: SeResource<CalypsoSam>?): PoSecuritySettings? {

        // The default KIF values for personalization, loading and debiting
        val DEFAULT_KIF_PERSO = 0x21.toByte()
        val DEFAULT_KIF_LOAD = 0x27.toByte()
        val DEFAULT_KIF_DEBIT = 0x30.toByte()
        // The default key record number values for personalization, loading and debiting
        // The actual value should be adjusted.
        val DEFAULT_KEY_RECORD_NUMBER_PERSO = 0x01.toByte()
        val DEFAULT_KEY_RECORD_NUMBER_LOAD = 0x02.toByte()
        val DEFAULT_KEY_RECORD_NUMBER_DEBIT = 0x03.toByte()

        /* define the security parameters to provide when creating PoTransaction */
        return PoSecuritySettingsBuilder(samResource) //
                .sessionDefaultKif(AccessLevel.SESSION_LVL_PERSO, DEFAULT_KIF_PERSO) //
                .sessionDefaultKif(AccessLevel.SESSION_LVL_LOAD, DEFAULT_KIF_LOAD) //
                .sessionDefaultKif(AccessLevel.SESSION_LVL_DEBIT, DEFAULT_KIF_DEBIT) //
                .sessionDefaultKeyRecordNumber(
                        AccessLevel.SESSION_LVL_PERSO,
                        DEFAULT_KEY_RECORD_NUMBER_PERSO
                ) //
                .sessionDefaultKeyRecordNumber(
                        AccessLevel.SESSION_LVL_LOAD,
                        DEFAULT_KEY_RECORD_NUMBER_LOAD
                ) //
                .sessionDefaultKeyRecordNumber(
                        AccessLevel.SESSION_LVL_DEBIT,
                        DEFAULT_KEY_RECORD_NUMBER_DEBIT
                )
                .build()
    }
}

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

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Date
import org.eclipse.keyple.calypso.command.po.exception.CalypsoPoCommandException
import org.eclipse.keyple.calypso.command.sam.exception.CalypsoSamCommandException
import org.eclipse.keyple.calypso.transaction.CalypsoPo
import org.eclipse.keyple.calypso.transaction.PoSelectionRequest
import org.eclipse.keyple.calypso.transaction.PoSelector
import org.eclipse.keyple.calypso.transaction.PoTransaction
import org.eclipse.keyple.calypso.transaction.exception.CalypsoPoTransactionException
import org.eclipse.keyple.core.command.AbstractApduCommandBuilder
import org.eclipse.keyple.core.selection.AbstractMatchingSe
import org.eclipse.keyple.core.selection.AbstractSeSelectionRequest
import org.eclipse.keyple.core.selection.SeResource
import org.eclipse.keyple.core.selection.SeSelection
import org.eclipse.keyple.core.selection.SelectionsResult
import org.eclipse.keyple.core.seproxy.MultiSeRequestProcessing
import org.eclipse.keyple.core.seproxy.SeReader
import org.eclipse.keyple.core.seproxy.SeSelector
import org.eclipse.keyple.core.seproxy.event.AbstractDefaultSelectionsResponse
import org.eclipse.keyple.core.seproxy.event.ObservableReader
import org.eclipse.keyple.core.seproxy.exception.KeypleReaderException
import org.eclipse.keyple.core.seproxy.message.SeResponse
import org.eclipse.keyple.core.seproxy.protocol.SeCommonProtocols
import org.eclipse.keyple.core.util.ByteArrayUtil
import timber.log.Timber

class TicketingSession(seReader: SeReader, samReader: SeReader?) :
        AbstractTicketingSession(seReader, samReader), ITicketingSession {
    private var mifareClassicIndex = 0
    private var mifareDesfireIndex = 0
    private var bankingCardIndex = 0
    private var navigoCardIndex = 0

    /*
     * Should be instanciated through the ticketing session mananger
    */
    init {
        prepareAndSetPoDefaultSelection()
    }

    /**
     * prepare the default selection
     */
    fun prepareAndSetPoDefaultSelection() {
        /*
         * Prepare a PO selection
         */
        seSelection = SeSelection(MultiSeRequestProcessing.FIRST_MATCH)

        seSelection.prepareReleaseSeChannel()

        /* Select Calypso */
        val poSelectionRequest = PoSelectionRequest(
                PoSelector.builder()
                        .seProtocol(SeCommonProtocols.PROTOCOL_ISO14443_4)
                        .aidSelector(SeSelector.AidSelector.builder().aidToSelect(CalypsoInfo.AID).build())
                        .invalidatedPo(PoSelector.InvalidatedPo.REJECT).build()
        )

        // Prepare the reading of the Environment and Holder file.
        poSelectionRequest.prepareReadRecordFile(
                CalypsoInfo.SFI_EnvironmentAndHolder,
                CalypsoInfo.RECORD_NUMBER_1.toInt()
        )
        poSelectionRequest.prepareReadRecordFile(
                CalypsoInfo.SFI_Contracts,
                CalypsoInfo.RECORD_NUMBER_1.toInt()
        )
        poSelectionRequest.prepareReadRecordFile(
                CalypsoInfo.SFI_Counter,
                CalypsoInfo.RECORD_NUMBER_1.toInt()
        )
        poSelectionRequest.prepareReadRecordFile(
                CalypsoInfo.SFI_EventLog,
                CalypsoInfo.RECORD_NUMBER_1.toInt()
        )

        /*
         * Add the selection case to the current selection (we could have added other cases here)
         */
        calypsoPoIndex = seSelection.prepareSelection(poSelectionRequest)

        /* Select Mifare Classic PO */
        val mifareClassicSelectionRequest = GenericSeSelectionRequest(
                SeSelector.builder()
                        .seProtocol(SeCommonProtocols.PROTOCOL_MIFARE_CLASSIC)
                        .atrFilter(SeSelector.AtrFilter(".*")).build())

        mifareClassicIndex = seSelection.prepareSelection(mifareClassicSelectionRequest)

        /* Select Mifare Desfire PO */
        val mifareDesfireSelectionRequest = GenericSeSelectionRequest(
                SeSelector.builder()
                        .seProtocol(SeCommonProtocols.PROTOCOL_MIFARE_DESFIRE)
                        .atrFilter(SeSelector.AtrFilter(".*")).build())

        mifareDesfireIndex = seSelection.prepareSelection(mifareDesfireSelectionRequest)

        /*
        * Add the selection case to the current selection
        */
        val bankingCardSelectionRequest = GenericSeSelectionRequest(
                SeSelector.builder()
                        .aidSelector(SeSelector.AidSelector.builder().aidToSelect("325041592e5359532e4444463031").build())
                        .seProtocol(SeCommonProtocols.PROTOCOL_ISO14443_4)
                        .build())

        bankingCardIndex = seSelection.prepareSelection(bankingCardSelectionRequest)

        /*
        * Add the selection case to the current selection
        */
        val navigoCardSelectionRequest = GenericSeSelectionRequest(
                SeSelector.builder()
                        .aidSelector(SeSelector.AidSelector.builder().aidToSelect("A0000004040125090101").build())
                        .seProtocol(SeCommonProtocols.PROTOCOL_ISO14443_4)
                        .build())

        navigoCardIndex = seSelection.prepareSelection(navigoCardSelectionRequest)

        /*
         * Provide the SeReader with the selection operation to be processed when a PO is inserted.
         */
        (seReader as ObservableReader).setDefaultSelectionRequest(
                seSelection.selectionOperation, ObservableReader.NotificationMode.ALWAYS
        )
    }

    fun processDefaultSelection(selectionResponse: AbstractDefaultSelectionsResponse?): SelectionsResult {
        Timber.i("selectionResponse = $selectionResponse")
        val selectionsResult: SelectionsResult =
                seSelection.processDefaultSelection(selectionResponse)
        if (selectionsResult.hasActiveSelection()) {
            when (selectionsResult.matchingSelections.keys.first()) {
                calypsoPoIndex -> {
                    calypsoPo = selectionsResult.activeMatchingSe as CalypsoPo
                    poTypeName = "CALYPSO"
                    efEnvironmentHolder =
                            calypsoPo.getFileBySfi(CalypsoInfo.SFI_EnvironmentAndHolder)
                    efEventLog = calypsoPo.getFileBySfi(CalypsoInfo.SFI_EventLog)
                    efCounter = calypsoPo.getFileBySfi(CalypsoInfo.SFI_Counter)
                    efContractParser = calypsoPo.getFileBySfi(CalypsoInfo.SFI_Contracts)
                }
                mifareClassicIndex -> {
                    poTypeName = "MIFARE Classic"
                }
//                mifareDesfireIndex -> {
//                    poTypeName = "MIFARE Desfire"
//                }
                bankingCardIndex -> {
                    poTypeName = "EMV"
                }
                navigoCardIndex -> {
                    poTypeName = "NAVIGO"
                }
                else -> {
                    poTypeName = "OTHER"
                }
            }
        }
        Timber.i("PO type = $poTypeName")
        return selectionsResult
    }

    /**
     * do the personalization of the PO according to the specified profile
     *
     * @param profile
     * @return
     */
    @Throws(
            CalypsoPoTransactionException::class,
            CalypsoPoCommandException::class,
            CalypsoSamCommandException::class
    )
    fun personalize(profile: String): Boolean {
        try {
            // Should block poTransaction without Sam?
            val poTransaction = if (samReader != null)
                PoTransaction(
                        SeResource(seReader, calypsoPo),
                        getSecuritySettings(checkSamAndOpenChannel(samReader))
                )
            else
                PoTransaction(SeResource(seReader, calypsoPo))
            poTransaction.processOpening(PoTransaction.SessionSetting.AccessLevel.SESSION_LVL_PERSO)

            if ("PROFILE1" == profile) {
                poTransaction.prepareUpdateRecord(
                        CalypsoInfo.SFI_EnvironmentAndHolder,
                        CalypsoInfo.RECORD_NUMBER_1.toInt(),
                        pad("John Smith", ' ', 29).toByteArray()
                )
                poTransaction.prepareUpdateRecord(
                        CalypsoInfo.SFI_Contracts,
                        CalypsoInfo.RECORD_NUMBER_1.toInt(),
                        pad("NO CONTRACT", ' ', 29).toByteArray()
                )
            } else {
                poTransaction.prepareUpdateRecord(
                        CalypsoInfo.SFI_EnvironmentAndHolder,
                        CalypsoInfo.RECORD_NUMBER_1.toInt(),
                        pad("Harry Potter", ' ', 29).toByteArray()
                )
                poTransaction.prepareUpdateRecord(
                        CalypsoInfo.SFI_Contracts,
                        CalypsoInfo.RECORD_NUMBER_1.toInt(),
                        pad("1 MONTH SEASON TICKET", ' ', 29).toByteArray()
                )
            }
            val dateFormat: DateFormat = SimpleDateFormat("yyMMdd HH:mm:ss")
            val dateTime = dateFormat.format(Date())
            poTransaction.prepareAppendRecord(
                    CalypsoInfo.SFI_EventLog,
                    pad("$dateTime OP = PERSO", ' ', 29).toByteArray()
            )
            poTransaction.prepareUpdateRecord(
                    CalypsoInfo.SFI_Counter,
                    CalypsoInfo.RECORD_NUMBER_1.toInt(),
                    ByteArrayUtil.fromHex(pad("", '0', 29 * 2))
            )
            seSelection.prepareReleaseSeChannel()
            return true
        } catch (e: CalypsoPoTransactionException) {
            Timber.e(e)
        } catch (e: CalypsoPoCommandException) {
            Timber.e(e)
        } catch (e: CalypsoSamCommandException) {
            Timber.e(e)
        }
        return false
    }
    /*
     * public void forceCloseChannel() throws KeypleReaderException {
     * logger.debug("Force close logical channel (hack for nfc reader)"); List<ApduRequest>
     * requestList = new ArrayList<>(); ((ProxyReader)poReader).transmit(new
     * SeRequest(requestList)); }
     */
    /**
     * load the PO according to the choice provided as an argument
     *
     * @param ticketNumber
     * @return
     * @throws KeypleReaderException
     */
    @Throws(KeypleReaderException::class)
    override fun loadTickets(ticketNumber: Int): Int {
        return try {
            // Should block poTransaction without Sam?
            val poTransaction = if (samReader != null)
                PoTransaction(
                        SeResource(seReader, calypsoPo),
                        getSecuritySettings(checkSamAndOpenChannel(samReader))
                )
            else
                PoTransaction(SeResource(seReader, calypsoPo))

            if (!Arrays.equals(currentPoSN, calypsoPo.applicationSerialNumberBytes)) {
                Timber.i("Load ticket status  : STATUS_CARD_SWITCHED")
                return ITicketingSession.STATUS_CARD_SWITCHED
            }
            /*
             * Open a transaction to read/write the Calypso PO
             */
            poTransaction.processOpening(PoTransaction.SessionSetting.AccessLevel.SESSION_LVL_LOAD)

            /*
             * Read actual ticket number
             */
            poTransaction.prepareReadRecordFile(
                    CalypsoInfo.SFI_Counter,
                    CalypsoInfo.RECORD_NUMBER_1.toInt()
            )
            poTransaction.processPoCommands()
            poTransaction.prepareIncreaseCounter(
                    CalypsoInfo.SFI_Counter,
                    CalypsoInfo.RECORD_NUMBER_1.toInt(),
                    ticketNumber
            )

            /*
             * Prepare record to be sent to Calypso PO log journal
             */
            val dateFormat: DateFormat = SimpleDateFormat("yyMMdd HH:mm:ss")
            val dateTime = dateFormat.format(Date())
            var event = ""
            event = if (ticketNumber > 0) {
                pad("$dateTime OP = +$ticketNumber", ' ', 29)
            } else {
                pad("$dateTime T1", ' ', 29)
            }
            poTransaction.prepareAppendRecord(CalypsoInfo.SFI_EventLog, event.toByteArray())

            /*
             * Process transaction
             */
            seSelection.prepareReleaseSeChannel()
            Timber.i("Load ticket status  : STATUS_OK")
            ITicketingSession.STATUS_OK
        } catch (e: CalypsoSamCommandException) {
            Timber.e(e)
            ITicketingSession.STATUS_SESSION_ERROR
        } catch (e: CalypsoPoCommandException) {
            Timber.e(e)
            ITicketingSession.STATUS_SESSION_ERROR
        } catch (e: CalypsoPoTransactionException) {
            Timber.e(e)
            ITicketingSession.STATUS_SESSION_ERROR
        }
    }

    fun debitTickets(ticketNumber: Int): Int {
        return try {
            // Should block poTransaction without Sam?
            val poTransaction =
                    if (samReader != null)
                        PoTransaction(
                                SeResource(seReader, calypsoPo),
                                getSecuritySettings(checkSamAndOpenChannel(samReader))
                        )
                    else
                        PoTransaction(SeResource(seReader, calypsoPo))

            /*
             * Open a transaction to read/write the Calypso PO
             */
            poTransaction.processOpening(PoTransaction.SessionSetting.AccessLevel.SESSION_LVL_DEBIT)

            /* allow to determine the anticipated response */
            poTransaction.prepareReadRecordFile(
                    CalypsoInfo.SFI_Counter,
                    CalypsoInfo.RECORD_NUMBER_1.toInt()
            )
            poTransaction.processPoCommands()

            /*
             * Prepare decrease command
             */
            poTransaction.prepareDecreaseCounter(
                    CalypsoInfo.SFI_Counter,
                    CalypsoInfo.RECORD_NUMBER_1.toInt(),
                    1
            )

            /*
            * Process transaction and close session
             */
            poTransaction.processClosing()

            Timber.i("Debit ticket status  : STATUS_OK")
            ITicketingSession.STATUS_OK
        } catch (e: CalypsoSamCommandException) {
            Timber.e(e)
            ITicketingSession.STATUS_SESSION_ERROR
        } catch (e: CalypsoPoCommandException) {
            Timber.e(e)
            ITicketingSession.STATUS_SESSION_ERROR
        } catch (e: CalypsoPoTransactionException) {
            Timber.e(e)
            ITicketingSession.STATUS_SESSION_ERROR
        }
    }

    /**
     * Load a season ticket contract
     *
     * @return
     * @throws KeypleReaderException
     */
    @Throws(KeypleReaderException::class)
    fun loadContract(): Int {
        return try {
            // Should block poTransaction without Sam?
            val poTransaction = if (samReader != null)
                PoTransaction(
                        SeResource(seReader, calypsoPo),
                        getSecuritySettings(checkSamAndOpenChannel(samReader))
                )
            else
                PoTransaction(SeResource(seReader, calypsoPo))

            if (!Arrays.equals(currentPoSN, calypsoPo.applicationSerialNumberBytes)) {
                return ITicketingSession.STATUS_CARD_SWITCHED
            }

            poTransaction.processOpening(PoTransaction.SessionSetting.AccessLevel.SESSION_LVL_LOAD)

            /* allow to determine the anticipated response */
            poTransaction.prepareReadRecordFile(
                    CalypsoInfo.SFI_Counter,
                    CalypsoInfo.RECORD_NUMBER_1.toInt()
            )
            poTransaction.processPoCommands()
            poTransaction.prepareUpdateRecord(
                    CalypsoInfo.SFI_Contracts,
                    CalypsoInfo.RECORD_NUMBER_1.toInt(),
                    pad("1 MONTH SEASON TICKET", ' ', 29).toByteArray()
            )

            // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("");
            // String dateTime = LocalDateTime.now().format(formatter);
            val dateFormat: DateFormat = SimpleDateFormat("yyMMdd HH:mm:ss")
            val event =
                    pad(dateFormat.format(Date()) + " OP = +ST", ' ', 29)
            poTransaction.prepareAppendRecord(CalypsoInfo.SFI_EventLog, event.toByteArray())
            poTransaction.processClosing()
            ITicketingSession.STATUS_OK
        } catch (e: CalypsoSamCommandException) {
            Timber.e(e)
            ITicketingSession.STATUS_SESSION_ERROR
        } catch (e: CalypsoPoCommandException) {
            Timber.e(e)
            ITicketingSession.STATUS_SESSION_ERROR
        } catch (e: CalypsoPoTransactionException) {
            Timber.e(e)
            ITicketingSession.STATUS_SESSION_ERROR
        }
    }

    /**
     * Create a new class extending AbstractSeSelectionRequest
     */
    inner class GenericSeSelectionRequest(seSelector: SeSelector) :
            AbstractSeSelectionRequest<AbstractApduCommandBuilder>(seSelector) {
        override fun parse(seResponse: SeResponse): AbstractMatchingSe {
            class GenericMatchingSe(
                    selectionResponse: SeResponse?
            ) : AbstractMatchingSe(selectionResponse)
            return GenericMatchingSe(seResponse)
        }
    }
}

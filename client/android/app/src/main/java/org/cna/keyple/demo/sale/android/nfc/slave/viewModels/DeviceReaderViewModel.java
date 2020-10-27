/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.viewModels;

import java.io.IOException;
import javax.inject.Inject;
import org.cna.keyple.demo.sale.android.nfc.slave.data.KeypleSlaveAPI;
import org.cna.keyple.demo.sale.android.nfc.slave.data.http.common.ReaderState;
import org.cna.keyple.demo.sale.android.nfc.slave.data.http.common.SaleTransaction;
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.CardReaderResponse;
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.Status;
import org.cna.keyple.demo.sale.android.nfc.slave.di.scopes.AppScoped;
import org.cna.keyple.demo.sale.android.nfc.slave.rx.SchedulerProvider;
import org.cna.keyple.demo.sale.android.nfc.slave.util.LiveEvent;
import org.eclipse.keyple.core.seproxy.SeReader;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AppScoped
public class DeviceReaderViewModel extends ViewModel {
    private final static String TAG = DeviceReaderViewModel.class.getSimpleName();

    private final SchedulerProvider schedulerProvider;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final LiveEvent<CardReaderResponse> readingResponse = new LiveEvent<>();

    String currentTransactionId;

    /*
     * Keyple objects
     */
    @NonNull
    private final KeypleSlaveAPI keypleSlaveAPI;


    /**
     * Constructor
     * 
     * @param keypleSlaveAPI
     * @param provider
     */
    @Inject
    public DeviceReaderViewModel(@NonNull KeypleSlaveAPI keypleSlaveAPI,
            SchedulerProvider provider) {
        this.schedulerProvider = provider;
        this.keypleSlaveAPI = keypleSlaveAPI;

    }

    public SeReader getReader() {
        return keypleSlaveAPI.getWizwayKeypleService().getReader();
    }


    /**
     * Process ReaderState
     */
    public void updateUIonReaderState(ReaderState readerState) {

        Log.d(TAG, "updateUIonReaderState  : " + readerState.toString());

        // get card type
        if (KeypleSlaveAPI.CALYPSO_PO_TYPE.equals(readerState.getPoTypeName())) {
            Log.d(TAG, "PO Serial Number : " + readerState.getCurrentPoSN());
            Log.d(TAG, "cardContent : " + readerState.getCardContent());
            Log.d(TAG, "tickets available : " + readerState.getCardContent().getCounters().get(1));
            Log.d(TAG, "saleTransaction : " + (readerState.getSaleTransaction() == null ? "null"
                    : readerState.getSaleTransaction().toString()));
            // get current transaction
            currentTransactionId = readerState.getSaleTransaction().getId();
            Log.i(TAG, "Receive currentTransactionId : " + currentTransactionId);


            /*
             * the card is valid and a transaction is pending
             */
            if (readerState.getCardContent() != null && readerState.getSaleTransaction() != null) {

                // get current ticket number if available
                Integer tickets = readerState.getCardContent().getCounters().get(1);

                // Get last validation content
                String lastValidationLog = "";
                for (int i = 0; i < readerState.getCardContent().getEventLog().size(); i++) {
                    byte[] l = readerState.getCardContent().getEventLog().get(i + 1);
                    if (l != null) {
                        lastValidationLog += "[" + (-i) + "]: " + new String(l);
                    }
                    if (i < (readerState.getCardContent().getEventLog().size() - 1)) {
                        lastValidationLog += " ";
                    }
                }

                // Get season pass
                String seasonPassExpiryDate;
                try {
                    seasonPassExpiryDate =
                            new String(readerState.getCardContent().getContracts().get(1));
                } catch (NullPointerException e) {
                    seasonPassExpiryDate = "";
                }

                /*
                 * Load is complete
                 */
                if (SaleTransaction.LOAD_COMPLETE
                        .equals(readerState.getSaleTransaction().getStatus())) {
                    setUiResponse(Status.SUCCESS, tickets, KeypleSlaveAPI.CALYPSO_PO_TYPE,
                            lastValidationLog, seasonPassExpiryDate);
                    return;
                }

                /*
                 * Card have been switched
                 */
                if (SaleTransaction.WRONG_CARD_TO_LOAD_TRY_AGAIN
                        .equals(readerState.getSaleTransaction().getStatus())) {
                    setUiResponse(Status.WRONG_CARD, tickets, KeypleSlaveAPI.CALYPSO_PO_TYPE, null,
                            null);
                    return;
                }

                /*
                 * Transaction has not been paid
                 */
                if (SaleTransaction.WAIT_FOR_PAYMENT
                        .equals(readerState.getSaleTransaction().getStatus())) {
                    if ((!seasonPassExpiryDate.isEmpty() && seasonPassExpiryDate.contains("SEASON"))
                            || (tickets != null && tickets != 0)) {
                        setUiResponse(Status.TICKETS_FOUND, tickets, KeypleSlaveAPI.CALYPSO_PO_TYPE,
                                lastValidationLog, seasonPassExpiryDate);
                        return;
                    } else {
                        // setUiResponse(Status.DEVICE_CONNECTED, 0, KeypleSlaveAPI.CALYPSO_PO_TYPE,
                        // lastValidationLog, seasonPassExpiryDate);
                        setUiResponse(Status.EMPTY_CARD, 0, KeypleSlaveAPI.CALYPSO_PO_TYPE,
                                lastValidationLog, seasonPassExpiryDate);
                        return;
                    }
                }

                /* wrong status : transaction is waiting for a card */
                if (SaleTransaction.WAIT_FOR_CARD_TO_LOAD
                        .equals(readerState.getSaleTransaction().getStatus())) {
                    Log.e(TAG, "should not be in this state");
                    setUiResponse(Status.ERROR, 0, KeypleSlaveAPI.CALYPSO_PO_TYPE, null, null);
                    return;
                }

            } else {
                setUiResponse(Status.ERROR, 0, KeypleSlaveAPI.CALYPSO_PO_TYPE, null, null);
                return;
            }
        } else {
            /*
             * Not a CALYPSO CARD
             */
            setUiResponse(Status.INVALID_CARD, 0, readerState.getPoTypeName(), null, null);
            return;
        }


    }

    /**
     * Reset current transaction
     */
    public void resetTransaction() {
        Log.d(TAG, "Clear all current transaction for current reader..");
        try {
            SeReader currentReader = keypleSlaveAPI.getCurrentReader();

            if (currentReader == null) {
                // Reader is not ready
                Log.e(TAG, "currentReader is null, cancel resetTransaction()");
            } else {
                keypleSlaveAPI.resetTransaction(currentReader.getName(), new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        // on success
                        Log.d(TAG, "Transaction deleted for reader "
                                + keypleSlaveAPI.getCurrentReader().getName());
                        return;
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e(TAG, "onFailure resetTransaction()  : " + t.getMessage());
                        return;
                    }
                });
            }

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Charge device with tickets
     * 
     * @param ticketsToLoad
     */
    public void chargeDevice(Integer ticketsToLoad) {
        setUiResponse(Status.LOADING, 0, KeypleSlaveAPI.CALYPSO_PO_TYPE, null, null);
        Log.d(TAG, "chargeDevice for " + ticketsToLoad + " tickets...");
        try {

            String slaveId = keypleSlaveAPI.getSlaveId();

            keypleSlaveAPI.chargeTicket(keypleSlaveAPI.getCurrentReader().getName(), slaveId,
                    currentTransactionId, ticketsToLoad, new Callback<ReaderState>() {
                        @Override
                        public void onResponse(Call<ReaderState> call, Response<ReaderState> res) {
                            if (res.body() == null) {
                                Log.d(TAG, "No state received");
                                return;
                            }

                            ReaderState readerState = res.body();
                            updateUIonReaderState(readerState);

                        }

                        @Override
                        public void onFailure(Call<ReaderState> call, Throwable t) {
                            Log.e(TAG, "onFailure chargeDevice" + t.toString());
                            setUiResponse(Status.ERROR, 0, KeypleSlaveAPI.CALYPSO_PO_TYPE, null,
                                    null);
                        }
                    });


        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.toString());
        }

    }



    /*
     * UI
     */

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

    public LiveEvent<CardReaderResponse> getReadingResponse() {
        return readingResponse;
    }

    /**
     * Generate observable to communicate with UI thread
     *
     * @param status
     * @param tickets
     */
    private void setUiResponse(Status status, Integer tickets, String cardType,
            @Nullable String lastValidation, @Nullable String seasonPassExpiryDate) {
        Log.d(TAG, "Set UI Response : " + status.toString() + " - tickets : " + tickets
                + " - cardType : " + cardType);
        disposables.add(Observable
                .just(new CardReaderResponse(status, tickets, cardType,
                        lastValidation != null ? lastValidation : "",
                        seasonPassExpiryDate != null ? seasonPassExpiryDate : ""))
                .observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.ui())
                .subscribe(readingResponse::setValue));
    }


}

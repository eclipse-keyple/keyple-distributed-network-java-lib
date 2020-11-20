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
import org.eclipse.keyple.demo.remote.data.model.Status;
import org.cna.keyple.demo.sale.android.nfc.slave.util.LiveEvent;
import org.eclipse.keyple.core.seproxy.SeReader;
import org.eclipse.keyple.core.seproxy.event.ObservableReader;
import org.eclipse.keyple.core.seproxy.event.ReaderEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import android.app.Activity;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import org.eclipse.keyple.demo.remote.di.scopes.AppScoped;
import org.eclipse.keyple.demo.remote.rx.SchedulerProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Unique ViewModel to interact with model and keyple objects
 */
@AppScoped
public class CardReaderViewModel extends ViewModel {

    private final static String TAG = CardReaderViewModel.class.getSimpleName();


    @NonNull
    private final KeypleSlaveAPI keypleSlaveAPI;

    private final SchedulerProvider schedulerProvider;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final LiveEvent<CardReaderResponse> readingResponse = new LiveEvent<>();

    private final LiveEvent<Boolean> isCardReaderConnected = new LiveEvent<>();

    public final PoObserver poLogic = new PoObserver();

    private String currentTransactionId;

    @Inject
    public CardReaderViewModel(@NonNull KeypleSlaveAPI keypleSlaveAPI, SchedulerProvider provider) {
        this.schedulerProvider = provider;
        this.keypleSlaveAPI = keypleSlaveAPI;

        // init
        isCardReaderConnected.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // disconnectReaderFromMaster();
        disposables.clear();
    }

    public LiveEvent<CardReaderResponse> getReadingResponse() {
        return readingResponse;
    }

    public LiveEvent<Boolean> isCardReaderConnected() {
        return isCardReaderConnected;
    }

    /**
     * Generate observable to communicate with UI thread
     * 
     * @param status
     * @param tickets
     */
    private void setUiResponse(Status status, Integer tickets, String cardType,
                               @Nullable String lastValidation, @Nullable String seasonPassExpiryDate) {
        Timber.d("Set UI Response : " + status.toString() + " - tickets : " + tickets
                + " - cardType : " + cardType);
        disposables.add(Observable
                .just(new CardReaderResponse(status, tickets, cardType,
                        lastValidation != null ? lastValidation : "",
                        seasonPassExpiryDate != null ? seasonPassExpiryDate : ""))
                .observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.ui())
                .subscribe(readingResponse::setValue));
    }


    public SeReader getNfcReader() {
        return keypleSlaveAPI.getAndroidNfcKeypleService().getReader();

    }

    public void attachObserver() {
        // TODO check if not present already
        Log.i(TAG, "Attach poObserver to reader");
        ((ObservableReader) keypleSlaveAPI.getAndroidNfcKeypleService().getReader())
                .addObserver(poLogic);
    }

    private class PoObserver implements ObservableReader.ReaderObserver {

        /**
         * update READER EVENT : SE_INSERTED, SE_MATCHED
         * 
         * @param event
         */
        @Override
        public void update(ReaderEvent event) {
            Timber.i("New ReaderEvent received : " + event.getEventType());

            // If event is of type SE_MATCHED or SE_INSERTED
            if (event.getEventType().equals(ReaderEvent.EventType.SE_MATCHED)
                    || event.getEventType().equals(ReaderEvent.EventType.SE_INSERTED)) {

                // Set loading status
                setUiResponse(Status.LOADING, 0, KeypleSlaveAPI.CALYPSO_PO_TYPE, null, null);

                try {
                    // get status from server
                    Timber.d("Getting reader state information...");
                    keypleSlaveAPI.getUpdatedReaderStateAsync(new Callback<ReaderState>() {
                        @Override
                        public void onResponse(Call<ReaderState> call, Response<ReaderState> res) {

                            if (res.body() == null) {
                                Log.d(TAG, "No state received");

                            } else {

                                ReaderState readerState = res.body();
                                Log.d(TAG, "Receive card reader state : " + readerState.toString());
                                // Log.d(TAG, "readerState : " + readerState.getReaderStatus());
                                // Log.d(TAG, "po Type Name : " + readerState.getPoTypeName());

                                // get card type
                                if (KeypleSlaveAPI.CALYPSO_PO_TYPE
                                        .equals(readerState.getPoTypeName())) {
                                    Log.d(TAG,
                                            "PO Serial Number : " + readerState.getCurrentPoSN());
                                    Log.d(TAG, "cardContent : " + readerState.getCardContent());
                                    Log.d(TAG, "tickets available : "
                                            + readerState.getCardContent().getCounters().get(1));
                                    Log.d(TAG, "saleTransaction : "
                                            + (readerState.getSaleTransaction() == null ? "null"
                                                    : readerState.getSaleTransaction().toString()));
                                    // get current transaction
                                    currentTransactionId = readerState.getSaleTransaction().getId();
                                    Log.i(TAG, "Receive currentTransactionId : "
                                            + currentTransactionId);


                                    /*
                                     * the card is valid and a transaction is pending
                                     */
                                    if (readerState.getCardContent() != null
                                            && readerState.getSaleTransaction() != null) {

                                        // get current ticket number if available
                                        Integer tickets =
                                                readerState.getCardContent().getCounters().get(1);

                                        // Get last validation content
                                        String lastValidationLog = "";
                                        for (int i = 0; i < readerState.getCardContent()
                                                .getEventLog().size(); i++) {
                                            byte[] l = readerState.getCardContent().getEventLog()
                                                    .get(i + 1);
                                            if (l != null) {
                                                lastValidationLog +=
                                                        "[" + (-i) + "]: " + new String(l);
                                            }
                                            if (i < (readerState.getCardContent().getEventLog()
                                                    .size() - 1)) {
                                                lastValidationLog += " ";
                                            }
                                        }

                                        // Get season pass
                                        String seasonPassExpiryDate;
                                        try {
                                            seasonPassExpiryDate = new String(readerState
                                                    .getCardContent().getContracts().get(1));
                                        } catch (NullPointerException e) {
                                            seasonPassExpiryDate = "";
                                        }

                                        /*
                                         * Load is complete
                                         */
                                        if (SaleTransaction.LOAD_COMPLETE.equals(
                                                readerState.getSaleTransaction().getStatus())) {
                                            setUiResponse(Status.SUCCESS, tickets,
                                                    KeypleSlaveAPI.CALYPSO_PO_TYPE,
                                                    lastValidationLog, seasonPassExpiryDate);
                                            return;
                                        }

                                        /*
                                         * Card have been switched
                                         */
                                        if (SaleTransaction.WRONG_CARD_TO_LOAD_TRY_AGAIN.equals(
                                                readerState.getSaleTransaction().getStatus())) {
                                            setUiResponse(Status.WRONG_CARD, tickets,
                                                    KeypleSlaveAPI.CALYPSO_PO_TYPE, null, null);
                                            return;
                                        }

                                        /*
                                         * Transaction has not been paid
                                         */
                                        if (SaleTransaction.WAIT_FOR_PAYMENT.equals(
                                                readerState.getSaleTransaction().getStatus())) {
                                            if ((!seasonPassExpiryDate.isEmpty()
                                                    && seasonPassExpiryDate.contains("SEASON"))
                                                    || (tickets != null && tickets != 0)) {
                                                setUiResponse(Status.TICKETS_FOUND, tickets,
                                                        KeypleSlaveAPI.CALYPSO_PO_TYPE,
                                                        lastValidationLog, seasonPassExpiryDate);
                                                return;
                                            } else {
                                                setUiResponse(Status.EMPTY_CARD, 0,
                                                        KeypleSlaveAPI.CALYPSO_PO_TYPE,
                                                        lastValidationLog, seasonPassExpiryDate);
                                                return;
                                            }
                                        }

                                        /* wrong status : transaction is waiting for a card */
                                        if (SaleTransaction.WAIT_FOR_CARD_TO_LOAD.equals(
                                                readerState.getSaleTransaction().getStatus())) {
                                            Log.e(TAG, "should not be in this state");
                                            setUiResponse(Status.ERROR, 0,
                                                    KeypleSlaveAPI.CALYPSO_PO_TYPE, null, null);
                                            return;
                                        }

                                    } else {
                                        setUiResponse(Status.ERROR, 0,
                                                KeypleSlaveAPI.CALYPSO_PO_TYPE, null, null);
                                        return;
                                    }
                                } else {
                                    /*
                                     * Not a CALYPSO CARD
                                     */
                                    setUiResponse(Status.INVALID_CARD, 0,
                                            readerState.getPoTypeName(), null, null);
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ReaderState> call, Throwable t) {
                            Log.e(TAG, "onFailure getUpdatedReaderStateAsync " + t.toString());
                            setUiResponse(Status.ERROR, 0, KeypleSlaveAPI.CALYPSO_PO_TYPE, null,
                                    null);
                        }
                    });
                } catch (IOException | NullPointerException e) {
                    Log.e(TAG, e.toString());
                    setUiResponse(Status.ERROR, 0, KeypleSlaveAPI.CALYPSO_PO_TYPE, null, null);
                }
            }
        }
    }

    public void startNfcDetection(Activity activity){
        keypleSlaveAPI.getAndroidNfcKeypleService().startNfcDetection(activity);
    }

    public void stopNfcDetection(Activity activity){
        keypleSlaveAPI.getAndroidNfcKeypleService().stopNfcDetection(activity);
    }

    public void resetTransaction() {
        Log.d(TAG, "Clear all current transaction for current reader..");
        try {
            keypleSlaveAPI.resetTransaction(keypleSlaveAPI.getCurrentReader().getName(),
                    new Callback<String>() {
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
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Notify server of payment for the current transaction
     * 
     * @param ticketsToLoad
     */
    public void payTicket(Integer ticketsToLoad) {
        Log.d(TAG, "Pay for " + ticketsToLoad + " tickets...");
        try {
            Log.d(TAG, "Retrieve transaction by id : " + currentTransactionId + "...");
            keypleSlaveAPI.getTransaction(currentTransactionId, new Callback<SaleTransaction>() {
                @Override
                public void onResponse(Call<SaleTransaction> call,
                        Response<SaleTransaction> response) {
                    if (response.body() != null) {
                        Log.d(TAG, "Transaction retrieved " + response.body());
                        keypleSlaveAPI.getTransactionAPI().payTicket(response.body().getId(),
                                ticketsToLoad);
                    } else {
                        Log.d(TAG, "No transaction found for id : " + currentTransactionId);
                    }
                }

                @Override
                public void onFailure(Call<SaleTransaction> call, Throwable t) {
                    Log.e(TAG, "Failure getTransaction : " + currentTransactionId + t.getMessage());
                }
            });
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.toString());
        }
    }

}

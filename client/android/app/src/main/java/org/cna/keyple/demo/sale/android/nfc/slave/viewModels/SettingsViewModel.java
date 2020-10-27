/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;
import org.cna.keyple.demo.sale.android.nfc.slave.data.SharedPrefData;
import org.cna.keyple.demo.sale.android.nfc.slave.data.apiEndpoints.PingApiEndpoint;
import org.cna.keyple.demo.sale.android.nfc.slave.di.scopes.AppScoped;
import org.cna.keyple.demo.sale.android.nfc.slave.rx.SchedulerProvider;
import org.cna.keyple.demo.sale.android.nfc.slave.util.LiveEvent;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;
import retrofit2.Response;


@AppScoped
public class SettingsViewModel extends ViewModel {

    private final SchedulerProvider schedulerProvider;

    private final PingApiEndpoint pingApiEndpoint;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final LiveEvent<String> pingResponse = new LiveEvent<>();

    private SharedPrefData sharedPrefData;

    private MutableLiveData<String> serverIp = new MutableLiveData<>();

    private MutableLiveData<Integer> serverPort = new MutableLiveData<>();

    private MutableLiveData<String> serverProtocol = new MutableLiveData<>();


    @Inject
    public SettingsViewModel(PingApiEndpoint pingApiEndpoint, SchedulerProvider provider,
            SharedPrefData sharedPrefData) {
        this.schedulerProvider = provider;
        this.pingApiEndpoint = pingApiEndpoint;
        this.sharedPrefData = sharedPrefData;

        serverIp.setValue(sharedPrefData.loadServerIP());
        serverPort.setValue(sharedPrefData.loadServerPort());
        serverProtocol.setValue(sharedPrefData.loadServerProtocol());
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    public MutableLiveData<String> serverIp() {
        return serverIp;
    }

    public void setServerIp(String value) {
        sharedPrefData.saveServerIP(value);
    }

    public MutableLiveData<Integer> serverPort() {
        return serverPort;
    }

    public void setServerPort(Integer value) {
        sharedPrefData.saveServerPort(value);
    }

    public MutableLiveData<String> serverProtocol() {
        return serverProtocol;
    }

    public void setServerProtocol(String value) {
        sharedPrefData.saveServerProtocol(value);
    }

    public LiveEvent<String> getPingResponse() {
        return pingResponse;
    }

    public void testServer() {
        pingApiEndpoint.ping().subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

            }

                    @Override
                    public void onNext(Response<String> response) {
                        pingResponse.setValue(String.valueOf(response.code()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            pingResponse.setValue(String.valueOf(((HttpException) e).code()));
                        } else if (e instanceof IllegalStateException) {
                            // Connection worked but we don't send correct info so we set as 200
                            pingResponse.setValue("200");
                        } else {
                            pingResponse.setValue(e.toString());
                        }
                    }

                    @Override
                    public void onComplete() {

            }
                });
    }
}

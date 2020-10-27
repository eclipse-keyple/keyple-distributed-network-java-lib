/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments;

import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;
import org.cna.keyple.demo.sale.android.nfc.slave.R;
import org.cna.keyple.demo.sale.android.nfc.slave.data.SharedPrefData;
import org.cna.keyple.demo.sale.android.nfc.slave.data.http.common.ReaderState;
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.CardReaderResponse;
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.DeviceEnum;
import org.cna.keyple.demo.sale.android.nfc.slave.databinding.FragmentDeviceStatusBinding;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.BaseView;
import org.cna.keyple.demo.sale.android.nfc.slave.viewModels.ConnectionStatusViewModel;
import org.cna.keyple.demo.sale.android.nfc.slave.viewModels.DeviceReaderViewModel;
import org.eclipse.keyple.core.seproxy.SeReader;
import com.airbnb.lottie.LottieDrawable;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import dagger.android.support.DaggerFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DeviceStatusFragment extends DaggerFragment implements BaseView {

    FragmentDeviceStatusBinding binding;
    private Timer timer = new Timer();

    DeviceReaderViewModel deviceReaderViewModel;
    ConnectionStatusViewModel connectionStatusViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    SharedPrefData prefData;

    private final Integer TRANSITION_DELAY_MS = 2000;

    private TextView toolbarTitle;
    private DeviceEnum device;

    @Inject
    public DeviceStatusFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_status, container,
                false);
        binding.setLifecycleOwner(this);

        deviceReaderViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(DeviceReaderViewModel.class);
        connectionStatusViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(ConnectionStatusViewModel.class);

        toolbarTitle = getActivity().findViewById(R.id.toolbar_title);
        Toolbar toolbar = getActivity().findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);

        device = DeviceEnum.getDeviceEnum(prefData.loadDeviceType());

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        /*
         * connect device
         */
        deviceReaderViewModel.resetTransaction();

        SeReader wizwayReader = deviceReaderViewModel.getReader();
        /*
         * Connect reader to master, callback contains a readerState
         */
        connectionStatusViewModel.connectReaderToMaster(wizwayReader, new Callback<ReaderState>() {
            @Override
            public void onResponse(Call<ReaderState> call, Response<ReaderState> res) {
                if (res.body() == null) {
                    return;
                }
                ReaderState readerState = res.body();
                deviceReaderViewModel.updateUIonReaderState(readerState);
            }

            @Override
            public void onFailure(Call<ReaderState> call, Throwable t) {}
        });


        bindViewModel();
    }

    @Override
    public void onPause() {
        super.onPause();

        unbindViewModel();
        timer.cancel();
        binding.animation.cancelAnimation();
    }

    @Override
    public void bindViewModel() {
        deviceReaderViewModel.getReadingResponse().observe(this, this::changeDisplay);

    }

    @Override
    public void unbindViewModel() {
        deviceReaderViewModel.getReadingResponse().removeObservers(this);
    }

    public void changeDisplay(CardReaderResponse cardReaderResponse) {
        if (cardReaderResponse != null) {

            switch (cardReaderResponse.status) {
                case LOADING:

                    toolbarTitle.setText(R.string.device_loading);
                    binding.animation.setAnimation("loading_anim.json");
                    binding.animation.setRepeatCount(LottieDrawable.INFINITE);
                    binding.bigText.setVisibility(View.INVISIBLE);
                    binding.mainView.setBackgroundColor(getResources().getColor(R.color.white));
                    break;

                case EMPTY_CARD:
                case TICKETS_FOUND:
                case SUCCESS:
                    toolbarTitle.setText(R.string.device_connected_title);
                    binding.animation.setAnimation("tick_white.json");
                    binding.mainView.setBackgroundColor(getResources().getColor(R.color.green));
                    binding.bigText.setText(getString(R.string.device_connected_description,
                            getString(device.getTextId())));

                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(() -> Navigation
                                    .findNavController(getView())
                                    .navigate(DeviceStatusFragmentDirections
                                            .actionDeviceStatusFragmentToCardSummaryFragment(
                                                    cardReaderResponse.status.toString(),
                                                    cardReaderResponse.ticketsNumber,
                                                    cardReaderResponse.cardType,
                                                    cardReaderResponse.lastValidation,
                                                    cardReaderResponse.seasonPassExpiryDate)));



                        }
                    }, TRANSITION_DELAY_MS);
                    break;
                default:
                    toolbarTitle.setText(R.string.device_not_connected_title);
                    binding.animation.setAnimation("error_white.json");
                    binding.mainView.setBackgroundColor(getResources().getColor(R.color.orange));
                    binding.bigText.setText(getString(R.string.device_not_connected_description,
                            getString(device.getTextId())));
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            getActivity()
                                    .runOnUiThread(() -> Navigation.findNavController(getView())
                                            .navigate(R.id.action_nav_graph_pop));
                        }
                    }, TRANSITION_DELAY_MS);
            }
            binding.animation.playAnimation();
        }
    }
}

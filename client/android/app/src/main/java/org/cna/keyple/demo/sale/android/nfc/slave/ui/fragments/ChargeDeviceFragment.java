/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments;

import javax.inject.Inject;
import org.cna.keyple.demo.sale.android.nfc.slave.R;
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.CardReaderResponse;
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.Status;
import org.cna.keyple.demo.sale.android.nfc.slave.databinding.FragmentChargeDeviceBinding;
import org.cna.keyple.demo.sale.android.nfc.slave.di.scopes.ActivityScoped;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.BaseView;
import org.cna.keyple.demo.sale.android.nfc.slave.viewModels.ConnectionStatusViewModel;
import org.cna.keyple.demo.sale.android.nfc.slave.viewModels.DeviceReaderViewModel;
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

@ActivityScoped
public class ChargeDeviceFragment extends DaggerFragment implements BaseView {

    DeviceReaderViewModel deviceReaderViewModel;
    ConnectionStatusViewModel connectionStatusViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    FragmentChargeDeviceBinding binding;

    TextView mTitle;

    int ticketNumber;

    @Inject
    public ChargeDeviceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_charge_device, container,
                false);
        deviceReaderViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(DeviceReaderViewModel.class);
        connectionStatusViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(ConnectionStatusViewModel.class);

        binding.setLifecycleOwner(this);

        mTitle = getActivity().findViewById(R.id.toolbar_title);
        Toolbar toolbar = getActivity().findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        mTitle.setText(R.string.title_loading_title);

        ticketNumber = ChargeDeviceFragmentArgs.fromBundle(getArguments()).getTicketNumber();

        // connectionStatusViewModel.startPolling();

        deviceReaderViewModel.chargeDevice(ticketNumber);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        bindViewModel();

        binding.loadingAnimation.setAnimation("loading_anim.json");
        binding.loadingAnimation.setRepeatCount(LottieDrawable.INFINITE);
        binding.loadingAnimation.playAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        unbindViewModel();
    }

    @Override
    public void bindViewModel() {
        deviceReaderViewModel.getReadingResponse().observe(this, this::changeDisplay);
        // connectionStatusViewModel.startPolling();
    }

    @Override
    public void unbindViewModel() {
        deviceReaderViewModel.getReadingResponse().removeObservers(this);
        // connectionStatusViewModel.startPolling();
    }

    public void changeDisplay(CardReaderResponse cardReaderResponse) {
        if (cardReaderResponse != null) {
            if (cardReaderResponse.status != Status.LOADING) {
                binding.loadingAnimation.cancelAnimation();
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(ChargeDeviceFragmentDirections
                                .actionChargeDeviceFragmentToChargeResultFragment(
                                        cardReaderResponse.status.toString(), ticketNumber));
            }
        }
    }
}

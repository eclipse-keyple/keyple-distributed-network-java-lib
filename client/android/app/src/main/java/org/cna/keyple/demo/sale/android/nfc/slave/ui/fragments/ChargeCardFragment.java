/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments;

import javax.inject.Inject;
import org.cna.keyple.demo.sale.android.nfc.slave.R;
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.CardReaderResponse;
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.Status;
import org.cna.keyple.demo.sale.android.nfc.slave.databinding.FragmentChargeCardBinding;
import org.cna.keyple.demo.sale.android.nfc.slave.di.scopes.ActivityScoped;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.BaseView;
import org.cna.keyple.demo.sale.android.nfc.slave.viewModels.CardReaderViewModel;
import org.cna.keyple.demo.sale.android.nfc.slave.viewModels.ConnectionStatusViewModel;
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
public class ChargeCardFragment extends DaggerFragment implements BaseView {

    CardReaderViewModel cardReaderViewModel;
    ConnectionStatusViewModel connectionStatusViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    FragmentChargeCardBinding binding;

    TextView mTitle;

    int ticketNumber;

    @Inject
    public ChargeCardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_charge_card, container, false);
        cardReaderViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(CardReaderViewModel.class);
        connectionStatusViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(ConnectionStatusViewModel.class);

        binding.setLifecycleOwner(this);

        mTitle = getActivity().findViewById(R.id.toolbar_title);
        Toolbar toolbar = getActivity().findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        mTitle.setText(R.string.title_loading_title);

        ticketNumber = ChargeCardFragmentArgs.fromBundle(getArguments()).getTicketNumber();

        cardReaderViewModel.payTicket(ticketNumber);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        bindViewModel();

        binding.loadingAnimation.setAnimation("cardscan_anim.json");
        binding.loadingAnimation.setRepeatCount(0);
        binding.loadingAnimation.playAnimation();

        /*
         * Start Polling
         */
        cardReaderViewModel.startNfcDetection(getActivity());

    }

    @Override
    public void onPause() {
        super.onPause();
        unbindViewModel();
        cardReaderViewModel.stopNfcDetection(getActivity());

    }

    @Override
    public void bindViewModel() {
        cardReaderViewModel.getReadingResponse().observe(this, this::changeDisplay);

        // connectionStatusViewModel.startPolling();
    }

    @Override
    public void unbindViewModel() {
        cardReaderViewModel.getReadingResponse().removeObservers(this);
        // connectionStatusViewModel.stopPolling();
    }

    public void changeDisplay(CardReaderResponse cardReaderResponse) {
        if (cardReaderResponse != null) {
            if (cardReaderResponse.status == Status.LOADING) {
                binding.loadingAnimation.setAnimation("loading_anim.json");
                binding.loadingAnimation.playAnimation();
                binding.loadingAnimation.setRepeatCount(LottieDrawable.INFINITE);
                binding.presentTxt.setVisibility(View.INVISIBLE);
            } else {
                binding.loadingAnimation.cancelAnimation();
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(
                        ChargeCardFragmentDirections.actionChargeCardFragmentToChargeResultFragment(
                                cardReaderResponse.status.toString(), ticketNumber));
            }
        }
    }
}

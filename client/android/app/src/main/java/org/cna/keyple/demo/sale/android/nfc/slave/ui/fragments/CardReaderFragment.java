/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments;


import javax.inject.Inject;
import org.cna.keyple.demo.sale.android.nfc.slave.R;
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.CardReaderResponse;
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.Status;
import org.cna.keyple.demo.sale.android.nfc.slave.databinding.FragmentCardReaderBinding;
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
public class CardReaderFragment extends DaggerFragment implements BaseView {

    ConnectionStatusViewModel connectionStatusViewModel;
    CardReaderViewModel cardReaderViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    FragmentCardReaderBinding binding;

    @Inject
    public CardReaderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_card_reader, container, false);
        cardReaderViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(CardReaderViewModel.class);
        connectionStatusViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(ConnectionStatusViewModel.class);

        binding.setLifecycleOwner(this);

        TextView mTitle = getActivity().findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.present_card_title);
        Toolbar toolbar = getActivity().findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);

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

        /*
         * Start Polling
         */
        // connectionStatusViewModel.startPolling();
        /*
         * Connect the NFC reader to the Master
         */
        connectionStatusViewModel.NfcReaderConnect(cardReaderViewModel.poLogic);

        /*
         * delete current transaction if any
         */
        cardReaderViewModel.resetTransaction();
    }

    @Override
    public void onPause() {
        super.onPause();
        unbindViewModel();

        binding.loadingAnimation.cancelAnimation();

        cardReaderViewModel.stopNfcDetection(getActivity());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void bindViewModel() {
        cardReaderViewModel.getReadingResponse().observe(this, this::changeDisplay);

    }

    @Override
    public void unbindViewModel() {
        cardReaderViewModel.getReadingResponse().removeObservers(this);

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
                        CardReaderFragmentDirections.actionCardReaderFragmentToCardSummaryFragment(
                                cardReaderResponse.status.toString(),
                                cardReaderResponse.ticketsNumber, cardReaderResponse.cardType,
                                cardReaderResponse.lastValidation,
                                cardReaderResponse.seasonPassExpiryDate));
            }
        }
    }
}

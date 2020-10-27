/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments;

import javax.inject.Inject;
import org.cna.keyple.demo.sale.android.nfc.slave.R;
import org.cna.keyple.demo.sale.android.nfc.slave.data.SharedPrefData;
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.DeviceEnum;
import org.cna.keyple.demo.sale.android.nfc.slave.databinding.FragmentPaymentValidatedBinding;
import android.content.Context;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.navigation.Navigation;
import dagger.android.support.DaggerFragment;

public class PaymentValidatedFragment extends DaggerFragment {

    FragmentPaymentValidatedBinding binding;

    @Inject
    SharedPrefData prefData;

    @Inject
    public PaymentValidatedFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_validated, container,
                false);
        binding.setLifecycleOwner(this);

        TextView mTitle = getActivity().findViewById(R.id.toolbar_title);
        Toolbar toolbar = getActivity().findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        mTitle.setText(R.string.payment_validated_title);

        DeviceEnum device = DeviceEnum.getDeviceEnum(prefData.loadDeviceType());
        int ticketNumber =
                PaymentValidatedFragmentArgs.fromBundle(getArguments()).getTicketNumber();

        binding.chargeBtn.setText(getString(R.string.load_card, getString(device.getTextId())));
        binding.chargeBtn.setOnClickListener(v -> {
            if (device == DeviceEnum.CONTACTLESS_CARD) {
                Navigation.findNavController(v)
                        .navigate(PaymentValidatedFragmentDirections
                                .actionPaymentValidatedFragmentToChargeCardFragment()
                                .setTicketNumber(ticketNumber));
            } else {
                Navigation.findNavController(v)
                        .navigate(PaymentValidatedFragmentDirections
                                .actionPaymentValidatedFragmentToChargeDeviceFragment()
                                .setTicketNumber(ticketNumber));
            }
        });

        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        binding.animation.setAnimation("tick_anim.json");
        binding.animation.setRepeatCount(0);
        binding.animation.playAnimation();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

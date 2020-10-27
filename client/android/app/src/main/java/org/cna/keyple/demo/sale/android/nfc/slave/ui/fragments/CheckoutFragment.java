/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments;

import javax.inject.Inject;
import org.cna.keyple.demo.sale.android.nfc.slave.R;
import org.cna.keyple.demo.sale.android.nfc.slave.databinding.FragmentCheckoutBinding;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class CheckoutFragment extends Fragment {

    FragmentCheckoutBinding binding;

    @Inject
    public CheckoutFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkout, container, false);
        binding.setLifecycleOwner(this);

        Toolbar toolbar = getActivity().findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);

        int ticketNumber = CheckoutFragmentArgs.fromBundle(getArguments()).getTicketNumber();

        binding.ticketNumber.setText(
                getResources().getQuantityString(R.plurals.x_tickets, ticketNumber, ticketNumber));
        binding.ticketPrice.setText(getString(R.string.ticket_price, ticketNumber));

        binding.validateBtn.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(CheckoutFragmentDirections
                        .actionCheckoutFragmentToPaymentValidatedFragment()
                        .setTicketNumber(ticketNumber)));
        return binding.getRoot();
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

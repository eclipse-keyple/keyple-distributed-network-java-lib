/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments;

import javax.inject.Inject;
import org.cna.keyple.demo.sale.android.nfc.slave.R;
import org.cna.keyple.demo.sale.android.nfc.slave.databinding.FragmentSelectTicketsBinding;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


public class SelectTicketsFragment extends Fragment {

    FragmentSelectTicketsBinding binding;

    @Inject
    public SelectTicketsFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_tickets, container,
                false);
        binding.setLifecycleOwner(this);

        TextView mTitle = getActivity().findViewById(R.id.toolbar_title);
        Toolbar toolbar = getActivity().findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        mTitle.setText(R.string.title_purchase_title);

        binding.ticket1Label.setText(getResources().getQuantityString(R.plurals.x_tickets, 1, 1));
        binding.ticket2Label.setText(getResources().getQuantityString(R.plurals.x_tickets, 2, 2));
        binding.ticket3Label.setText(getResources().getQuantityString(R.plurals.x_tickets, 3, 3));
        binding.ticket4Label.setText(getResources().getQuantityString(R.plurals.x_tickets, 4, 4));

        binding.ticket1Price.setText(getString(R.string.ticket_price, 1));
        binding.ticket2Price.setText(getString(R.string.ticket_price, 2));
        binding.ticket3Price.setText(getString(R.string.ticket_price, 3));
        binding.ticket4Price.setText(getString(R.string.ticket_price, 4));

        binding.ticket1Btn.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(SelectTicketsFragmentDirections.actionGoCheckout().setTicketNumber(1)));
        binding.ticket2Btn.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(SelectTicketsFragmentDirections.actionGoCheckout().setTicketNumber(2)));
        binding.ticket3Btn.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(SelectTicketsFragmentDirections.actionGoCheckout().setTicketNumber(3)));
        binding.ticket4Btn.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(SelectTicketsFragmentDirections.actionGoCheckout().setTicketNumber(4)));

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

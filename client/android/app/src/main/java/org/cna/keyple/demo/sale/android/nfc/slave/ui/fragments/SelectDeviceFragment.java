/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments;

import javax.inject.Inject;
import org.cna.keyple.demo.sale.android.nfc.slave.R;
import org.cna.keyple.demo.sale.android.nfc.slave.data.SharedPrefData;
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.DeviceEnum;
import org.cna.keyple.demo.sale.android.nfc.slave.databinding.FragmentSelectDeviceBinding;
import org.cna.keyple.demo.sale.android.nfc.slave.viewModels.CardReaderViewModel;
import org.cna.keyple.demo.sale.android.nfc.slave.viewModels.ConnectionStatusViewModel;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import dagger.android.support.DaggerFragment;

public class SelectDeviceFragment extends DaggerFragment {

    FragmentSelectDeviceBinding binding;
    ConnectionStatusViewModel connectionStatusViewModel;
    CardReaderViewModel cardReaderViewModel;

    @Inject
    SharedPrefData prefData;

    @Inject
    ViewModelProvider.Factory viewModelFactory;


    @Inject
    public SelectDeviceFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_device, container,
                false);
        connectionStatusViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(ConnectionStatusViewModel.class);
        cardReaderViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(CardReaderViewModel.class);

        binding.setLifecycleOwner(this);

        TextView mTitle = getActivity().findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.device_selection_title);

        // OD : hide device Status?
        /*
         * ImageView deviceStatus = getActivity().findViewById(R.id.device_status);
         * deviceStatus.setVisibility(View.GONE);
         */

        binding.contactlessCardBtn.setOnClickListener(v -> {
            prefData.saveDeviceType(DeviceEnum.CONTACTLESS_CARD.toString());
            Navigation.findNavController(v).navigate(SelectDeviceFragmentDirections
                    .actionSelectDeviceFragmentToCardReaderFragment());
        });
        binding.simCardBtn.setOnClickListener(v -> {
            prefData.saveDeviceType(DeviceEnum.SIM.toString());
            Navigation.findNavController(v).navigate(SelectDeviceFragmentDirections
                    .actionSelectDeviceFragmentToDeviceStatusFragment());
        });
        /*
         * binding.braceletBtn.setOnClickListener(v -> {
         * prefData.saveDeviceType(DeviceEnum.BRACELET.toString());
         * Navigation.findNavController(v).navigate(SelectDeviceFragmentDirections.
         * actionSelectDeviceFragmentToDeviceStatusFragment()); });
         * binding.multiServiceCardBtn.setOnClickListener(v -> {
         * prefData.saveDeviceType(DeviceEnum.MULTI_CARD.toString());
         * Navigation.findNavController(v).navigate(SelectDeviceFragmentDirections.
         * actionSelectDeviceFragmentToDeviceStatusFragment()); });
         */
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        /*
         * Disconnect each reader at this screen
         */
        connectionStatusViewModel.NfcReaderDisconnect();
        connectionStatusViewModel.WizwayReaderDisconnect();

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

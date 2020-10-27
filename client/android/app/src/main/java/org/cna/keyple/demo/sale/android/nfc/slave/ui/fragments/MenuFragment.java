/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments;

import org.cna.keyple.demo.sale.android.nfc.slave.R;
import org.cna.keyple.demo.sale.android.nfc.slave.databinding.FragmentMenuBinding;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class MenuFragment extends Fragment {
    FragmentMenuBinding binding;

    ImageView menuBtn, closeMenuBtn;

    public MenuFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_menu, container, false);
        binding.setLifecycleOwner(this);

        TextView mTitle = getActivity().findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.menu);
        Toolbar toolbar = getActivity().findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);

        menuBtn = getActivity().findViewById(R.id.menu_btn);
        closeMenuBtn = getActivity().findViewById(R.id.close_menu_btn);

        binding.presentBtn.setOnClickListener(
                v -> Navigation.findNavController(v).navigate(R.id.action_nav_graph_pop));
        binding.settingsBtn.setOnClickListener(
                v -> Navigation.findNavController(v).navigate(R.id.settingsFragment));

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        menuBtn.setVisibility(View.GONE);
        closeMenuBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();

        menuBtn.setVisibility(View.VISIBLE);
        closeMenuBtn.setVisibility(View.GONE);
    }
}

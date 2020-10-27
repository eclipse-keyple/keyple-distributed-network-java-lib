/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments;

import javax.inject.Inject;
import org.cna.keyple.demo.sale.android.nfc.slave.R;
import org.cna.keyple.demo.sale.android.nfc.slave.databinding.FragmentSettingsBinding;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.BaseView;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.activities.SplashScreenActivity;
import org.cna.keyple.demo.sale.android.nfc.slave.viewModels.SettingsViewModel;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import dagger.android.support.DaggerFragment;

public class SettingsFragment extends DaggerFragment implements BaseView {

    FragmentSettingsBinding binding;

    SettingsViewModel settingsViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    public SettingsFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        settingsViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(SettingsViewModel.class);
        binding.setViewModel(settingsViewModel);
        binding.setLifecycleOwner(this);

        TextView mTitle = getActivity().findViewById(R.id.toolbar_title);
        Toolbar toolbar = getActivity().findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        mTitle.setText(R.string.action_settings);

        settingsViewModel.getPingResponse().observe(this, status -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.pingResult.setVisibility(View.VISIBLE);
            binding.pingResult.setText(status);
        });

        settingsViewModel.serverIp().observe(this, v -> settingsViewModel.setServerIp(v));
        settingsViewModel.serverPort().observe(this, v -> settingsViewModel.setServerPort(v));
        settingsViewModel.serverProtocol().observe(this,
                v -> settingsViewModel.setServerProtocol(v));

        binding.pingBtn.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.pingResult.setVisibility(View.GONE);
            settingsViewModel.testServer();
        });
        binding.restart.setOnClickListener(v -> restartApp());

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        bindViewModel();
    }

    @Override
    public void onPause() {
        super.onPause();
        unbindViewModel();
    }

    @Override
    public void bindViewModel() {}

    @Override
    public void unbindViewModel() {
        settingsViewModel.getPingResponse().removeObservers(this);
    }

    private void restartApp() {
        Context context = getActivity().getApplicationContext();
        Intent intent = new Intent(context, SplashScreenActivity.class);
        int mPendingIntentId = 0;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }
}

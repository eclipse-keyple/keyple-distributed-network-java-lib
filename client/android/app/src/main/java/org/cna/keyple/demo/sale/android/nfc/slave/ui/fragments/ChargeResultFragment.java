/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments;

import java.util.Timer;
import java.util.TimerTask;
import org.cna.keyple.demo.sale.android.nfc.slave.R;
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.Status;
import org.cna.keyple.demo.sale.android.nfc.slave.databinding.FragmentChargeResultBinding;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.OnBackPressedListener;
import com.airbnb.lottie.LottieDrawable;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class ChargeResultFragment extends Fragment implements OnBackPressedListener {

    private final Integer RETURN_DELAY_MS = 5000;

    FragmentChargeResultBinding binding;

    private Timer timer = new Timer();
    private Status status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_charge_result, container,
                false);
        binding.setLifecycleOwner(this);

        TextView mTitle = getActivity().findViewById(R.id.toolbar_title);
        Toolbar toolbar = getActivity().findViewById(R.id.my_toolbar);

        status = Status.getStatus(ChargeResultFragmentArgs.fromBundle(getArguments()).getStatus());
        Integer ticketNumber =
                ChargeResultFragmentArgs.fromBundle(getArguments()).getTicketNumber();

        binding.tryBtn.setOnClickListener(v -> getActivity().onBackPressed());

        switch (status) {
            case LOADING:
                binding.animation.setAnimation("loading_anim.json");
                binding.animation.setRepeatCount(LottieDrawable.INFINITE);
                binding.bigText.setVisibility(View.INVISIBLE);
                binding.smallDesc.setVisibility(View.INVISIBLE);
                binding.tryBtn.setVisibility(View.INVISIBLE);
                break;
            case SUCCESS:
                binding.animation.setAnimation("tick_anim.json");
                binding.animation.setRepeatCount(0);
                binding.animation.playAnimation();

                binding.bigText.setText(R.string.charging_success_label);
                binding.bigText.setVisibility(View.VISIBLE);
                binding.bigText.setTextColor(getResources().getColor(R.color.green));
                binding.smallDesc.setText(getResources().getQuantityString(
                        R.plurals.charging_success_description, ticketNumber, ticketNumber));
                binding.smallDesc.setTextColor(getResources().getColor(R.color.green));
                binding.smallDesc.setVisibility(View.VISIBLE);
                binding.tryBtn.setVisibility(View.INVISIBLE);
                mTitle.setText(R.string.title_loaded_title);

                toolbar.setNavigationIcon(null);

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(() -> Navigation.findNavController(getView())
                                .navigate(R.id.action_nav_graph_pop));
                    }
                }, RETURN_DELAY_MS);

                break;
            default:
                binding.animation.setAnimation("error_anim.json");
                binding.animation.setRepeatCount(0);
                binding.animation.playAnimation();

                binding.bigText.setText(R.string.transaction_cancelled_label);
                binding.bigText.setVisibility(View.VISIBLE);
                binding.bigText.setTextColor(getResources().getColor(R.color.red));
                binding.smallDesc.setText(R.string.transaction_cancelled_desc);
                binding.smallDesc.setTextColor(getResources().getColor(R.color.red));
                binding.smallDesc.setVisibility(View.VISIBLE);
                binding.tryBtn.setVisibility(View.VISIBLE);
                mTitle.setText(R.string.transaction_cancelled_title);

                toolbar.setNavigationIcon(R.drawable.ic_back);

                break;
        }

        // Play sound
        final MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.reading_sound);
        mp.start();

        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    public void onBackPressed() {
        if (status != Status.SUCCESS) {
            Navigation.findNavController(getView()).popBackStack();
        } else {
            Navigation.findNavController(getView()).navigate(R.id.action_nav_graph_pop);
        }
    }
}

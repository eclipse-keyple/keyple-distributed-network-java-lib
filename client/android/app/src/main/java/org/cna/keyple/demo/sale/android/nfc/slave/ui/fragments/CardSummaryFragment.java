/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments;

import javax.inject.Inject;
import org.cna.keyple.demo.sale.android.nfc.slave.R;
import org.cna.keyple.demo.sale.android.nfc.slave.data.SharedPrefData;
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.DeviceEnum;
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.Status;
import org.cna.keyple.demo.sale.android.nfc.slave.databinding.FragmentCardSummaryBinding;
import android.content.Context;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.navigation.Navigation;
import dagger.android.support.DaggerFragment;

public class CardSummaryFragment extends DaggerFragment {

    FragmentCardSummaryBinding binding;

    @Inject
    SharedPrefData prefData;

    private final static String TAG = CardSummaryFragment.class.getSimpleName();


    @Inject
    public CardSummaryFragment() {
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
        binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_card_summary, container, false);
        binding.setLifecycleOwner(this);

        TextView mTitle = getActivity().findViewById(R.id.toolbar_title);
        Toolbar toolbar = getActivity().findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);

        Status status =
                Status.getStatus(CardSummaryFragmentArgs.fromBundle(getArguments()).getStatus());
        Integer ticketNumber = CardSummaryFragmentArgs.fromBundle(getArguments()).getTicketNumber();
        DeviceEnum device = DeviceEnum.getDeviceEnum(prefData.loadDeviceType());
        String cardType = CardSummaryFragmentArgs.fromBundle(getArguments()).getCardType();
        String lastValidation =
                CardSummaryFragmentArgs.fromBundle(getArguments()).getLastValidation();
        String seasonPassExpiryDate =
                CardSummaryFragmentArgs.fromBundle(getArguments()).getSeasonPassExpiryDate();

        ImageView deviceStatus = getActivity().findViewById(R.id.device_status);
        deviceStatus.setVisibility(View.VISIBLE);
        /*
         * if(device == DeviceEnum.CONTACTLESS_CARD) { deviceStatus.setVisibility(View.GONE); } else
         * { deviceStatus.setVisibility(View.VISIBLE); }
         */

        switch (status) {
            case INVALID_CARD:
                mTitle.setText(R.string.card_not_valid_title);
                binding.animation.setAnimation("error_orange_anim.json");
                binding.animation.playAnimation();
                binding.bigText.setText(R.string.card_invalid_label);
                binding.bigText.setTextColor(getResources().getColor(R.color.orange));
                binding.smallDesc
                        .setText(String.format(getString(R.string.card_invalid_desc), cardType));
                binding.smallDesc.setTextColor(getResources().getColor(R.color.orange));
                binding.buyBtn.setVisibility(View.INVISIBLE);
                binding.cardContentSummary.setVisibility(View.GONE);
                break;
            case TICKETS_FOUND:
            case SUCCESS:
                mTitle.setText(R.string.card_content_title);
                binding.cardContentSummary.setVisibility(View.VISIBLE);
                binding.titleSummary.setText(getResources().getQuantityString(R.plurals.trips_lefts,
                        ticketNumber, ticketNumber));
                binding.animation.setVisibility(View.GONE);
                binding.bigText.setVisibility(View.GONE);
                binding.smallDesc.setVisibility(View.INVISIBLE);
                binding.buyBtn.setVisibility(View.VISIBLE);

                displaySeasonAndLastValidation(lastValidation, seasonPassExpiryDate);

                break;
            case EMPTY_CARD:
                mTitle.setText(R.string.no_title_title);
                binding.animation.setAnimation("error_anim.json");
                binding.animation.playAnimation();
                binding.bigText
                        .setText(getString(R.string.no_valid_label, getString(device.getTextId())));
                binding.bigText.setTextColor(getResources().getColor(R.color.red));
                binding.smallDesc.setVisibility(View.INVISIBLE);
                binding.buyBtn.setVisibility(View.VISIBLE);
                binding.cardContentSummary.setVisibility(View.GONE);

                displaySeasonAndLastValidation(lastValidation, seasonPassExpiryDate);

                break;
            default:
                mTitle.setText(R.string.error_title);
                binding.animation.setAnimation("error_anim.json");
                binding.animation.playAnimation();
                binding.bigText.setText(R.string.error_label);
                binding.bigText.setTextColor(getResources().getColor(R.color.red));
                binding.smallDesc.setVisibility(View.INVISIBLE);
                binding.buyBtn.setVisibility(View.INVISIBLE);
                binding.cardContentSummary.setVisibility(View.GONE);
                break;
        }

        binding.animation.playAnimation();

        // Play sound
        final MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.reading_sound);
        mp.start();

        binding.buyBtn.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.action_cardSummaryFragment_to_selectTicketsFragment));

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

    public void displaySeasonAndLastValidation(String lastValidation, String seasonPassExpiryDate) {

        Log.d(TAG, "lastValidation:" + lastValidation + " - seasonPassExpiryDate:"
                + seasonPassExpiryDate);

        if (lastValidation != null && lastValidation.length() > 0) {
            binding.lastValidationContent.setVisibility(View.VISIBLE);
            binding.lastValidationText.setText(lastValidation);
        } else {
            binding.lastValidationContent.setVisibility(View.GONE);
        }

        if (seasonPassExpiryDate != null && !seasonPassExpiryDate.isEmpty()
                && seasonPassExpiryDate.contains("SEASON")) {
            binding.seasonPassSummary.setVisibility(View.VISIBLE);
            // binding.seasonPassSummary.setText(String.format(getString(R.string.season_pass_valid),
            // seasonPassExpiryDate));
            binding.seasonPassSummary.setText(String.format(seasonPassExpiryDate));
        } else {
            binding.seasonPassSummary.setVisibility(View.GONE);
        }
    }
}

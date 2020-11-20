/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments;

import androidx.arch.core.executor.testing.CountingTaskExecutorRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.eclipse.keyple.demo.remote.ui.CardReaderActivity;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.CardReaderResponse;
import org.cna.keyple.demo.sale.android.nfc.slave.testing.SingleFragmentActivity;
import org.cna.keyple.demo.sale.android.nfc.slave.util.LiveEvent;
import org.cna.keyple.demo.sale.android.nfc.slave.util.ViewModelUtil;
import org.cna.keyple.demo.sale.android.nfc.slave.viewModels.CardReaderViewModel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CardReaderFragmentTest {
    @Rule
    public CountingTaskExecutorRule testRule = new CountingTaskExecutorRule();

    @Rule
    public ActivityTestRule<SingleFragmentActivity> activityTestRule =
            new ActivityTestRule<>(SingleFragmentActivity.class, true, true);

    private final CardReaderViewModel mViewModel = mock(CardReaderViewModel.class);

    private final LiveEvent<CardReaderResponse> response = new LiveEvent<>();

    private final CardReaderActivity fragment = new CardReaderActivity();

    @Before
    public void setUp() {
        when(mViewModel.getReadingResponse()).thenReturn(response);
        fragment.viewModelFactory = ViewModelUtil.createFor(mViewModel);
        activityTestRule.getActivity().setFragment(fragment);
    }
}

/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.cna.keyple.demo.sale.android.nfc.slave.data.KeypleSlaveAPI;
import org.cna.keyple.demo.sale.android.nfc.slave.rx.SchedulerProvider;
import org.cna.keyple.demo.sale.android.nfc.slave.viewModels.CardReaderViewModel;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import io.reactivex.schedulers.Schedulers;

/**
 * Unit test for {@link org.cna.keyple.demo.sale.android.nfc.slave.viewModels.CardReaderViewModel}
 */
public class CardReaderViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private KeypleSlaveAPI cardReaderApi;

    private SchedulerProvider scheduler;
    private CardReaderViewModel mViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        scheduler = new SchedulerProvider(Schedulers.trampoline(), Schedulers.trampoline());
        mViewModel = new CardReaderViewModel(cardReaderApi, scheduler);
    }
}

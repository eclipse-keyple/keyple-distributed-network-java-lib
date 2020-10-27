/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.rx;

import javax.inject.Inject;
import io.reactivex.Scheduler;

public class SchedulerProvider {
    private Scheduler backScheduler;
    private Scheduler foreScheduler;

    @Inject
    public SchedulerProvider(Scheduler backgroundScheduler, Scheduler foregroundScheduler) {
        backScheduler = backgroundScheduler;
        foreScheduler = foregroundScheduler;
    }

    /**
     * IO thread pool scheduler
     */
    public Scheduler io() {
        return backScheduler;
    }

    /**
     * Main Thread scheduler
     */
    public Scheduler ui() {
        return foreScheduler;
    }
}

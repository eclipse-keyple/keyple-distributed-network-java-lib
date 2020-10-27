/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data.model;

import org.cna.keyple.demo.sale.android.nfc.slave.R;

public enum DeviceEnum {
    CONTACTLESS_CARD(R.string.contactless_card), SIM(R.string.sim_card), BRACELET(
            R.string.bracelet), MULTI_CARD(R.string.multi_service_card);

    private final int stringId;

    /**
     * @param stringId
     */
    DeviceEnum(final int stringId) {
        this.stringId = stringId;
    }

    public int getTextId() {
        return stringId;
    }

    public static DeviceEnum getDeviceEnum(String name) {
        try {
            return DeviceEnum.valueOf(name.toUpperCase());
        } catch (Exception e) {
            // If the given state does not exist, return the default value.
            return DeviceEnum.CONTACTLESS_CARD;
        }
    }
}

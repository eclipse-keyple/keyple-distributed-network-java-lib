/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data.model;

public enum Status {
    LOADING("loading"), ERROR("error"), TICKETS_FOUND("tickets_found"), INVALID_CARD(
            "invalid_card"), EMPTY_CARD("empty_card"), WRONG_CARD(
                    "wrong_card"), DEVICE_CONNECTED("device_connected"), SUCCESS("success");

    private final String status;

    /**
     * @param status
     */
    Status(final String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }

    public static Status getStatus(String name) {
        try {
            return Status.valueOf(name.toUpperCase());
        } catch (Exception e) {
            // If the given state does not exist, return the default value.
            return Status.ERROR;
        }
    }
}

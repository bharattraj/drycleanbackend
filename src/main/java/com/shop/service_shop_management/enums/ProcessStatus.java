
package com.shop.service_shop_management.enums;

public enum ProcessStatus {

    RECEIVED((short) 0),
    UNDER_PROCESSING((short) 1),
    READY_FOR_SHOP((short) 2),
    READY_FOR_DELIVERY((short) 3),
    DELIVERED((short) 4);

    private final short code;

    ProcessStatus(short code) {
        this.code = code;
    }

    public short getCode() {
        return code;
    }

    public static ProcessStatus fromCode(short code) {

        for (ProcessStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }

        throw new IllegalArgumentException(
                "Invalid process status code: " + code
        );
    }
}

package com.motuma.paymentserviceesb.security.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    //    TODO: ADMIN PERMISSIONS
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
//    TODO: MANAGER PERMISSIONS
    MANAGER_READ("management:read"),
    MANAGER_UPDATE("management:update"),
    MANAGER_CREATE("management:create"),
    MANAGER_DELETE("management:delete"),
    //    TODO: MERCHANT PERMISSIONS
    MERCHANT_READ("merchant:read"),
    MERCHANT_UPDATE("merchant:update"),
    MERCHANT_CREATE("merchant:create"),
    MERCHANT_DELETE("merchant:delete"),
    //    TODO: SALES PERMISSIONS
    SALES_READ("sales:read"),
    SALES_UPDATE("sales:update"),
    SALES_CREATE("sales:create"),
    SALES_DELETE("sales:delete")
    ;

    @Getter
    private final String permission;
}

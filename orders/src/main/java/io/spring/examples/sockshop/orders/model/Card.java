/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.orders.model;

import java.io.Serializable;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Credit card information.
 */
@Data
@NoArgsConstructor
public class Card implements Serializable {
    /**
     * Credit card number.
     */
    @Schema(description = "Credit card number")
    private String longNum;

    /**
     * Expiration date.
     */
    @Schema(description = "Expiration date")
    private String expires;

    /**
     * CCV code.
     */
    @Schema(description = "CCV code")
    private String ccv;

    @Builder
    Card(String longNum, String expires, String ccv) {
        this.longNum = longNum;
        this.expires = expires;
        this.ccv = ccv;
    }
}

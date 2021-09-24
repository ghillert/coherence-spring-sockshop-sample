/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Composite key for the {@link Address class} when using JPA.
 */
@Data
public class AddressId implements Serializable {
	/**
	 * The customer Id that the address is associated with.
	 */
	private String user;

	/**
	 * The id for the address.
	 */
	private String addressId;

	/**
	 * Default constructor.
	 */
	public AddressId() {}

	@Builder
	@JsonCreator(mode = JsonCreator.Mode.DELEGATING)
	public AddressId(String id) {
		String[] parts = id.split(":");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Address Id is in the wrong format");
		}
		user = parts[0];
		addressId = parts[1];
	}

	/**
	 * Construct an instance of {@code AddressId} with the specified parameters.
	 */
	public AddressId(String user, String addressId) {
		this.user = user;
		this.addressId = addressId;
	}

	@Override
	public String toString() {
		return user + ":" + addressId;
	}

}

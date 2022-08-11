/*
 * Copyright (c) 2021, 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.controller;

import com.oracle.coherence.examples.sockshop.spring.users.controller.support.IdStatusResponse;
import com.oracle.coherence.examples.sockshop.spring.users.model.User;
import com.oracle.coherence.examples.sockshop.spring.users.controller.support.exceptions.UserAlreadyExistsException;
import com.oracle.coherence.examples.sockshop.spring.users.model.CustomUserEntityModel;
import com.oracle.coherence.examples.sockshop.spring.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(
		consumes = { MediaTypes.HAL_JSON_VALUE, MediaType.ALL_VALUE},
		produces = { MediaTypes.HAL_JSON_VALUE, MediaType.ALL_VALUE}
)
public class UserController {

	@Autowired
	private UserService users;

	//@NewSpan
	@Operation(summary = "Basic user authentication")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if user is successfully authenticated"),
			@ApiResponse(responseCode = "401", description = "if authentication fails")
	})
	@GetMapping("/login")
	public CustomUserEntityModel login(@AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser) {
		final User userFromCoherence = this.users.getUser(securityUser.getUsername());
		final User userToReturn = new User();
		userToReturn.setUsername(userFromCoherence.getUsername());
		userToReturn.setFirstName(userFromCoherence.getFirstName());
		userToReturn.setLastName(userFromCoherence.getLastName());

		final CustomUserEntityModel userEntityModel = new CustomUserEntityModel(userToReturn);

		final CustomerController controller = methodOn(CustomerController.class);

		final Link selfLink = linkTo(controller.getCustomer(userToReturn.getId())).withSelfRel();
		final Link customerLink = linkTo(controller.getCustomer(userToReturn.getId())).withRel("customer");
		final Link addressesLink = linkTo(controller.getCustomerAddresses(userToReturn.getId())).withRel("addresses");
		final Link cardsLink = linkTo(controller.getCustomerCards(userToReturn.getId())).withRel("cards");

		userEntityModel.add(selfLink, customerLink, addressesLink, cardsLink);
		return userEntityModel;
	}

	//@NewSpan
	@PostMapping("/register")
	@Operation(summary = "Register a user")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if user is successfully registered"),
			@ApiResponse(responseCode = "409", description = "if the user is already registered")
	})
	public IdStatusResponse register(
			@RequestBody
			@Parameter(description = "The user to be registered")
					User user) {
		final User prev = users.register(user);
		if (prev != null) {
			throw new UserAlreadyExistsException();
		}
		return new IdStatusResponse(user.getUsername());
	}
}

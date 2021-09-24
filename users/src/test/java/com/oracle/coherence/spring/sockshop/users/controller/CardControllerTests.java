/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.controller;

import com.oracle.coherence.spring.sockshop.users.model.Card;
import com.oracle.coherence.spring.sockshop.users.model.CardId;
import com.oracle.coherence.spring.sockshop.users.model.User;
import com.oracle.coherence.spring.sockshop.users.service.UserService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for {@link CardController}.
 */
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CardControllerTests {

	@Autowired
	private ServletWebServerApplicationContext webServerApplicationConext;


	@Autowired
	ApplicationContext context;

	private UserService userService;

	@BeforeEach
	void setup() {
		// Configure RestAssured to run tests against our application
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = webServerApplicationConext.getWebServer().getPort();
		userService = getUserService();
		userService.removeUser("foouser");
		userService.removeUser("baruser");
	}

	protected UserService getUserService() {
		return context.getBean(UserService.class);
	}

	@Test
	public void testRegisterCard() {
		this.userService.register(new User("foo", "passfoo", "foo@weavesocks.com", "foouser", "pass"));
		given().
				contentType(JSON).
				body(new CardController.AddCardRequest("3691369136913691", "01/21", "789", "foouser")).
				when().
				post("/cards").
				then().log().all().
				statusCode(200).
				body("id", containsString("foouser"));
	}

	@Test
	public void testGetCard() {
		final User user = this.userService.getOrCreate("cardUser");
		user.setUsername("cardUser");
		user.setPassword("not-a-secret");

		final Card card = new Card("3691369136913691", "01/21", "789");
		final CardId cardId = user.addCard(card).getCardId();

        this.userService.register(user);
		given().
				pathParam("id", cardId.toString()).
				when().
				get("/cards/{id}").
				then().
				statusCode(HttpStatus.OK.value()).
				body("longNum", containsString("3691"),
						"ccv", is("789"));
	}

	@Test
	public void testDeleteCard() {
		final User user = this.userService.getOrCreate("cardUser");
		user.setUsername("cardUser");
		user.setPassword("not-a-secret");

		final Card card = new Card("3691369136913691", "01/21", "789");
		final CardId cardId = user.addCard(card).getCardId();

        this.userService.register(user);
		given().
				pathParam("id", cardId.toString()).
				when().
				get("/cards/{id}").
				then().
				statusCode(HttpStatus.OK.value());
	}

	@Test
	public void testGetAllCards() {
		when().
				get("/cards").
				then().
				statusCode(HttpStatus.OK.value());
	}
}

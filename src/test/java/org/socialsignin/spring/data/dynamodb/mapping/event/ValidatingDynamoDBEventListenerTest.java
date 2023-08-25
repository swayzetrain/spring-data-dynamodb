/**
 * Copyright © 2018 spring-data-dynamodb (https://github.com/swayzetrain/spring-data-dynamodb)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.socialsignin.spring.data.dynamodb.mapping.event;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.socialsignin.spring.data.dynamodb.domain.sample.User;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ExtendWith(MockitoExtension.class)
class ValidatingDynamoDBEventListenerTest {

	private final User sampleEntity = new User();
	@Mock
	private Validator validator;
	private ValidatingDynamoDBEventListener underTest;

	@BeforeEach
	public void setUp() {
		underTest = new ValidatingDynamoDBEventListener(validator);
	}

	@Test
	void testWrongConstructor() {

		assertThatThrownBy(() -> new ValidatingDynamoDBEventListener(null)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testEmptyResult() {

		underTest.onBeforeSave(sampleEntity);

		assertTrue(true);
	}

	@Test
	void testValidationException() {

		Set<ConstraintViolation<User>> validationResult = new HashSet<>();

		@SuppressWarnings("unchecked")
		ConstraintViolation<User> vc1 = mock(ConstraintViolation.class);
		when(vc1.toString()).thenReturn("Test Validation Exception 1");
		validationResult.add(vc1);

		@SuppressWarnings("unchecked")
		ConstraintViolation<User> vc2 = mock(ConstraintViolation.class);
		when(vc2.toString()).thenReturn("Test Validation Exception 2");
		validationResult.add(vc2);
		when(validator.validate(sampleEntity)).thenReturn(validationResult);

		assertThatThrownBy(() -> underTest.onBeforeSave(sampleEntity)).isInstanceOf(ConstraintViolationException.class);
	}
}

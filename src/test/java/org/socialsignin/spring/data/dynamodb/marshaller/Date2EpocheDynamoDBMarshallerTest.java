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
package org.socialsignin.spring.data.dynamodb.marshaller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Date2EpocheDynamoDBMarshallerTest {

	private Date2EpocheDynamoDBMarshaller underTest;

	@BeforeEach
	public void setUp() {
		underTest = new Date2EpocheDynamoDBMarshaller();
	}

	@Test
	void testNullMarshall() {
		String actual = underTest.marshall(null);

		assertNull(actual);
	}

	@Test
	void testMarshall() {
		assertEquals("0", underTest.marshall(new Date(0)));
		assertEquals("0", underTest.convert(new Date(0)));
	}

	@Test
	void testUnmarshallNull() {
		Date actual = underTest.unmarshall(Date.class, null);

		assertNull(actual);
	}

	@Test
	void testUnmarshall() {
		assertEquals(new Date(0), underTest.unmarshall(Date.class, "0"));
		assertEquals(new Date(0), underTest.unconvert("0"));;

	}

	@Test
	void testUnmarshallGarbage() {
		assertThatThrownBy(() -> underTest.unmarshall(Date.class, "something")).isInstanceOf(NumberFormatException.class);
	}
}

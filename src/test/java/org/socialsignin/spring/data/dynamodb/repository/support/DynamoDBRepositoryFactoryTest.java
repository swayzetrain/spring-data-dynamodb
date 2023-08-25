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
package org.socialsignin.spring.data.dynamodb.repository.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;



class DynamoDBRepositoryFactoryTest {

	@Test
	void testVersionNullNull() {
		assertFalse(DynamoDBRepositoryFactory.isCompatible(null, null));
	}

	@Test
	void testVersionNullValue() {
		assertFalse(DynamoDBRepositoryFactory.isCompatible(null, "1.0."));
		assertFalse(DynamoDBRepositoryFactory.isCompatible("1.0", null));
	}

	@Test
	void testVersionCompatible() {
		assertTrue(DynamoDBRepositoryFactory.isCompatible("1.0", "1.0"));
		assertTrue(DynamoDBRepositoryFactory.isCompatible("1.0.0.0.1", "1.0..0.0.1"));

		assertFalse(DynamoDBRepositoryFactory.isCompatible("1.1", "1.0"));
		assertFalse(DynamoDBRepositoryFactory.isCompatible("1.0", "2.0"));

		assertTrue(DynamoDBRepositoryFactory.isCompatible("1.0.0-SR", "1.0.0-SR"));
	}

}

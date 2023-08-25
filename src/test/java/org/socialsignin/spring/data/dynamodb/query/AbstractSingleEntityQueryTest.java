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
/**
	 * Copyright © 2018 spring-data-dynamodb (https://github.com/boostchicken/spring-data-dynamodb)
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
package org.socialsignin.spring.data.dynamodb.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;
import org.socialsignin.spring.data.dynamodb.domain.sample.User;

class AbstractSingleEntityQueryTest {

	@Mock
	private DynamoDBOperations dynamoDBOperations;

	private final User entity = new User();

	private AbstractSingleEntityQuery<User> underTest;

	@Test
	void testGetResultList() {
		underTest = new AbstractSingleEntityQuery<User>(dynamoDBOperations, User.class) {
			@Override
			public User getSingleResult() {
				return entity;
			}
		};

		List<User> actual = underTest.getResultList();

		assertEquals(1, actual.size());
		assertEquals(entity, actual.get(0));
	}

	@Test
	void testGetResultListEmpty() {
		underTest = new AbstractSingleEntityQuery<User>(dynamoDBOperations, User.class) {
			@Override
			public User getSingleResult() { return null; }
		};

		List<User> actual = underTest.getResultList();

		assertEquals(0, actual.size());
	}

}

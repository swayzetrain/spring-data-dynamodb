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
package org.socialsignin.spring.data.dynamodb.repository.query;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;

@ExtendWith(MockitoExtension.class)
class DynamoDBQueryLookupStrategyTest {
	
	@Mock
	private DynamoDBOperations dynamoDBOperations;

	@Test
	void testCreate() {
		QueryLookupStrategy actual;
		actual = DynamoDBQueryLookupStrategy.create(dynamoDBOperations, Key.CREATE);
		assertNotNull(actual);

		actual = DynamoDBQueryLookupStrategy.create(dynamoDBOperations, Key.CREATE_IF_NOT_FOUND);
		assertNotNull(actual);
	}

	@Test
	void testNull() {
		QueryLookupStrategy actualNull = DynamoDBQueryLookupStrategy.create(dynamoDBOperations, null);
		QueryLookupStrategy actualCreate = DynamoDBQueryLookupStrategy.create(dynamoDBOperations, Key.CREATE);

		assertSame(actualNull.getClass(), actualCreate.getClass());
	}

	@Test
	void testDeclaredQuery() {
		assertThatThrownBy(() -> DynamoDBQueryLookupStrategy.create(dynamoDBOperations, Key.USE_DECLARED_QUERY)).isInstanceOf(IllegalArgumentException.class);
	}

}

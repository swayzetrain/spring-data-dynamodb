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
package org.socialsignin.spring.data.dynamodb.query;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;
import org.socialsignin.spring.data.dynamodb.domain.sample.User;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;


@ExtendWith(MockitoExtension.class)
class ScanExpressionCountQueryTest {

	@Mock
	private DynamoDBOperations dynamoDBOperations;
	@Mock
	private DynamoDBScanExpression scanExpression;

	private ScanExpressionCountQuery<User> underTest;

	@Test
	void testScanCountEnabledTrueTrue() {
		underTest = new ScanExpressionCountQuery<>(dynamoDBOperations, User.class, scanExpression, true);

		underTest.assertScanCountEnabled(true);

		assertTrue(true);
	}

	@Test
	void testScanCountEnabledTrueFalse() {
		underTest = new ScanExpressionCountQuery<>(dynamoDBOperations, User.class, scanExpression, true);

		assertThatThrownBy(() -> underTest.assertScanCountEnabled(false)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testScanCountEnabledFalseTrue() {
		underTest = new ScanExpressionCountQuery<>(dynamoDBOperations, User.class, scanExpression, false);

		underTest.assertScanCountEnabled(true);

		assertTrue(true);
	}

	@Test
	void testScanCountEnabledFalseFalse() {
		underTest = new ScanExpressionCountQuery<>(dynamoDBOperations, User.class, scanExpression, false);

		assertThatThrownBy(() -> underTest.assertScanCountEnabled(false)).isInstanceOf(IllegalArgumentException.class);
	}
}

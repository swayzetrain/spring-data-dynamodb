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
package org.socialsignin.spring.data.dynamodb.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.socialsignin.spring.data.dynamodb.domain.sample.Playlist;
import org.socialsignin.spring.data.dynamodb.domain.sample.User;
import org.springframework.context.ApplicationContext;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;

@ExtendWith(MockitoExtension.class)
class DynamoDBTemplateTest {
	@Mock
	private DynamoDBMapper dynamoDBMapper;
	@Mock
	private DynamoDBMapperConfig dynamoDBMapperConfig;
	@Mock
	private AmazonDynamoDB dynamoDB;
	@Mock
	private ApplicationContext applicationContext;
	@Mock
	private DynamoDBQueryExpression<User> countUserQuery;

	private DynamoDBTemplate dynamoDBTemplate;

	@BeforeEach
	public void setUp() {
		this.dynamoDBTemplate = new DynamoDBTemplate(dynamoDB, dynamoDBMapper, dynamoDBMapperConfig);
		this.dynamoDBTemplate.setApplicationContext(applicationContext);

		// check that the defaults are properly initialized - #108
		String userTableName = dynamoDBTemplate.getOverriddenTableName(User.class, "UserTable");
		assertEquals("UserTable", userTableName);
	}

	@Test
	void testConstructorAllNull() {
		try {
			dynamoDBTemplate = new DynamoDBTemplate(null, null, null);
			fail("AmazonDynamoDB must not be null!");
		} catch (IllegalArgumentException iae) {
			// ignored
		}

		try {
			dynamoDBTemplate = new DynamoDBTemplate(dynamoDB, null, null);
			fail("DynamoDBMapper must not be null!");
		} catch (IllegalArgumentException iae) {
			// ignored
		}
		try {
			dynamoDBTemplate = new DynamoDBTemplate(dynamoDB, dynamoDBMapper, null);
			fail("DynamoDBMapperConfig must not be null!");
		} catch (IllegalArgumentException iae) {
			// ignored
		}
		assertTrue(true);
	}

	@Test
	void testDelete() {
		User user = new User();
		dynamoDBTemplate.delete(user);

		verify(dynamoDBMapper).delete(user);
	}

	@Test
	void testBatchDelete_CallsCorrectDynamoDBMapperMethod() {
		List<User> users = new ArrayList<>();
		dynamoDBTemplate.batchDelete(users);
		verify(dynamoDBMapper).batchDelete(anyList());
	}

	@Test
	void testSave() {
		User user = new User();
		dynamoDBTemplate.save(user);

		verify(dynamoDBMapper).save(user);
	}

	@Test
	void testBatchSave_CallsCorrectDynamoDBMapperMethod() {
		List<User> users = new ArrayList<>();
		dynamoDBTemplate.batchSave(users);

		verify(dynamoDBMapper).batchSave(eq(users));
	}

	@Test
	void testCountQuery() {
		DynamoDBQueryExpression<User> query = countUserQuery;
		dynamoDBTemplate.count(User.class, query);

		verify(dynamoDBMapper).count(User.class, query);
	}

	@Test
	void testCountScan() {
		DynamoDBScanExpression scan = mock(DynamoDBScanExpression.class);
		int actual = dynamoDBTemplate.count(User.class, scan);

		assertEquals(0, actual);
		verify(dynamoDBMapper).count(User.class, scan);
	}

	@Test
	void testLoadByHashKey_WhenDynamoDBMapperReturnsNull() {
		User user = dynamoDBTemplate.load(User.class, "someHashKey");
		Assertions.assertNull(user);
	}

	@Test
	void testLoadByHashKeyAndRangeKey_WhenDynamoDBMapperReturnsNull() {
		Playlist playlist = dynamoDBTemplate.load(Playlist.class, "someHashKey", "someRangeKey");
		Assertions.assertNull(playlist);
	}

}

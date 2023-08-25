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


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.spy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;
import org.socialsignin.spring.data.dynamodb.domain.sample.User;
import org.socialsignin.spring.data.dynamodb.mapping.DynamoDBMappingContext;
import org.socialsignin.spring.data.dynamodb.repository.util.DynamoDBMappingContextProcessor;
import org.socialsignin.spring.data.dynamodb.repository.util.Entity2DynamoDBTableSynchronizer;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.Repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

@ExtendWith(MockitoExtension.class)
class DynamoDBRepositoryFactoryBeanTest {

	@Mock
	private ApplicationContext applicationContext;
	@Mock
	private DynamoDBOperations dynamoDBOperations;
	@Mock
	private DynamoDBMapperConfig dynamoDBMapperConfig;
	@Mock
	private AmazonDynamoDB amazonDynamoDB;
	@Mock
	private Entity2DynamoDBTableSynchronizer<User, String> tableSynchronizer;
	@Mock
	private DynamoDBMappingContextProcessor<User, String> dynamoDBMappingContextProcessor;

	private DynamoDBMappingContext dynamoDBMappingContext = new DynamoDBMappingContext();

	private DynamoDBRepositoryFactoryBean<UserRepository, User, String> underTest;

	public interface UserRepository extends Repository<User, String> {

	}

	@BeforeEach
	public void setUp() {
		underTest = spy(new DynamoDBRepositoryFactoryBean<>(UserRepository.class));
		underTest.setDynamoDBMappingContext(dynamoDBMappingContext);
		underTest.setEntity2DynamoDBTableSynchronizer(tableSynchronizer);
		underTest.setDynamoDBMappingContextProcessor(dynamoDBMappingContextProcessor);
	}

	@Test
	void testDynamoDBOperations() {
		try {
			underTest.getPersistentEntity();
			fail();
		} catch (NullPointerException /* IllegalStateException */ ise) {
			assertTrue(true);
		}

		underTest.setDynamoDBOperations(dynamoDBOperations);
		underTest.afterPropertiesSet();

		assertNotNull(underTest.getPersistentEntity());
	}

	@Test
	void testAmazonDynamoDB() {
		try {
			underTest.getPersistentEntity();
			fail();
		} catch (NullPointerException /* IllegalStateException */ ise) {
			assertTrue(true);
		}

		underTest.setDynamoDBOperations(dynamoDBOperations);
		underTest.afterPropertiesSet();

		assertNotNull(underTest.getPersistentEntity());
	}

}

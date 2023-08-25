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
package org.socialsignin.spring.data.dynamodb.mapping;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBHashAndRangeKey;
import org.springframework.data.annotation.Id;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Unit tests for {@link DynamoDBMappingContext}.
 * 
 * @author Michael Lavelle
 * @author Sebastian Just
 */
@ExtendWith(MockitoExtension.class)
class DynamoDBMappingContextTest {
	@DynamoDBTable(tableName = "a")
	static class DynamoDBMappingContextTestFieldEntity {

		@DynamoDBHashKey
		private String hashKey;

		@DynamoDBRangeKey
		private String rangeKey;

		@SuppressWarnings("unused")
		private String someProperty;
	}

	@DynamoDBTable(tableName = "b")
	static class DynamoDBMappingContextTestMethodEntity {

		@DynamoDBHashKey
		public String getHashKey() {
			return null;
		}

		@DynamoDBRangeKey
		public String getRangeKey() {
			return null;
		}

		public String getSomeProperty() {
			return null;
		}
	}

	@DynamoDBTable(tableName = "c")
	static class DynamoDBMappingContextTestIdEntity {
		@Id
		private DynamoDBHashAndRangeKey hashRangeKey;

		@DynamoDBIgnore
		public String getSomething() {
			return null;
		}
	}

	private DynamoDBMappingContext underTest;

	@BeforeEach
	public void setUp() {
		underTest = new DynamoDBMappingContext();
	}

	@Test
	void detectsPropertyAnnotation() {

		DynamoDBPersistentEntityImpl<?> entity = underTest
				.getPersistentEntity(DynamoDBMappingContextTestFieldEntity.class);

		assertNotNull(entity);
		assertNotNull(entity.getIdProperty());
	}

	@Test
	void detectdMethodsAnnotation() {
		DynamoDBPersistentEntityImpl<?> entity = underTest
				.getPersistentEntity(DynamoDBMappingContextTestMethodEntity.class);

		assertNotNull(entity);
		assertNotNull(entity.getIdProperty());

	}

	@Test
	void detectdMethodsId() {
		DynamoDBPersistentEntityImpl<?> entity = underTest
				.getPersistentEntity(DynamoDBMappingContextTestIdEntity.class);

		assertNotNull(entity);
		assertNotNull(entity.getIdProperty());

	}
}

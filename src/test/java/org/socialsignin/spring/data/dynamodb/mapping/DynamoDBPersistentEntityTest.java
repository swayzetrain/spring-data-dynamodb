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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBHashAndRangeKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.ClassTypeInformation;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;

@ExtendWith(MockitoExtension.class)
class DynamoDBPersistentEntityTest {

	static class DynamoDBPersistentEntity {
		@DynamoDBHashKey
		private String id;

		@Id
		private DynamoDBHashAndRangeKey hashRangeKey;

		@SuppressWarnings("unused")
		private String name;
	}

	@Mock
	private Comparator<DynamoDBPersistentProperty> comparator;

	private ClassTypeInformation<DynamoDBPersistentEntity> cti = ClassTypeInformation
			.from(DynamoDBPersistentEntity.class);
	private DynamoDBPersistentEntityImpl<DynamoDBPersistentEntity> underTest;

	@BeforeEach
	public void setUp() {
		underTest = new DynamoDBPersistentEntityImpl<>(cti, comparator);
	}

	@Test
	void testSomeProperty() throws NoSuchFieldException {
		Property prop = Property.of(cti, DynamoDBPersistentEntity.class.getDeclaredField("name"));

		DynamoDBPersistentProperty property = new DynamoDBPersistentPropertyImpl(prop, underTest,
				SimpleTypeHolder.DEFAULT);
		DynamoDBPersistentProperty actual = underTest.returnPropertyIfBetterIdPropertyCandidateOrNull(property);

		assertNull(actual);
	}

	@Test
	void testIdProperty() throws NoSuchFieldException {
		Property prop = Property.of(cti, DynamoDBPersistentEntity.class.getDeclaredField("id"));
		DynamoDBPersistentProperty property = new DynamoDBPersistentPropertyImpl(prop, underTest,
				SimpleTypeHolder.DEFAULT);
		DynamoDBPersistentProperty actual = underTest.returnPropertyIfBetterIdPropertyCandidateOrNull(property);

		assertNotNull(actual);
		assertTrue(actual.isHashKeyProperty());
	}

	@Test
	void testCompositeIdProperty() throws NoSuchFieldException {
		Property prop = Property.of(cti, DynamoDBPersistentEntity.class.getDeclaredField("hashRangeKey"));
		DynamoDBPersistentProperty property = new DynamoDBPersistentPropertyImpl(prop, underTest,
				SimpleTypeHolder.DEFAULT);
		DynamoDBPersistentProperty actual = underTest.returnPropertyIfBetterIdPropertyCandidateOrNull(property);

		assertNotNull(actual);
		assertTrue(actual.isCompositeIdProperty());
	}
}

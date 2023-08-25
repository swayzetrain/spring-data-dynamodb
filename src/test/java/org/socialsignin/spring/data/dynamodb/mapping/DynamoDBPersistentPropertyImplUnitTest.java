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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Unit tests for {@link DynamoDBPersistentPropertyImpl}.
 * 
 * @author Michael Lavelle
 * @author Sebastian Just
 */
@ExtendWith(MockitoExtension.class)
class DynamoDBPersistentPropertyImplUnitTest {

	DynamoDBMappingContext context;
	DynamoDBPersistentEntity<?> entity;

	@BeforeEach
	public void setUp() {

		context = new DynamoDBMappingContext();
		entity = context.getPersistentEntity(Sample.class);
	}

	/**
	 * @see DATAJPA-284
	 */
	@Test
	void considersOtherPropertiesAsNotTransient() {

		DynamoDBPersistentProperty property = entity.getPersistentProperty("otherProp");
		assertNotNull(property);
	}

	/**
	 * @see DATAJPA-376
	 */
	@Test
	void considersDynamoDBIgnoredPropertiesAsTransient() {
		assertNull(entity.getPersistentProperty("ignoredProp"));
	}

	@DynamoDBTable(tableName = "sample")
	static class Sample {

		private String ignoredProp = "ignored";
		private String otherProp = "other";

		public String getOtherProp() {
			return otherProp;
		}

		public void setOtherProp(String otherProp) {
			this.otherProp = otherProp;
		}

		@DynamoDBIgnore
		public String getIgnoredProp() {
			return ignoredProp;
		}

		public void setIgnoredProp(String ignoredProp) {
			this.ignoredProp = ignoredProp;
		}
	}

}

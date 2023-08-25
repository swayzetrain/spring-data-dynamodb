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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.socialsignin.spring.data.dynamodb.domain.sample.User;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;

@ExtendWith(MockitoExtension.class)
class DynamoDBEntityMetadataSupportUnitTest {

	@Test
	void testGetMarshallerForProperty_WhenAnnotationIsOnField_AndReturnsDynamoDBMarshaller() {
		DynamoDBEntityMetadataSupport<User, ?> support = new DynamoDBEntityMetadataSupport<>(User.class);
		@SuppressWarnings("deprecation")
		DynamoDBMarshaller<?> fieldAnnotation = support.getMarshallerForProperty("joinYear");
		assertNotNull(fieldAnnotation);
	}

	@Test
	void testGetMarshallerForProperty_WhenAnnotationIsOnMethod_AndReturnsDynamoDBMarshaller() {
		DynamoDBEntityMetadataSupport<User, ?> support = new DynamoDBEntityMetadataSupport<>(User.class);
		@SuppressWarnings("deprecation")
		DynamoDBMarshaller<?> methodAnnotation = support.getMarshallerForProperty("leaveDate");
		assertNotNull(methodAnnotation);
	}
}

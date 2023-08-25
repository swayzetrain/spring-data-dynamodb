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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.socialsignin.spring.data.dynamodb.domain.sample.PlaylistId;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
class DynamoDBHashAndRangeKeyMethodExtractorImplUnitTest {

	private DynamoDBHashAndRangeKeyMethodExtractor<PlaylistId> playlistIdMetadata;
	private DynamoDBHashAndRangeKeyMethodExtractor<IdClassWithNoAnnotatedMethods> idClassWithNoHashOrRangeKeyMethodMetadata;
	private DynamoDBHashAndRangeKeyMethodExtractor<IdClassWithOnlyAnnotatedHashKeyMethod> idClassWithOnlyHashKeyMethodMetadata;
	private DynamoDBHashAndRangeKeyMethodExtractor<IdClassWithOnlyAnnotatedRangeKeyMethod> idClassWithOnlyRangeKeyMethodMetadata;
	private DynamoDBHashAndRangeKeyMethodExtractor<IdClassWithMulitpleAnnotatedHashKeyMethods> idClassWitMultipleAnnotatedHashKeysMetadata;
	private DynamoDBHashAndRangeKeyMethodExtractor<IdClassWithMulitpleAnnotatedRangeKeyMethods> idClassWitMultipleAnnotatedRangeKeysMetadata;

	@Test
	void testConstruct_WhenHashKeyMethodExists_WhenRangeKeyMethodExists() {
		playlistIdMetadata = new DynamoDBHashAndRangeKeyMethodExtractorImpl<PlaylistId>(PlaylistId.class);
		Method hashKeyMethod = playlistIdMetadata.getHashKeyMethod();
		assertNotNull(hashKeyMethod);
		assertEquals("getUserName", hashKeyMethod.getName());
		Method rangeKeyMethod = playlistIdMetadata.getRangeKeyMethod();
		assertNotNull(rangeKeyMethod);
		assertEquals("getPlaylistName", rangeKeyMethod.getName());

		assertEquals(PlaylistId.class, playlistIdMetadata.getJavaType());

	}

	@Test
	void testConstruct_WhenHashKeyMethodExists_WhenRangeKeyMethodDoesNotExist() {
		assertThatThrownBy(() -> new DynamoDBHashAndRangeKeyMethodExtractorImpl<IdClassWithOnlyAnnotatedHashKeyMethod>(
				IdClassWithOnlyAnnotatedHashKeyMethod.class)).isInstanceOf(IllegalArgumentException.class);

	}

	@Test
	void testConstruct_WhenHashKeyMethodDoesNotExist_WhenRangeKeyMethodExists() {
		assertThatThrownBy(() -> new DynamoDBHashAndRangeKeyMethodExtractorImpl<IdClassWithOnlyAnnotatedRangeKeyMethod>(
				IdClassWithOnlyAnnotatedRangeKeyMethod.class)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testConstruct_WhenMultipleHashKeyMethodsExist() {
		assertThatThrownBy(() -> new DynamoDBHashAndRangeKeyMethodExtractorImpl<IdClassWithMulitpleAnnotatedHashKeyMethods>(
				IdClassWithMulitpleAnnotatedHashKeyMethods.class)).isInstanceOf(IllegalArgumentException.class);

	}

	@Test
	void testGetConstruct_WhenMultipleRangeKeyMethodsExist() {
		assertThatThrownBy(() -> new DynamoDBHashAndRangeKeyMethodExtractorImpl<IdClassWithMulitpleAnnotatedRangeKeyMethods>(
				IdClassWithMulitpleAnnotatedRangeKeyMethods.class)).isInstanceOf(IllegalArgumentException.class);

	}

	@Test
	void testConstruct_WhenNeitherHashKeyOrRangeKeyMethodExist() {
		assertThatThrownBy(() -> new DynamoDBHashAndRangeKeyMethodExtractorImpl<IdClassWithNoAnnotatedMethods>(
				IdClassWithNoAnnotatedMethods.class)).isInstanceOf(IllegalArgumentException.class);

	}

	private class IdClassWithNoAnnotatedMethods {

		public String getHashKey() {
			return null;
		}
		public String getRangeKey() {
			return null;
		}

	}

	private class IdClassWithOnlyAnnotatedHashKeyMethod {

		@DynamoDBHashKey
		public String getHashKey() {
			return null;
		}
		public String getRangeKey() {
			return null;
		}

	}

	private class IdClassWithOnlyAnnotatedRangeKeyMethod {

		public String getHashKey() {
			return null;
		}

		@DynamoDBRangeKey
		public String getRangeKey() {
			return null;
		}

	}

	private class IdClassWithMulitpleAnnotatedHashKeyMethods {

		@DynamoDBHashKey
		public String getHashKey() {
			return null;
		}

		@DynamoDBHashKey
		public String getOtherHashKey() {
			return null;
		}

		@DynamoDBRangeKey
		public String getRangeKey() {
			return null;
		}

	}

	private class IdClassWithMulitpleAnnotatedRangeKeyMethods {

		@DynamoDBHashKey
		public String getHashKey() {
			return null;
		}

		@DynamoDBRangeKey
		public String getOtherRangeKey() {
			return null;
		}

		@DynamoDBRangeKey
		public String getRangeKey() {
			return null;
		}

	}

}

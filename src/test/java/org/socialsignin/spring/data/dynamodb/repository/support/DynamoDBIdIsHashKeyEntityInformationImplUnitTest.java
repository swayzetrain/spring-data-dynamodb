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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.socialsignin.spring.data.dynamodb.domain.sample.Playlist;
import org.socialsignin.spring.data.dynamodb.domain.sample.PlaylistId;
import org.socialsignin.spring.data.dynamodb.domain.sample.User;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
class DynamoDBIdIsHashKeyEntityInformationImplUnitTest {

	private DynamoDBIdIsHashKeyEntityInformationImpl<Playlist, PlaylistId> dynamoDBPlaylistEntityInformation;

	private DynamoDBIdIsHashKeyEntityInformationImpl<User, String> dynamoDBUserEntityInformation;

	@Mock
	private DynamoDBHashAndRangeKeyExtractingEntityMetadata<Playlist, String> mockPlaylistEntityMetadata;

	@Mock
	private DynamoDBHashKeyExtractingEntityMetadata<User> mockUserEntityMetadata;

	@Mock
	private Object mockHashKey;

	@Mock
	private User mockUserPrototype;

	@Mock
	private Playlist mockPlaylistPrototype;

	@SuppressWarnings("deprecation")
	@Mock
	private DynamoDBMarshaller<Object> mockPropertyMarshaller;

	@BeforeEach
	void setup() {

		lenient().when(mockUserEntityMetadata.getHashKeyPropertyName()).thenReturn("userHashKeyPropertyName");
		lenient().when(mockPlaylistEntityMetadata.getHashKeyPropertyName()).thenReturn("playlistHashKeyPropertyName");
		lenient().when(mockUserEntityMetadata.getMarshallerForProperty("marshalledProperty"))
				.thenReturn(mockPropertyMarshaller);
		lenient().when(mockPlaylistEntityMetadata.getMarshallerForProperty("marshalledProperty"))
				.thenReturn(mockPropertyMarshaller);
		lenient().when(mockUserEntityMetadata.getOverriddenAttributeName("overriddenProperty"))
				.thenReturn(Optional.of("modifiedPropertyName"));
		lenient().when(mockPlaylistEntityMetadata.getOverriddenAttributeName("overriddenProperty"))
				.thenReturn(Optional.of("modifiedPropertyName"));

		lenient().when(mockUserEntityMetadata.isHashKeyProperty("hashKeyProperty")).thenReturn(true);
		lenient().when(mockPlaylistEntityMetadata.isHashKeyProperty("nonHashKeyProperty")).thenReturn(false);

		dynamoDBPlaylistEntityInformation = new DynamoDBIdIsHashKeyEntityInformationImpl<>(Playlist.class,
				mockPlaylistEntityMetadata);
		dynamoDBUserEntityInformation = new DynamoDBIdIsHashKeyEntityInformationImpl<>(User.class,
				mockUserEntityMetadata);
	}

	@Test
	void testGetId_WhenHashKeyTypeSameAsIdType_InvokesHashKeyMethod_AndReturnedIdIsAssignableToIdType_AndIsValueExpected() {
		User user = new User();
		user.setId("someUserId");
		String id = dynamoDBUserEntityInformation.getId(user);
		assertEquals("someUserId", id);

	}

	@Test
	void testGetId_WhenHashKeyMethodNotSameAsIdType_InvokesHashKeyMethod_AndReturnedIdIsNotAssignableToIdType() {
		Playlist playlist = new Playlist();
		playlist.setUserName("someUserName");
		playlist.setPlaylistName("somePlaylistName");

		assertThatThrownBy(() -> dynamoDBPlaylistEntityInformation.getId(playlist)).isInstanceOf(ClassCastException.class);
	}

	@Test
	void testGetHashKeyGivenId_WhenHashKeyTypeSameAsIdType_ReturnsId() {
		Object hashKey = dynamoDBUserEntityInformation.getHashKey("someUserId");
		assertNotNull(hashKey);
		assertEquals("someUserId", hashKey);
	}

	@Test
	void testGetHashKeyGivenId_WhenHashKeyTypeNotSameAsIdType_ThrowsIllegalArgumentException() {
		PlaylistId id = new PlaylistId();
		assertThatThrownBy(() -> dynamoDBPlaylistEntityInformation.getHashKey(id)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testGetJavaType_WhenEntityIsInstanceWithHashAndRangeKey_ReturnsEntityClass() {
		assertEquals(Playlist.class, dynamoDBPlaylistEntityInformation.getJavaType());
	}

	@Test
	void testGetJavaType_WhenEntityIsInstanceWithHashKeyOnly_ReturnsEntityClass() {
		assertEquals(User.class, dynamoDBUserEntityInformation.getJavaType());
	}

	@Test
	void testGetIdType_WhenEntityIsInstanceWithHashAndRangeKey_ReturnsReturnTypeOfHashKeyMethod() {
		assertEquals(String.class, dynamoDBPlaylistEntityInformation.getIdType());
	}

	@Test
	void testGetIdType_WhenEntityIsInstanceWithHashKeyOnly_ReturnsReturnTypeOfHashKeyMethod() {
		assertEquals(String.class, dynamoDBUserEntityInformation.getIdType());
	}

	// The following tests ensure that invarient methods such as those always
	// retuning constants, or
	// that delegate to metadata, behave the same irrespective of the setup of the
	// EntityInformation

	@Test
	void testGetRangeKey_ReturnsNull_IrrespectiveOfEntityInformationSetup() {
		Object userRangeKey = dynamoDBUserEntityInformation.getRangeKey("someUserId");
		assertNull(userRangeKey);

		Object playlistRangeKey = dynamoDBPlaylistEntityInformation.getRangeKey(new PlaylistId());
		assertNull(playlistRangeKey);
	}

	@Test
	void testIsRangeKeyAware_ReturnsFalse_IrrespectiveOfEntityInformationSetup() {
		assertFalse(dynamoDBUserEntityInformation.isRangeKeyAware());

		assertFalse(dynamoDBPlaylistEntityInformation.isRangeKeyAware());
	}

	@Test
	void testGetHashKeyPropertyName_DelegatesToEntityMetadata_IrrespectiveOfEntityInformationSetup() {
		assertEquals("userHashKeyPropertyName", dynamoDBUserEntityInformation.getHashKeyPropertyName());
		assertEquals("playlistHashKeyPropertyName", dynamoDBPlaylistEntityInformation.getHashKeyPropertyName());

	}

	@Test
	void testGetMarshallerForProperty_DelegatesToEntityMetadata_IrrespectiveOfEntityInformationSetup() {
		@SuppressWarnings("deprecation")
		DynamoDBMarshaller<?> marshaller1 = dynamoDBPlaylistEntityInformation
				.getMarshallerForProperty("marshalledProperty");
		assertEquals(mockPropertyMarshaller, marshaller1);

		@SuppressWarnings("deprecation")
		DynamoDBMarshaller<?> marshaller2 = dynamoDBUserEntityInformation
				.getMarshallerForProperty("marshalledProperty");
		assertEquals(mockPropertyMarshaller, marshaller2);
	}

	@Test
	void testGetIsHashKeyProperty_DelegatesToEntityMetadata_IrrespectiveOfEntityInformationSetup() {
		assertTrue(dynamoDBUserEntityInformation.isHashKeyProperty("hashKeyProperty"));
		assertTrue(dynamoDBUserEntityInformation.isHashKeyProperty("hashKeyProperty"));

		assertFalse(dynamoDBPlaylistEntityInformation.isHashKeyProperty("nonHashKeyProperty"));
		assertFalse(dynamoDBPlaylistEntityInformation.isHashKeyProperty("nonHashKeyProperty"));
	}

	@Test
	void testGetIsCompositeIdProperty_ReturnsFalse_IrrespectiveOfEntityInformationSetup() {
		assertFalse(dynamoDBUserEntityInformation.isCompositeHashAndRangeKeyProperty("compositeIdProperty"));
		assertFalse(dynamoDBUserEntityInformation.isCompositeHashAndRangeKeyProperty("compositeIdProperty"));

		assertFalse(
				dynamoDBPlaylistEntityInformation.isCompositeHashAndRangeKeyProperty("nonCompositeIdProperty"));
		assertFalse(
				dynamoDBPlaylistEntityInformation.isCompositeHashAndRangeKeyProperty("nonCompositeIdProperty"));
	}

	@Test
	void testGetOverriddenAttributeName_DelegatesToEntityMetadata_IrrespectiveOfEntityInformationSetup() {
		Optional<String> propertyName1 = dynamoDBUserEntityInformation.getOverriddenAttributeName("overriddenProperty");
		assertEquals(Optional.of("modifiedPropertyName"), propertyName1);

		Optional<String> propertyName2 = dynamoDBPlaylistEntityInformation
				.getOverriddenAttributeName("overriddenProperty");
		assertEquals(Optional.of("modifiedPropertyName"), propertyName2);
	}

}

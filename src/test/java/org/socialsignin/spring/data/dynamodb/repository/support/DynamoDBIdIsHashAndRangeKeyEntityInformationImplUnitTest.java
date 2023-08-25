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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.socialsignin.spring.data.dynamodb.domain.sample.Playlist;
import org.socialsignin.spring.data.dynamodb.domain.sample.PlaylistId;
import org.socialsignin.spring.data.dynamodb.domain.sample.User;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;

@ExtendWith(MockitoExtension.class)
class DynamoDBIdIsHashAndRangeKeyEntityInformationImplUnitTest {

	private DynamoDBIdIsHashAndRangeKeyEntityInformationImpl<Playlist, PlaylistId> dynamoDBPlaylistEntityInformation;

	@Mock
	private DynamoDBHashAndRangeKeyExtractingEntityMetadata<Playlist, PlaylistId> mockPlaylistEntityMetadata;

	@Mock
	private DynamoDBHashAndRangeKeyExtractingEntityMetadata<User, String> mockUserEntityMetadata;

	@Mock
	private Object mockHashKey;

	@Mock
	private Object mockRangeKey;

	@SuppressWarnings("rawtypes")
	@Mock
	private HashAndRangeKeyExtractor mockHashAndRangeKeyExtractor;

	@Mock
	private User mockUserPrototype;

	@Mock
	private Playlist mockPlaylistPrototype;

	@Mock
	private PlaylistId mockPlaylistId;

	@SuppressWarnings("deprecation")
	@Mock
	private DynamoDBMarshaller<Object> mockPropertyMarshaller;

	@SuppressWarnings("unchecked")
	@BeforeEach
	void setup() {
		lenient().when(mockPlaylistEntityMetadata.getHashAndRangeKeyExtractor(PlaylistId.class))
				.thenReturn(mockHashAndRangeKeyExtractor);
		lenient().when(mockHashAndRangeKeyExtractor.getHashKey(mockPlaylistId)).thenReturn(mockHashKey);
		lenient().when(mockHashAndRangeKeyExtractor.getRangeKey(mockPlaylistId)).thenReturn(mockRangeKey);

		lenient().when(mockPlaylistEntityMetadata.getHashKeyPropertyName()).thenReturn("playlistHashKeyPropertyName");
		lenient().when(mockPlaylistEntityMetadata.getHashKeyPropotypeEntityForHashKey("somePlaylistHashKey"))
				.thenReturn(mockPlaylistPrototype);
		lenient().when(mockPlaylistEntityMetadata.getMarshallerForProperty("marshalledProperty"))
				.thenReturn(mockPropertyMarshaller);
		lenient().when(mockPlaylistEntityMetadata.getOverriddenAttributeName("overriddenProperty"))
				.thenReturn(Optional.of("modifiedPropertyName"));

		lenient().when(mockPlaylistEntityMetadata.isHashKeyProperty("nonHashKeyProperty")).thenReturn(false);
		lenient().when(mockPlaylistEntityMetadata.isCompositeHashAndRangeKeyProperty("compositeIdProperty"))
				.thenReturn(true);
		lenient().when(mockPlaylistEntityMetadata.isCompositeHashAndRangeKeyProperty("nonCompositeIdProperty"))
				.thenReturn(false);

		dynamoDBPlaylistEntityInformation = new DynamoDBIdIsHashAndRangeKeyEntityInformationImpl<>(Playlist.class,
				mockPlaylistEntityMetadata);

	}

	@Test
	void testConstruct_WhenEntityDoesNotHaveFieldAnnotatedWithId_ThrowsIllegalArgumentException() {
		assertThatThrownBy(() -> new DynamoDBIdIsHashAndRangeKeyEntityInformationImpl<User, String>(User.class, mockUserEntityMetadata)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testGetId_WhenHashKeyMethodSameAsIdType_InvokesHashKeyMethod_AndReturnedIdIsAssignableToIdType_AndIsValueExpected() {
		Playlist playlist = new Playlist();
		playlist.setUserName("someUserName");
		playlist.setPlaylistName("somePlaylistName");
		PlaylistId id = dynamoDBPlaylistEntityInformation.getId(playlist);
		assertNotNull(id);
		assertEquals("someUserName", id.getUserName());
		assertEquals("somePlaylistName", id.getPlaylistName());
	}

	@Test
	void testGetJavaType_WhenEntityIsInstanceWithHashAndRangeKey_ReturnsEntityClass() {
		assertEquals(Playlist.class, dynamoDBPlaylistEntityInformation.getJavaType());
	}

	@Test
	void testGetIdType_WhenEntityIsInstanceWithHashAndRangeKey_ReturnsReturnTypeOfIdMethod() {
		assertEquals(PlaylistId.class, dynamoDBPlaylistEntityInformation.getIdType());
	}

	// The following tests ensure that invarient methods such as those always
	// retuning constants, or
	// that delegate to metadata, behave the same irrespective of the setup of the
	// EntityInformation

	@Test
	void testIsRangeKeyAware_ReturnsTrue() {
		assertTrue(dynamoDBPlaylistEntityInformation.isRangeKeyAware());
	}

	@Test
	void testGetHashKeyGivenId_WhenIdMethodFoundOnEntity_DelegatesToHashAndRangeKeyExtractorWithGivenIdValue() {
		Object hashKey = dynamoDBPlaylistEntityInformation.getHashKey(mockPlaylistId);
		assertNotNull(hashKey);
		assertEquals(mockHashKey, hashKey);
	}

	@Test
	void testGetRangeKeyGivenId_WhenIdMethodFoundOnEntity_DelegatesToHashAndRangeKeyExtractorWithGivenIdValue() {
		Object rangeKey = dynamoDBPlaylistEntityInformation.getRangeKey(mockPlaylistId);
		assertNotNull(rangeKey);
		assertEquals(mockRangeKey, rangeKey);
	}

	@Test
	void testGetPrototypeEntityForHashKey_DelegatesToDynamoDBEntityMetadata_IrrespectiveOfEntityInformationSetup() {
		Playlist playlistPrototypeEntity = new Playlist();
		Mockito.when(mockPlaylistEntityMetadata.getHashKeyPropotypeEntityForHashKey("someHashKey"))
				.thenReturn(playlistPrototypeEntity);

		Object returnedPlaylistEntity = dynamoDBPlaylistEntityInformation
				.getHashKeyPropotypeEntityForHashKey("someHashKey");

		assertEquals(playlistPrototypeEntity, returnedPlaylistEntity);
		Mockito.verify(mockPlaylistEntityMetadata).getHashKeyPropotypeEntityForHashKey("someHashKey");

	}

	@Test
	void testGetHashKeyPropertyName_DelegatesToEntityMetadata_IrrespectiveOfEntityInformationSetup() {
		assertEquals("playlistHashKeyPropertyName", dynamoDBPlaylistEntityInformation.getHashKeyPropertyName());

	}

	@Test
	void testGetHashKeyPrototypeEntityForHashKey_DelegatesToEntityMetadata_IrrespectiveOfEntityInformationSetup() {

		Object hashKeyPrototype2 = dynamoDBPlaylistEntityInformation
				.getHashKeyPropotypeEntityForHashKey("somePlaylistHashKey");
		assertEquals(mockPlaylistPrototype, hashKeyPrototype2);
	}

	@Test
	void testGetMarshallerForProperty_DelegatesToEntityMetadata_IrrespectiveOfEntityInformationSetup() {
		@SuppressWarnings("deprecation")
		DynamoDBMarshaller<?> marshaller1 = dynamoDBPlaylistEntityInformation
				.getMarshallerForProperty("marshalledProperty");
		assertEquals(mockPropertyMarshaller, marshaller1);

	}

	@Test
	void testGetOverriddenAttributeName_DelegatesToEntityMetadata_IrrespectiveOfEntityInformationSetup() {

		Optional<String> propertyName2 = dynamoDBPlaylistEntityInformation
				.getOverriddenAttributeName("overriddenProperty");
		assertEquals(Optional.of("modifiedPropertyName"), propertyName2);
	}

	@Test
	void testGetIsHashKeyProperty_DelegatesToEntityMetadata_IrrespectiveOfEntityInformationSetup() {

		assertFalse(dynamoDBPlaylistEntityInformation.isHashKeyProperty("nonHashKeyProperty"));
		assertFalse(dynamoDBPlaylistEntityInformation.isHashKeyProperty("nonHashKeyProperty"));
	}

	@Test
	void testGetIsCompositeIdProperty_DelegatesToEntityMetadata_IrrespectiveOfEntityInformationSetup() {

		assertTrue(dynamoDBPlaylistEntityInformation.isCompositeHashAndRangeKeyProperty("compositeIdProperty"));
		assertFalse(
				dynamoDBPlaylistEntityInformation.isCompositeHashAndRangeKeyProperty("nonCompositeIdProperty"));
	}

}

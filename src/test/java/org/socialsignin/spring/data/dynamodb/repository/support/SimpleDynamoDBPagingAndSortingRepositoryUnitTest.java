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
import static org.mockito.Mockito.lenient;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;
import org.socialsignin.spring.data.dynamodb.domain.sample.Playlist;
import org.socialsignin.spring.data.dynamodb.domain.sample.PlaylistId;
import org.socialsignin.spring.data.dynamodb.domain.sample.User;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 * Unit tests for {@link DynamoDBSimpleIdRepository}.
 * 
 * @author Michael Lavelle
 * @author Sebastian Just
 */
@ExtendWith(MockitoExtension.class)
public class SimpleDynamoDBPagingAndSortingRepositoryUnitTest {

	SimpleDynamoDBPagingAndSortingRepository<User, Long> repoForEntityWithOnlyHashKey;

	SimpleDynamoDBPagingAndSortingRepository<Playlist, PlaylistId> repoForEntityWithHashAndRangeKey;

	@Mock
	DynamoDBOperations dynamoDBOperations;

	private User testUser;

	private Playlist testPlaylist;

	private PlaylistId testPlaylistId;

	@Mock
	EnableScanPermissions mockEnableScanPermissions;

	@Mock
	DynamoDBEntityInformation<User, Long> entityWithOnlyHashKeyInformation;

	@Mock
	DynamoDBEntityInformation<Playlist, PlaylistId> entityWithHashAndRangeKeyInformation;

	@BeforeEach
	void setUp() {

		testUser = new User();

		testPlaylistId = new PlaylistId();
		testPlaylistId.setUserName("michael");
		testPlaylistId.setPlaylistName("playlist1");

		testPlaylist = new Playlist(testPlaylistId);

		lenient().when(entityWithOnlyHashKeyInformation.getJavaType()).thenReturn(User.class);
		lenient().when(entityWithOnlyHashKeyInformation.getHashKey(1l)).thenReturn(1l);

		lenient().when(entityWithHashAndRangeKeyInformation.getJavaType()).thenReturn(Playlist.class);
		lenient().when(entityWithHashAndRangeKeyInformation.getHashKey(testPlaylistId)).thenReturn("michael");
		lenient().when(entityWithHashAndRangeKeyInformation.getRangeKey(testPlaylistId)).thenReturn("playlist1");
		lenient().when(entityWithHashAndRangeKeyInformation.isRangeKeyAware()).thenReturn(true);

		repoForEntityWithOnlyHashKey = new SimpleDynamoDBPagingAndSortingRepository<>(entityWithOnlyHashKeyInformation,
				dynamoDBOperations, mockEnableScanPermissions);
		repoForEntityWithHashAndRangeKey = new SimpleDynamoDBPagingAndSortingRepository<>(
				entityWithHashAndRangeKeyInformation, dynamoDBOperations, mockEnableScanPermissions);

		lenient().when(dynamoDBOperations.load(User.class, 1l)).thenReturn(testUser);
		lenient().when(dynamoDBOperations.load(Playlist.class, "michael", "playlist1")).thenReturn(testPlaylist);

	}

	/**
	 * @see DATAJPA-177
	 */
	@Test
	void throwsExceptionIfEntityWithOnlyHashKeyToDeleteDoesNotExist() {

		assertThatThrownBy(() -> repoForEntityWithOnlyHashKey.deleteById(4711L)).isInstanceOf(EmptyResultDataAccessException.class);
	}

	@Test
	void findOneEntityWithOnlyHashKey() {
		Optional<User> user = repoForEntityWithOnlyHashKey.findById(1l);
		Mockito.verify(dynamoDBOperations).load(User.class, 1l);
		assertEquals(testUser, user.get());
	}

	@Test
	void findOneEntityWithHashAndRangeKey() {
		Optional<Playlist> playlist = repoForEntityWithHashAndRangeKey.findById(testPlaylistId);
		assertEquals(testPlaylist, playlist.get());
	}

	/**
	 * @see DATAJPA-177
	 */
	@Test
	void throwsExceptionIfEntityWithHashAndRangeKeyToDeleteDoesNotExist() {

		PlaylistId playlistId = new PlaylistId();
		playlistId.setUserName("someUser");
		playlistId.setPlaylistName("somePlaylistName");

		assertThatThrownBy(() -> repoForEntityWithHashAndRangeKey.deleteById(playlistId)).isInstanceOf(EmptyResultDataAccessException.class);
	}
}

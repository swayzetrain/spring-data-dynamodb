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
package org.socialsignin.spring.data.dynamodb.repository.query;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.socialsignin.spring.data.dynamodb.domain.sample.Playlist;
import org.socialsignin.spring.data.dynamodb.repository.support.DynamoDBIdIsHashAndRangeKeyEntityInformation;

@ExtendWith(MockitoExtension.class)
class DynamoDBEntityWithHashAndRangeKeyCriteriaUnitTest
		extends
			AbstractDynamoDBQueryCriteriaUnitTest<DynamoDBEntityWithHashAndRangeKeyCriteria<Playlist, String>> {

	@Mock
	private DynamoDBIdIsHashAndRangeKeyEntityInformation<Playlist, String> entityInformation;

	@BeforeEach
	public void setUp() {
		Mockito.when(entityInformation.getHashKeyPropertyName()).thenReturn("userName");
		Mockito.when(entityInformation.getRangeKeyPropertyName()).thenReturn("playlistName");
		criteria = new DynamoDBEntityWithHashAndRangeKeyCriteria<>(entityInformation, null);
	}

	@Test
	void testHasIndexHashKeyEqualConditionAnd_WhenConditionCriteriaIsEqualityOnAPropertyWhichIsAnIndexHashKeyButNotAHashKeyOrRangeKey() {
		Mockito.when(entityInformation.isGlobalIndexHashKeyProperty("displayName")).thenReturn(true);
		criteria.withPropertyEquals("displayName", "some display name", String.class);
		boolean hasIndexHashKeyEqualCondition = criteria.hasIndexHashKeyEqualCondition();
		assertTrue(hasIndexHashKeyEqualCondition);
	}

	@Test
	void testHasIndexHashKeyEqualCondition_WhenConditionCriteriaIsEqualityOnAPropertyWhichIsNotAnIndexHashKeyButIsAHashKey() {
		Mockito.when(entityInformation.isGlobalIndexHashKeyProperty("userName")).thenReturn(false);
		criteria.withPropertyEquals("userName", "some user name", String.class);
		boolean hasIndexHashKeyEqualCondition = criteria.hasIndexHashKeyEqualCondition();
		assertFalse(hasIndexHashKeyEqualCondition);
	}

	@Test
	void testHasIndexHashKeyEqualCondition_WhenConditionCriteriaIsEqualityOnAPropertyWhichIsNotAnIndexHashKeyButIsARangeKey() {
		Mockito.when(entityInformation.isGlobalIndexHashKeyProperty("playlistName")).thenReturn(false);
		criteria.withPropertyEquals("playlistName", "some playlist name", String.class);
		boolean hasIndexHashKeyEqualCondition = criteria.hasIndexHashKeyEqualCondition();
		assertFalse(hasIndexHashKeyEqualCondition);
	}

	@Test
	void testHasIndexHashKeyEqualCondition_WhenConditionCriteriaIsEqualityOnAPropertyWhichIsBothAnIndexHashKeyAndAHashKey() {
		Mockito.when(entityInformation.isGlobalIndexHashKeyProperty("userName")).thenReturn(true);
		criteria.withPropertyEquals("userName", "some user name", String.class);
		boolean hasIndexHashKeyEqualCondition = criteria.hasIndexHashKeyEqualCondition();
		assertTrue(hasIndexHashKeyEqualCondition);
	}

	@Test
	void testHasIndexHashKeyEqualCondition_WhenConditionCriteriaIsEqualityOnAPropertyWhichIsBothAnIndexHashKeyAndARangeKey() {
		Mockito.when(entityInformation.isGlobalIndexHashKeyProperty("playlistName")).thenReturn(true);
		criteria.withPropertyEquals("playlistName", "some playlist name", String.class);
		boolean hasIndexHashKeyEqualCondition = criteria.hasIndexHashKeyEqualCondition();
		assertTrue(hasIndexHashKeyEqualCondition);
	}

	@Test
	void testHasIndexHashKeyEqualCondition_WhenConditionCriteriaIsEqualityOnAPropertyWhichIsNeitherAnIndexHashKeyOrAHashKeyOrRangeKey() {
		Mockito.when(entityInformation.isGlobalIndexHashKeyProperty("displayName")).thenReturn(false);
		criteria.withPropertyEquals("displayName", "some display name", String.class);
		boolean hasIndexHashKeyEqualCondition = criteria.hasIndexHashKeyEqualCondition();
		assertFalse(hasIndexHashKeyEqualCondition);
	}

	@Test
	void testHasIndexHashKeyEqualCondition_WhenConditionCriteriaIsEqualityOnAPropertyWhichIsNeitherAnIndexHashKeyOrAHashKeyButIsRangeKey() {
		Mockito.when(entityInformation.isGlobalIndexHashKeyProperty("playlistName")).thenReturn(false);
		criteria.withPropertyEquals("playlistName", "some playlist name", String.class);
		boolean hasIndexHashKeyEqualCondition = criteria.hasIndexHashKeyEqualCondition();
		assertFalse(hasIndexHashKeyEqualCondition);
	}

	@Test
	void testHasIndexRangeKeyCondition_WhenConditionCriteriaIsEqualityOnAPropertyWhichIsAnIndexRangeKeyButNotAHashKeyOrRangeKey() {
		Mockito.when(entityInformation.isGlobalIndexRangeKeyProperty("displayName")).thenReturn(true);
		criteria.withPropertyEquals("displayName", "some display name", String.class);
		boolean hasIndexRangeKeyCondition = criteria.hasIndexRangeKeyCondition();
		assertTrue(hasIndexRangeKeyCondition);
	}

	@Test
	void testHasIndexRangeKeyCondition_WhenConditionCriteriaIsEqualityOnAPropertyWhichIsAnIndexRangeKeyButNotAHashKeyAndIsARangeKey() {
		Mockito.when(entityInformation.isGlobalIndexRangeKeyProperty("playlistName")).thenReturn(true);
		criteria.withPropertyEquals("playlistName", "some playlist name", String.class);
		boolean hasIndexRangeKeyCondition = criteria.hasIndexRangeKeyCondition();
		assertTrue(hasIndexRangeKeyCondition);
	}

	@Test
	void testHasIndexRangeKeyCondition_WhenConditionCriteriaIsEqualityOnAPropertyWhichIsNotAnIndexRangeKeyButIsAHashKey() {
		Mockito.when(entityInformation.isGlobalIndexRangeKeyProperty("userName")).thenReturn(false);
		criteria.withPropertyEquals("userName", "some user name", String.class);
		boolean hasIndexRangeKeyCondition = criteria.hasIndexRangeKeyCondition();
		assertFalse(hasIndexRangeKeyCondition);
	}

	@Test
	void testHasIndexRangeKeyCondition_WhenConditionCriteriaIsEqualityOnAPropertyWhichIsNotAnIndexRangeKeyButIsARangeKey() {
		criteria.withPropertyEquals("playlist name", "some playlist name", String.class);
		boolean hasIndexRangeKeyCondition = criteria.hasIndexRangeKeyCondition();
		assertFalse(hasIndexRangeKeyCondition);
	}

	@Test
	void testHasIndexRangeKeyCondition_WhenConditionCriteriaIsEqualityOnAPropertyWhichIsBothAnIndexRangeKeyAndAHashKey() {
		Mockito.when(entityInformation.isGlobalIndexRangeKeyProperty("userName")).thenReturn(true);
		criteria.withPropertyEquals("userName", "some user name", String.class);
		boolean hasIndexRangeKeyCondition = criteria.hasIndexRangeKeyCondition();
		assertTrue(hasIndexRangeKeyCondition);
	}

	@Test
	void testHasIndexRangeKeyCondition_WhenConditionCriteriaIsEqualityOnAPropertyWhichIsNeitherAnIndexRangeKeyOrAHashKeyOrARangeKey() {
		Mockito.when(entityInformation.isGlobalIndexRangeKeyProperty("displayName")).thenReturn(false);
		criteria.withPropertyEquals("displayName", "some display name", String.class);
		boolean hasIndexRangeKeyCondition = criteria.hasIndexRangeKeyCondition();
		assertFalse(hasIndexRangeKeyCondition);
	}

	@Test
	void testHasIndexRangeKeyCondition_WhenConditionCriteriaIsEqualityOnAPropertyWhichIsNeitherAnIndexRangeKeyOrAHashKeyButIsARangeKey() {
		Mockito.when(entityInformation.isGlobalIndexRangeKeyProperty("playlistName")).thenReturn(false);
		criteria.withPropertyEquals("playlistName", "some playlist name", String.class);
		boolean hasIndexRangeKeyCondition = criteria.hasIndexRangeKeyCondition();
		assertFalse(hasIndexRangeKeyCondition);
	}

	// repeat

	@Test
	void testHasIndexHashKeyEqualConditionAnd_WhenConditionCriteriaIsNonEqualityConditionOnAPropertyWhichIsAnIndexHashKeyButNotAHashKeyOrRangeKey() {
		Mockito.when(entityInformation.isGlobalIndexHashKeyProperty("displayName")).thenReturn(true);
		criteria.withPropertyBetween("displayName", "some display name", "some other display name", String.class);
		boolean hasIndexHashKeyEqualCondition = criteria.hasIndexHashKeyEqualCondition();
		assertFalse(hasIndexHashKeyEqualCondition);
	}

	@Test
	void testHasIndexHashKeyEqualConditionAnd_WhenConditionCriteriaIsNonEqualityConditionOnAPropertyWhichIsAnIndexHashKeyButNotAHashKeyButIsARangeKey() {
		Mockito.when(entityInformation.isGlobalIndexHashKeyProperty("playlistName")).thenReturn(true);
		criteria.withPropertyBetween("playlistName", "some playlist name", "some other playlist name", String.class);
		boolean hasIndexHashKeyEqualCondition = criteria.hasIndexHashKeyEqualCondition();
		assertFalse(hasIndexHashKeyEqualCondition);
	}

	@Test
	void testHasIndexHashKeyEqualCondition_WhenConditionCriteriaIsNonEqualityConditionOnAPropertyWhichIsNotAnIndexHashKeyButIsAHashKey() {
		Mockito.when(entityInformation.isGlobalIndexHashKeyProperty("userName")).thenReturn(false);
		criteria.withPropertyBetween("userName", "some user name", "some other user name", String.class);
		boolean hasIndexHashKeyEqualCondition = criteria.hasIndexHashKeyEqualCondition();
		assertFalse(hasIndexHashKeyEqualCondition);
	}

	@Test
	void testHasIndexHashKeyEqualCondition_WhenConditionCriteriaIsNonEqualityConditionOnAPropertyWhichIsBothAnIndexHashKeyAndAHashKey() {
		Mockito.when(entityInformation.isGlobalIndexHashKeyProperty("userName")).thenReturn(true);
		criteria.withPropertyBetween("userName", "some user name", "some other user name", String.class);
		boolean hasIndexHashKeyEqualCondition = criteria.hasIndexHashKeyEqualCondition();
		assertFalse(hasIndexHashKeyEqualCondition);
	}

	@Test
	void testHasIndexHashKeyEqualCondition_WhenConditionCriteriaIsNonEqualityConditionOnAPropertyWhichIsNeitherAnIndexHashKeyOrAHashKeyOrARangeKey() {
		Mockito.when(entityInformation.isGlobalIndexHashKeyProperty("displayName")).thenReturn(false);
		criteria.withPropertyBetween("displayName", "some display name", "some other display name", String.class);
		boolean hasIndexHashKeyEqualCondition = criteria.hasIndexHashKeyEqualCondition();
		assertFalse(hasIndexHashKeyEqualCondition);
	}

	@Test
	void testHasIndexHashKeyEqualCondition_WhenConditionCriteriaIsNonEqualityConditionOnAPropertyWhichIsNeitherAnIndexHashKeyOrAHashKeyButIsARangeKey() {
		Mockito.when(entityInformation.isGlobalIndexHashKeyProperty("playlistName")).thenReturn(false);
		criteria.withPropertyBetween("playlistName", "some playlist name", "some other playlist name", String.class);
		boolean hasIndexHashKeyEqualCondition = criteria.hasIndexHashKeyEqualCondition();
		assertFalse(hasIndexHashKeyEqualCondition);
	}

	@Test
	void testHasIndexRangeKeyCondition_WhenConditionCriteriaIsNonEqualityConditionOnAPropertyWhichIsAnIndexRangeKeyButNotAHashKeyOrARangeKey() {
		Mockito.when(entityInformation.isGlobalIndexRangeKeyProperty("displayName")).thenReturn(true);
		criteria.withPropertyBetween("displayName", "some display name", "some other display name", String.class);
		boolean hasIndexRangeKeyCondition = criteria.hasIndexRangeKeyCondition();
		assertTrue(hasIndexRangeKeyCondition);
	}

	@Test
	void testHasIndexRangeKeyCondition_WhenConditionCriteriaIsNonEqualityConditionOnAPropertyWhichIsAnIndexRangeKeyButNotAHashKeyButIsARangeKey() {
		Mockito.when(entityInformation.isGlobalIndexRangeKeyProperty("playlistName")).thenReturn(true);
		criteria.withPropertyBetween("playlistName", "some playlist name", "some other playlist name", String.class);
		boolean hasIndexRangeKeyCondition = criteria.hasIndexRangeKeyCondition();
		assertTrue(hasIndexRangeKeyCondition);
	}

	@Test
	void testHasIndexRangeKeyCondition_WhenConditionCriteriaIsNonEqualityConditionOnAPropertyWhichIsNotAnIndexRangeKeyButIsAHashKey() {
		Mockito.when(entityInformation.isGlobalIndexRangeKeyProperty("userName")).thenReturn(false);
		criteria.withPropertyBetween("userName", "some user name", "some other user name", String.class);
		boolean hasIndexRangeKeyCondition = criteria.hasIndexRangeKeyCondition();
		assertFalse(hasIndexRangeKeyCondition);
	}

	@Test
	void testHasIndexRangeKeyCondition_WhenConditionCriteriaIsNonEqualityConditionOnAPropertyWhichIsBothAnIndexRangeKeyAndAHashKey() {
		Mockito.when(entityInformation.isGlobalIndexRangeKeyProperty("userName")).thenReturn(true);
		criteria.withPropertyBetween("userName", "some user name", "some other user name", String.class);
		boolean hasIndexRangeKeyCondition = criteria.hasIndexRangeKeyCondition();
		assertTrue(hasIndexRangeKeyCondition);
	}

	@Test
	void testHasIndexRangeKeyCondition_WhenConditionCriteriaIsNonEqualityConditionOnAPropertyWhichIsNeitherAnIndexRangeKeyOrAHashKeyOrARangeKey() {
		Mockito.when(entityInformation.isGlobalIndexRangeKeyProperty("displayName")).thenReturn(false);
		criteria.withPropertyBetween("displayName", "some display name", "some other display name", String.class);
		boolean hasIndexRangeKeyCondition = criteria.hasIndexRangeKeyCondition();
		assertFalse(hasIndexRangeKeyCondition);
	}

	@Test
	void testHasIndexRangeKeyCondition_WhenConditionCriteriaIsNonEqualityConditionOnAPropertyWhichIsNeitherAnIndexRangeKeyOrAHashKeyButIsARangeKey() {
		Mockito.when(entityInformation.isGlobalIndexRangeKeyProperty("playlistName")).thenReturn(false);
		criteria.withPropertyBetween("playlistName", "some playlist name", "some other playlist name", String.class);
		boolean hasIndexRangeKeyCondition = criteria.hasIndexRangeKeyCondition();
		assertFalse(hasIndexRangeKeyCondition);
	}

}

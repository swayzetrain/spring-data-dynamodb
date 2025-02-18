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
package org.socialsignin.spring.data.dynamodb.domain.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

/**
 * Show the usage of Hash+Range key as also how to use XML based configuration
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:META-INF/context/HashRangeKeyIT-context.xml"})
class HashRangeKeyIT {

	@Autowired
	private PlaylistRepository playlistRepository;

	@Autowired
	private AmazonDynamoDB ddb;

	@BeforeEach
	public void setUp() {
		CreateTableRequest ctr = new DynamoDBMapper(ddb).generateCreateTableRequest(Playlist.class);
		ctr.withProvisionedThroughput(new ProvisionedThroughput(10L, 10L));
		ddb.createTable(ctr);
	}

	@Test
	void runCrudOperations() {
		final String displayName = "displayName" + UUID.randomUUID().toString();
		final String userName = "userName-" + UUID.randomUUID().toString();
		final String playlistName = "playlistName-" + UUID.randomUUID().toString();
		PlaylistId id = new PlaylistId(userName, playlistName);

		Optional<Playlist> actual = playlistRepository.findById(id);
		assertFalse(actual.isPresent());

		Playlist playlist = new Playlist(id);
		playlist.setDisplayName(displayName);

		playlistRepository.save(playlist);

		actual = playlistRepository.findById(id);
		assertTrue(actual.isPresent());
		assertEquals(displayName, actual.get().getDisplayName());
		assertEquals(id.getPlaylistName(), actual.get().getPlaylistName());
		assertEquals(id.getUserName(), actual.get().getUserName());
	}
}

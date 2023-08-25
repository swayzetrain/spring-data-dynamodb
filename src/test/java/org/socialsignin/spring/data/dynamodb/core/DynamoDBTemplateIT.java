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
package org.socialsignin.spring.data.dynamodb.core;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.socialsignin.spring.data.dynamodb.domain.sample.User;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.socialsignin.spring.data.dynamodb.utils.DynamoDBLocalResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

/**
 * Integration test that interacts with DynamoDB Local instance.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DynamoDBLocalResource.class, DynamoDBTemplateIT.TestAppConfig.class})
@TestPropertySource(properties = {"spring.data.dynamodb.entity2ddl.auto=create"})
class DynamoDBTemplateIT {

	@Autowired
	private AmazonDynamoDB amazonDynamoDB;
	@Autowired
	private DynamoDBTemplate dynamoDBTemplate;

	@Configuration
	@EnableDynamoDBRepositories(basePackages = "org.socialsignin.spring.data.dynamodb.domain.sample")
	public static class TestAppConfig {
	}

	@Test
	void testUser_CRUD() {

		// Given a entity to save.
		User user = new User();
		user.setName("John Doe");
		user.setNumberOfPlaylists(10);
		user.setId(UUID.randomUUID().toString());

		// Save it to DB.
		dynamoDBTemplate.save(user);

		// Retrieve it from DB.
		User retrievedUser = dynamoDBTemplate.load(User.class, user.getId());

		// Verify the details on the entity.
		assert retrievedUser.getName().equals(user.getName());
		assert retrievedUser.getId().equals(user.getId());
		assert retrievedUser.getNumberOfPlaylists() == user.getNumberOfPlaylists();

		// Update the entity and save.
		retrievedUser.setNumberOfPlaylists(20);
		dynamoDBTemplate.save(retrievedUser);

		retrievedUser = dynamoDBTemplate.load(User.class, user.getId());

		assert retrievedUser.getNumberOfPlaylists() == 20;

		// Delete.
		dynamoDBTemplate.delete(retrievedUser);

		// Get again.
		assert dynamoDBTemplate.load(User.class, user.getId()) == null;
	}

}

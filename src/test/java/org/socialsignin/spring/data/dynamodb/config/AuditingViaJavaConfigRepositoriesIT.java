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
package org.socialsignin.spring.data.dynamodb.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialsignin.spring.data.dynamodb.domain.sample.AuditableUser;
import org.socialsignin.spring.data.dynamodb.domain.sample.AuditableUserRepository;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.socialsignin.spring.data.dynamodb.utils.DynamoDBLocalResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Integration tests for auditing via Java config.
 * 
 * @author Vito Limandibhrata
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DynamoDBLocalResource.class, AuditingViaJavaConfigRepositoriesIT.TestAppConfig.class})
@TestPropertySource(properties = {"spring.data.dynamodb.entity2ddl.auto=create"})
class AuditingViaJavaConfigRepositoriesIT {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuditingViaJavaConfigRepositoriesIT.class);

	@Configuration
	@EnableDynamoDBAuditing(auditorAwareRef = "auditorProvider")
	@EnableDynamoDBRepositories(mappingContextRef = "dynamoDBMappingContext", basePackages = "org.socialsignin.spring.data.dynamodb.domain.sample")
	public static class TestAppConfig {

		@SuppressWarnings("unchecked")
		@Bean(name = "auditorProvider")
		public AuditorAware<AuditableUser> auditorProvider() {
			LOGGER.info("mocked auditorProvider provided");
			return Mockito.mock(AuditorAware.class);
		}
	}

	@Autowired
	AuditableUserRepository auditableUserRepository;

	@Autowired
	AuditorAware<AuditableUser> auditorAware;

	AuditableUser auditor;

	@BeforeEach
	void setUp() throws InterruptedException {
		this.auditor = auditableUserRepository.save(new AuditableUser("auditor"));
		assertThat(this.auditor).isNotNull();

		Optional<AuditableUser> auditorUser = auditableUserRepository.findById(this.auditor.getId());
		assertThat(auditorUser).isPresent();

	}

	@Test
	void basicAuditing() {

		doReturn(Optional.of(this.auditor.getId())).when(this.auditorAware).getCurrentAuditor();

		AuditableUser savedUser = auditableUserRepository.save(new AuditableUser("user"));

		assertThat(savedUser.getCreatedAt()).isNotNull();
		assertThat(savedUser.getCreatedBy()).isEqualTo(this.auditor.getId());

		assertThat(savedUser.getLastModifiedAt()).isNotNull();
		assertThat(savedUser.getLastModifiedBy()).isEqualTo(this.auditor.getId());

	}

}

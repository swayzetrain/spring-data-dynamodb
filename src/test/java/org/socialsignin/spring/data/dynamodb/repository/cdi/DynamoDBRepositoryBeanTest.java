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
package org.socialsignin.spring.data.dynamodb.repository.cdi;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;
import org.socialsignin.spring.data.dynamodb.domain.sample.User;
import org.springframework.data.repository.Repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;

@ExtendWith(MockitoExtension.class)
class DynamoDBRepositoryBeanTest {
	interface SampleRepository extends Repository<User, String> {
	}

	@Mock
	private CreationalContext<AmazonDynamoDB> creationalContext;
	@Mock
	private CreationalContext<SampleRepository> repoCreationalContext;
	@Mock
	private BeanManager beanManager;
	@Mock
	private Bean<AmazonDynamoDB> amazonDynamoDBBean;
	@Mock
	private AmazonDynamoDB amazonDynamoDB;
	@Mock
	private jakarta.enterprise.inject.spi.Bean<DynamoDBMapperConfig> dynamoDBMapperConfigBean;
	@Mock
	private Bean<DynamoDBOperations> dynamoDBOperationsBean;

	@Mock
	private Bean<DynamoDBMapper> dynamoDBMapperBean;
	private Set<Annotation> qualifiers = Collections.emptySet();
	private Class<SampleRepository> repositoryType = SampleRepository.class;

	@BeforeEach
	public void setUp() {
		lenient().when(beanManager.createCreationalContext(amazonDynamoDBBean)).thenReturn(creationalContext);
		lenient().when(beanManager.getReference(amazonDynamoDBBean, AmazonDynamoDB.class, creationalContext))
				.thenReturn(amazonDynamoDB);
	}

	@Test
	void testNullOperationsOk() {
		DynamoDBRepositoryBean<SampleRepository> underTest = new DynamoDBRepositoryBean<>(beanManager,
				amazonDynamoDBBean, dynamoDBMapperConfigBean, null, dynamoDBMapperBean, qualifiers, repositoryType);

		assertNotNull(underTest);
	}

	@Test
	void testNullOperationFail() {
		assertThatThrownBy(() -> new DynamoDBRepositoryBean<>(beanManager, null, dynamoDBMapperConfigBean, null, null, qualifiers, repositoryType)).isInstanceOf(RuntimeException.class);
	}

	@Test
	void testSetOperationOk1() {
		DynamoDBRepositoryBean<SampleRepository> underTest = new DynamoDBRepositoryBean<>(beanManager, null, null,
				dynamoDBOperationsBean, dynamoDBMapperBean, qualifiers, repositoryType);

		assertNotNull(underTest);
	}

	@Test
	void testSetOperationFail1() {
		assertThatThrownBy(() -> new DynamoDBRepositoryBean<>(beanManager, null, dynamoDBMapperConfigBean, dynamoDBOperationsBean, dynamoDBMapperBean, qualifiers,
				repositoryType)).isInstanceOf(RuntimeException.class);
	}

	@Test
	void testSetOperationFail2() {
		assertThatThrownBy(() -> new DynamoDBRepositoryBean<>(beanManager, amazonDynamoDBBean, null, dynamoDBOperationsBean, dynamoDBMapperBean, qualifiers,
				repositoryType)).isInstanceOf(RuntimeException.class);
	}

	@Test
	void testCreateRepostiory() {
		DynamoDBRepositoryBean<SampleRepository> underTest = new DynamoDBRepositoryBean<>(beanManager,
				amazonDynamoDBBean, dynamoDBMapperConfigBean, null, dynamoDBMapperBean, qualifiers, repositoryType);

		SampleRepository actual = underTest.create(repoCreationalContext, SampleRepository.class);
		assertNotNull(actual);
	}
}

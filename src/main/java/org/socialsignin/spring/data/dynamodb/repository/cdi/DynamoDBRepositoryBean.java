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

import java.lang.annotation.Annotation;
import java.util.Set;

import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBTemplate;
import org.socialsignin.spring.data.dynamodb.repository.support.DynamoDBRepositoryFactory;
import org.springframework.data.repository.cdi.CdiRepositoryBean;
import org.springframework.util.Assert;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;

/**
 * A bean which represents a DynamoDB repository.
 * 
 * @author Michael Lavelle
 * @author Sebastian Just
 * @param <T>
 *            The type of the repository.
 */
class DynamoDBRepositoryBean<T> extends CdiRepositoryBean<T> {
	private final Bean<AmazonDynamoDB> amazonDynamoDBBean;

	private final Bean<DynamoDBMapperConfig> dynamoDBMapperConfigBean;

	private final Bean<DynamoDBOperations> dynamoDBOperationsBean;

	private final Bean<DynamoDBMapper> dynamoDBMapperBean;

	/**
	 * Constructs a {@link DynamoDBRepositoryBean}.
	 * 
	 * @param beanManager
	 *            must not be {@literal null}.
	 * @param amazonDynamoDBBean
	 *            must not be {@literal null}.
	 * @param dynamoDBOperationsBean
	 *            must not be {@literal null}.
	 * @param qualifiers
	 *            must not be {@literal null}.
	 * @param repositoryType
	 *            must not be {@literal null}.
	 */
	DynamoDBRepositoryBean(BeanManager beanManager, Bean<AmazonDynamoDB> amazonDynamoDBBean,
			Bean<DynamoDBMapperConfig> dynamoDBMapperConfigBean, Bean<DynamoDBOperations> dynamoDBOperationsBean,
						   Bean<DynamoDBMapper> dynamoDBMapperBean, Set<Annotation> qualifiers, Class<T> repositoryType) {

		super(qualifiers, repositoryType, beanManager);
		if (dynamoDBOperationsBean == null) {
			Assert.notNull(amazonDynamoDBBean, "amazonDynamoDBBean must not be null!");
		} else {
			Assert.isNull(amazonDynamoDBBean,
					"Cannot specify both amazonDynamoDB bean and dynamoDBOperationsBean in repository configuration");
			Assert.isNull(dynamoDBMapperConfigBean,
					"Cannot specify both dynamoDBMapperConfigBean bean and dynamoDBOperationsBean in repository configuration");

		}
		this.amazonDynamoDBBean = amazonDynamoDBBean;
		this.dynamoDBMapperConfigBean = dynamoDBMapperConfigBean;
		this.dynamoDBOperationsBean = dynamoDBOperationsBean;
		this.dynamoDBMapperBean = dynamoDBMapperBean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.enterprise.context.spi.Contextual#create(javax.enterprise
	 * .context.spi.CreationalContext, Class<T>, Optional<Object>)
	 */
	@Override
	protected T create(CreationalContext<T> creationalContext, Class<T> repositoryType) {

		// Get an instance from the associated AmazonDynamoDB bean.
		AmazonDynamoDB amazonDynamoDB = getDependencyInstance(amazonDynamoDBBean, AmazonDynamoDB.class);

		// Get an instance from the associated optional AmazonDynamoDB bean.
		DynamoDBMapperConfig dynamoDBMapperConfig = dynamoDBMapperConfigBean == null
				? null
				: getDependencyInstance(dynamoDBMapperConfigBean, DynamoDBMapperConfig.class);

		DynamoDBMapper dynamoDBMapper = dynamoDBMapperBean == null
				? null
				: getDependencyInstance(dynamoDBMapperBean, DynamoDBMapper.class);

		DynamoDBOperations dynamoDBOperations = dynamoDBOperationsBean == null
				? null
				: getDependencyInstance(dynamoDBOperationsBean, DynamoDBOperations.class);

		if (dynamoDBMapperConfig == null) {
			dynamoDBMapperConfig = DynamoDBMapperConfig.DEFAULT;
		}
		if(dynamoDBMapper == null) {
			dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB, dynamoDBMapperConfig);
		}
		if (dynamoDBOperations == null) {
			dynamoDBOperations = new DynamoDBTemplate(amazonDynamoDB, dynamoDBMapper, dynamoDBMapperConfig);
		}

		DynamoDBRepositoryFactory factory = new DynamoDBRepositoryFactory(dynamoDBOperations);
		return factory.getRepository(repositoryType);
	}

}

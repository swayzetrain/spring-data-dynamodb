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

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;
import org.springframework.data.repository.cdi.CdiRepositoryExtensionSupport;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.UnsatisfiedResolutionException;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.ProcessBean;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A portable CDI extension which registers beans for Spring Data DynamoDB
 * repositories.
 * 
 * @author Michael Lavelle
 * @author Sebastian Just
 */
public class DynamoDBRepositoryExtension extends CdiRepositoryExtensionSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDBRepositoryExtension.class);

	private final Map<Set<Annotation>, Bean<AmazonDynamoDB>> amazonDynamoDBs = new HashMap<>();

	private final Map<Set<Annotation>, Bean<DynamoDBOperations>> dynamoDBOperationss = new HashMap<>();

	private final Map<Set<Annotation>, Bean<DynamoDBMapperConfig>> dbMapperConfigs = new HashMap<>();

	private final Map<Set<Annotation>, Bean<DynamoDBMapper>> dbMapper = new HashMap<>();
	public DynamoDBRepositoryExtension() {
		LOGGER.info("Activating CDI extension for Spring Data DynamoDB repositories.");
	}

	/**
	 * Implementation of a an observer which checks for AmazonDynamoDBClient beans
	 * and stores them in {@link #amazonDynamoDBClients} for later association with
	 * corresponding repository beans.
	 * 
	 * @param <X>
	 *            The type.
	 * @param processAnnotatedType
	 *            The annotated type as defined by CDI.
	 */
	@SuppressWarnings("unchecked")
	<X> void processBean(@Observes ProcessBean<X> processBean) {
		Bean<X> bean = processBean.getBean();
		for (Type type : bean.getTypes()) {
			// Check if the bean is a AmazonDynamoDB
			if (type instanceof Class<?> && AmazonDynamoDBClient.class.isAssignableFrom((Class<?>) type)) {
				Set<Annotation> qualifiers = new HashSet<>(bean.getQualifiers());
				if (bean.isAlternative() || !amazonDynamoDBs.containsKey(qualifiers)) {
					LOGGER.debug("Discovered '{}' with qualifiers {}.", AmazonDynamoDB.class.getName(), qualifiers);
					amazonDynamoDBs.put(qualifiers, (Bean<AmazonDynamoDB>) bean);
				}
			}
			// Check if the bean is a DynamoDBMapperConfig
			if (type instanceof Class<?> && DynamoDBMapperConfig.class.isAssignableFrom((Class<?>) type)) {
				Set<Annotation> qualifiers = new HashSet<>(bean.getQualifiers());
				if (bean.isAlternative() || !dbMapperConfigs.containsKey(qualifiers)) {
					LOGGER.debug("Discovered '{}' with qualifiers {}.", DynamoDBMapperConfig.class.getName(),
							qualifiers);
					dbMapperConfigs.put(qualifiers, (Bean<DynamoDBMapperConfig>) bean);
				}
			}
			if (type instanceof Class<?> && DynamoDBMapper.class.isAssignableFrom((Class<?>) type)) {
				Set<Annotation> qualifiers = new HashSet<>(bean.getQualifiers());
				if (bean.isAlternative() || !dbMapper.containsKey(qualifiers)) {
					LOGGER.debug("Discovered '{}' with qualifiers {}.", DynamoDBMapper.class.getName(),
							qualifiers);
					dbMapper.put(qualifiers, (Bean<DynamoDBMapper>) bean);
				}
			}
		}
	}

	/**
	 * Implementation of a an observer which registers beans to the CDI container
	 * for the detected Spring Data repositories.
	 * <p>
	 * The repository beans are associated to the EntityManagers using their
	 * qualifiers.
	 * 
	 * @param beanManager
	 *            The BeanManager instance.
	 */
	void afterBeanDiscovery(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager beanManager) {

		for (Entry<Class<?>, Set<Annotation>> entry : getRepositoryTypes()) {

			Class<?> repositoryType = entry.getKey();
			Set<Annotation> qualifiers = entry.getValue();
			// Create the bean representing the repository.
			Bean<?> repositoryBean = createRepositoryBean(repositoryType, qualifiers, beanManager);
			LOGGER.info("Registering bean for '{}' with qualifiers {}.", repositoryType.getName(), qualifiers);
			// Register the bean to the container.
			afterBeanDiscovery.addBean(repositoryBean);
		}
	}

	/**
	 * Creates a {@link Bean}.
	 * 
	 * @param <T>
	 *            The type of the repository.
	 * @param repositoryType
	 *            The class representing the repository.
	 * @param beanManager
	 *            The BeanManager instance.
	 * @return The bean.
	 */
	private <T> Bean<T> createRepositoryBean(Class<T> repositoryType, Set<Annotation> qualifiers,
			BeanManager beanManager) {

		// Determine the amazondbclient bean which matches the qualifiers of the
		// repository.
		Bean<AmazonDynamoDB> amazonDynamoDBBean = amazonDynamoDBs.get(qualifiers);

		// Determine the dynamo db mapper configbean which matches the
		// qualifiers of the repository.
		Bean<DynamoDBMapperConfig> dynamoDBMapperConfigBean = dbMapperConfigs.get(qualifiers);

		Bean<DynamoDBOperations> dynamoDBOperationsBean = dynamoDBOperationss.get(qualifiers);
		if (amazonDynamoDBBean == null) {
			throw new UnsatisfiedResolutionException(
					String.format("Unable to resolve a bean for '%s' with qualifiers %s.",
							AmazonDynamoDBClient.class.getName(), qualifiers));
		}

		Bean<DynamoDBMapper> dynamoDBMapperBean = dbMapper.get(qualifiers);

		// Construct and return the repository bean.
		return new DynamoDBRepositoryBean<>(beanManager, amazonDynamoDBBean, dynamoDBMapperConfigBean,
				dynamoDBOperationsBean, dynamoDBMapperBean, qualifiers, repositoryType);
	}
}

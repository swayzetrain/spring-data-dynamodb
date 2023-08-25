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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Unit tests for {@link DynamoDBAuditingRegistrar}.
 *
 * @author Vito Limandibhrata
 */
@ExtendWith(MockitoExtension.class)
class DynamoDBAuditingRegistrarUnitTests {

	DynamoDBAuditingRegistrar registrar = new DynamoDBAuditingRegistrar();

	@Mock
	AnnotationMetadata metadata;
	@Mock
	BeanDefinitionRegistry registry;

	@Test
	void rejectsNullAnnotationMetadata() {
		assertThatThrownBy(() -> registrar.registerBeanDefinitions(null, registry)).isInstanceOf(IllegalArgumentException.class);
		
	}

	@Test
	void rejectsNullBeanDefinitionRegistry() {		
		assertThatThrownBy(() -> registrar.registerBeanDefinitions(metadata, null)).isInstanceOf(IllegalArgumentException.class);
	}
}

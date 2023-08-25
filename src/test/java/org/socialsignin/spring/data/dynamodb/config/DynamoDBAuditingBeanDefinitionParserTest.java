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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.socialsignin.spring.data.dynamodb.config.BeanNames.MAPPING_CONTEXT_BEAN_NAME;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.socialsignin.spring.data.dynamodb.mapping.DynamoDBMappingContext;
import org.socialsignin.spring.data.dynamodb.mapping.event.AuditingEventListener;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.parsing.ReaderEventListener;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.DocumentDefaultsDefinition;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@ExtendWith(MockitoExtension.class)
class DynamoDBAuditingBeanDefinitionParserTest {

	private DynamoDBAuditingBeanDefinitionParser underTest;
	private Document configXmlDoc;

	private XmlBeanDefinitionReader reader;
	private XmlReaderContext readerContext;

	@Mock
	private BeanDefinitionParserDelegate beanDefinitionParserDelegate;
	private ParserContext parserContext;
	@Mock
	private BeanDefinitionRegistry registry;

	@Mock
	private BeanDefinitionBuilder builder;

	@Mock
	private BeanNameGenerator beanNameGenerator;
	@Mock
	private Resource resource;
	@Mock
	private ProblemReporter problemReporter;
	@Mock
	private ReaderEventListener eventListener;
	@Mock
	private SourceExtractor sourceExtractor;
	@Mock
	private NamespaceHandlerResolver namespaceHandlerResolver;
	private DocumentDefaultsDefinition documentDefaultsDefinition;

	@BeforeEach
	void setUp() throws Exception {
		underTest = new DynamoDBAuditingBeanDefinitionParser();

		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		configXmlDoc = documentBuilder.newDocument();

		reader = new XmlBeanDefinitionReader(registry);
		reader.setBeanNameGenerator(beanNameGenerator);
		documentDefaultsDefinition = new DocumentDefaultsDefinition();

		readerContext = new XmlReaderContext(resource, problemReporter, eventListener, sourceExtractor, reader,
				namespaceHandlerResolver);
		parserContext = new ParserContext(readerContext, beanDefinitionParserDelegate);
	}

	@Test
	void testShouldGenerateId() {

		assertThat(underTest.shouldGenerateId()).isTrue();
	}

	@Test
	void testDoParseWithMappingContext() {

		String mappingContextRef = MAPPING_CONTEXT_BEAN_NAME;
		Element configElement = configXmlDoc.createElement("config");
		configElement.setAttribute("mapping-context-ref", mappingContextRef);
		
		when(beanDefinitionParserDelegate.getDefaults()).thenReturn(documentDefaultsDefinition);
		when(beanNameGenerator.generateBeanName(any(), eq(registry))).thenReturn("dynamoDBMappingContext");

		underTest.doParse(configElement, parserContext, builder);

		verify(builder).addConstructorArgValue(any());
	}

	@Test
	void testDoParseWithoutMappingContextAutocreatingBean() {
		// mappingContextRef not set
		String mappingContextRef = null;
		Element configElement = configXmlDoc.createElement("config");
		configElement.setAttribute("mapping-context-ref", mappingContextRef);

		// Default bean not yet registered
		when(beanDefinitionParserDelegate.getDefaults()).thenReturn(documentDefaultsDefinition);
		when(beanNameGenerator.generateBeanName(any(), eq(registry))).thenReturn("dynamoDBMappingContext");
		when(registry.containsBeanDefinition(MAPPING_CONTEXT_BEAN_NAME)).thenReturn(false);

		underTest.doParse(configElement, parserContext, builder);

		verify(registry).registerBeanDefinition(MAPPING_CONTEXT_BEAN_NAME,
				new RootBeanDefinition(DynamoDBMappingContext.class));

		verify(builder).addConstructorArgValue(any());
	}

	@Test
	void testDoParseWithoutMappingContextReusingBean() {
		// mappingContextRef not set
		String mappingContextRef = null;
		Element configElement = configXmlDoc.createElement("config");
		configElement.setAttribute("mapping-context-ref", mappingContextRef);

		// Default bean not yet registered
		when(beanDefinitionParserDelegate.getDefaults()).thenReturn(documentDefaultsDefinition);
		when(beanNameGenerator.generateBeanName(any(), eq(registry))).thenReturn("dynamoDBMappingContext");
		when(registry.containsBeanDefinition(MAPPING_CONTEXT_BEAN_NAME)).thenReturn(true);

		underTest.doParse(configElement, parserContext, builder);

		// verify(registry).registerBeanDefinition(eq(MAPPING_CONTEXT_BEAN_NAME),
		// isA(GenericBeanDefinition.class));
		verify(builder).addConstructorArgValue(any());
	}
}

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
package org.socialsignin.spring.data.dynamodb.mapping.event;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static uk.org.lidalia.slf4jtest.LoggingEvent.trace;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.socialsignin.spring.data.dynamodb.domain.sample.User;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;

import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

@ExtendWith(MockitoExtension.class)
class LoggingEventListenerTest {

	@Autowired
	private final TestLogger logger = TestLoggerFactory.getTestLogger(LoggingEventListener.class);
	
	private final User sampleEntity = new User();
	@Mock
	private PaginatedQueryList<User> sampleQueryList;
	@Mock
	private PaginatedScanList<User> sampleScanList;

	private LoggingEventListener underTest;

	@BeforeEach
	void setUp() {
		underTest = new LoggingEventListener();

		logger.setEnabledLevelsForAllThreads(Level.TRACE);

		List<User> queryList = new ArrayList<>();
		queryList.add(sampleEntity);
		lenient().when(sampleQueryList.stream()).thenReturn(queryList.stream());
		lenient().when(sampleScanList.stream()).thenReturn(queryList.stream());
	}

	@AfterEach
	void clearLoggers() {
		TestLoggerFactory.clear();
	}

	@Test
	void testAfterDelete() {
		underTest.onApplicationEvent(new AfterDeleteEvent<>(sampleEntity));

		assertThat(logger.getLoggingEvents()).isEqualTo(asList(trace("onAfterDelete: {}", sampleEntity)));
	}

	@Test
	void testAfterLoad() {
		underTest.onApplicationEvent(new AfterLoadEvent<>(sampleEntity));

		assertThat(logger.getLoggingEvents()).isEqualTo(asList(trace("onAfterLoad: {}", sampleEntity)));
	}

	@Test
	void testAfterQuery() {
		underTest.onApplicationEvent(new AfterQueryEvent<>(sampleQueryList));

		assertThat(logger.getLoggingEvents()).isEqualTo(asList(trace("onAfterQuery: {}", sampleEntity)));
	}

	@Test
	void testAfterSave() {
		underTest.onApplicationEvent(new AfterSaveEvent<>(sampleEntity));

		assertThat(logger.getLoggingEvents()).isEqualTo(asList(trace("onAfterSave: {}", sampleEntity)));
	}

	@Test
	void testAfterScan() {
		underTest.onApplicationEvent(new AfterScanEvent<>(sampleScanList));

		assertThat(logger.getLoggingEvents()).isEqualTo(asList(trace("onAfterScan: {}", sampleEntity)));
	}

	@Test
	void testBeforeDelete() {
		underTest.onApplicationEvent(new BeforeDeleteEvent<>(sampleEntity));

		assertThat(logger.getLoggingEvents()).isEqualTo(asList(trace("onBeforeDelete: {}", sampleEntity)));
	}

	@Test
	void testBeforeSave() {
		underTest.onApplicationEvent(new BeforeSaveEvent<>(sampleEntity));

		assertThat(logger.getLoggingEvents()).isEqualTo(asList(trace("onBeforeSave: {}", sampleEntity)));
	}

}

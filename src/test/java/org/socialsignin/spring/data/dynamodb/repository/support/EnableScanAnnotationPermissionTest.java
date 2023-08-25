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
package org.socialsignin.spring.data.dynamodb.repository.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.socialsignin.spring.data.dynamodb.domain.sample.User;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.EnableScanCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class EnableScanAnnotationPermissionTest {

	@EnableScan
	public interface SampleRepository {
		List<User> findAll();
	}

	public interface SampleNoScanRepository {
		List<User> findAll();
	}

	@EnableScanCount
	public interface SampleMethodRepository {

		@EnableScan
		int someMethodThatsIgnored();

		@EnableScan
		int count();

		@EnableScan
		void deleteAll();

		@EnableScan
		List<User> findAll();
		@EnableScan
		Page<User> findAll(Pageable pageable);

	}

	@BeforeEach
	public void setUp() {

	}

	@Test
	void testSampleRepository() {
		EnableScanAnnotationPermissions underTest = new EnableScanAnnotationPermissions(SampleRepository.class);

		assertTrue(underTest.isCountUnpaginatedScanEnabled());
		assertTrue(underTest.isDeleteAllUnpaginatedScanEnabled());
		assertTrue(underTest.isFindAllPaginatedScanEnabled());
		assertFalse(underTest.isFindAllUnpaginatedScanCountEnabled());
		assertTrue(underTest.isFindAllUnpaginatedScanEnabled());
	}

	@Test
	void testSampleNoScanRepository() {
		EnableScanAnnotationPermissions underTest = new EnableScanAnnotationPermissions(SampleMethodRepository.class);

		assertTrue(underTest.isCountUnpaginatedScanEnabled());
		assertTrue(underTest.isDeleteAllUnpaginatedScanEnabled());
		assertTrue(underTest.isFindAllPaginatedScanEnabled());
		assertTrue(underTest.isFindAllUnpaginatedScanCountEnabled());
		assertTrue(underTest.isFindAllUnpaginatedScanEnabled());
	}

	@Test
	void testSampleMethodRepository() {
		EnableScanAnnotationPermissions underTest = new EnableScanAnnotationPermissions(SampleNoScanRepository.class);

		assertFalse(underTest.isCountUnpaginatedScanEnabled());
		assertFalse(underTest.isDeleteAllUnpaginatedScanEnabled());
		assertFalse(underTest.isFindAllPaginatedScanEnabled());
		assertFalse(underTest.isFindAllUnpaginatedScanCountEnabled());
		assertFalse(underTest.isFindAllUnpaginatedScanEnabled());
	}

}

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
package org.socialsignin.spring.data.dynamodb.utils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class SortHandlerTest {

	private SortHandler underTest = new SortHandler() {
	};

	@Test
	void testThrowUnsupportedSortException() {
		assertThatThrownBy(() -> underTest.throwUnsupportedSortOperationException()).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void testEnsureNoSortUnsorted() {
		underTest.ensureNoSort(Sort.unsorted());
	}

	@Test
	void testEnsureNoSortSorted() {
		assertThatThrownBy(() -> underTest.ensureNoSort(Sort.by("property"))).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void testEnsureNoSortUnpaged() {
		underTest.ensureNoSort(Pageable.unpaged());
	}

	@Test
	void TestEnsureNoSortPagedUnsorted() {
		underTest.ensureNoSort(PageRequest.of(0, 1, Sort.unsorted()));
	}

	@Test
	void TestEnsureNoSortPagedSorted() {
		assertThatThrownBy(() -> underTest.ensureNoSort(PageRequest.of(0, 1, Sort.by("property")))).isInstanceOf(UnsupportedOperationException.class);
	}
	
}

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
package org.socialsignin.spring.data.dynamodb.domain.sample;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.ExpressionAttribute;
import org.socialsignin.spring.data.dynamodb.repository.Query;
import org.socialsignin.spring.data.dynamodb.repository.QueryConstants;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;

public interface UserRepository extends CrudRepository<User, String> {

	@EnableScan
	@Override
	List<User> findAll();

	// CRUD method using Optional
	@Query(consistentReads = QueryConstants.ConsistentReadMode.CONSISTENT)
	Optional<User> findById(String id);

	@EnableScan
	List<User> findByLeaveDate(Instant leaveDate);

	@EnableScan
	Optional<User> findByName(String name);

    @EnableScan
    List<User> findAllByTagsContains(String tag);

    @EnableScan
    List<User> findAllByTagsContaining(String tag);

    @EnableScan
    List<User> findAllByTagsIsContaining(String tag);

    @EnableScan
    List<User> findAllByTagsNotContains(String tag);

    @EnableScan
    List<User> findAllByTagsNotContaining(String tag);

    @EnableScan
    List<User> findAllByTagsIsNotContaining(String tag);

    @EnableScan
    List<User> findAllByTagsContains(Set<String> tags);

    @EnableScan
    List<User> findAllByTagsContaining(Set<String> tags);

    @EnableScan
    List<User> findAllByTagsIsContaining(Set<String> tags);

    @EnableScan
    List<User> findAllByTagsNotContains(Set<String> tags);

    @EnableScan
    List<User> findAllByTagsNotContaining(Set<String> tags);

    @EnableScan
    List<User> findAllByTagsIsNotContaining(Set<String> tags);

    @EnableScan
    List<User> findAllByTagsContains(List<String> tags);

    @EnableScan
    List<User> findAllByTagsContaining(List<String> tags);

    @EnableScan
    List<User> findAllByTagsIsContaining(List<String> tags);

    @EnableScan
    List<User> findAllByTagsNotContains(List<String> tags);

    @EnableScan
    List<User> findAllByTagsNotContaining(List<String> tags);

    @EnableScan
    List<User> findAllByTagsIsNotContaining(List<String> tags);

    @EnableScan
	Future<User> findByNameAndPostCode(String name, String postCode);
	@EnableScan
	User findFirstByPostCode(String postCode);

	<T extends User> T save(T entity);

	@EnableScan
	List<User> findByNameIn(List<String> names);

	@EnableScan
	void deleteByIdAndName(String id, String name);

	@Query(fields = "leaveDate", limit = 1, filterExpression = "contains(#field, :value)",
			expressionMappingNames = {@ExpressionAttribute(key = "#field", value = "name")},
			expressionMappingValues = {@ExpressionAttribute(key=":value", parameterName = "projection")})
	List<User> findByPostCode(@Param("postCode") String postCode, @Param("projection") String projection);

	@Query(fields = "leaveDate", limit = 1, filterExpression = "contains(#field, :value)",
			expressionMappingNames = {@ExpressionAttribute(key = "#field", value = "name")},
			expressionMappingValues = {@ExpressionAttribute(key=":value", value = "projection")})
	List<User> findByPostCode(String postCode);

	@EnableScan
	User findByNameAndLeaveDate(String name, Instant leaveDate);

	void deleteByPostCodeAndNumberOfPlaylists(String postCode, Integer numberOfPlaylists);

	@EnableScan
	void deleteAll();
}

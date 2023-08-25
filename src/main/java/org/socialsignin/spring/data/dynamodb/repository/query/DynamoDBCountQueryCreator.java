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
package org.socialsignin.spring.data.dynamodb.repository.query;

import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;
import org.socialsignin.spring.data.dynamodb.query.Query;
import org.socialsignin.spring.data.dynamodb.query.StaticQuery;
import org.socialsignin.spring.data.dynamodb.repository.ExpressionAttribute;
import org.socialsignin.spring.data.dynamodb.repository.QueryConstants;
import org.socialsignin.spring.data.dynamodb.repository.support.DynamoDBEntityInformation;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.lang.Nullable;

import java.util.Optional;

public class DynamoDBCountQueryCreator<T, ID> extends AbstractDynamoDBQueryCreator<T, ID, Long> {

	private boolean pageQuery;

	public DynamoDBCountQueryCreator(PartTree tree, ParameterAccessor parameterAccessor,
									 DynamoDBEntityInformation<T, ID> entityMetadata, Optional<String> filterExpression,
									 ExpressionAttribute[] names, ExpressionAttribute[] values, DynamoDBOperations dynamoDBOperations,
									 boolean pageQuery) {

		super(tree, parameterAccessor, entityMetadata, Optional.empty(), Optional.empty(),
				QueryConstants.ConsistentReadMode.DEFAULT,  filterExpression, names, values, dynamoDBOperations);
		this.pageQuery = pageQuery;

	}

	@Override
	protected Query<Long> complete(@Nullable DynamoDBQueryCriteria<T, ID> criteria, Sort sort) {
		if (criteria == null) {
			return new StaticQuery<>(1L);
		} else {
			criteria.withFilterExpression(filterExpression);
			criteria.withExpressionAttributeNames(expressionAttributeNames);
			criteria.withExpressionAttributeValues(expressionAttributeValues);
			criteria.withMappedExpressionValues(mappedExpressionValues);
			return criteria.buildCountQuery(dynamoDBOperations, pageQuery);
		}
	}

}

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
package org.socialsignin.spring.data.dynamodb.query;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;

/**
 * @author Michael Lavelle
 * @author Sebastian Just
 */
public class QueryExpressionCountQuery<T> extends AbstractSingleEntityQuery<Long> {

	private final DynamoDBQueryExpression<T> queryExpression;
	private final Class<T> domainClass;

	public QueryExpressionCountQuery(DynamoDBOperations dynamoDBOperations, Class<T> clazz,
			DynamoDBQueryExpression<T> queryExpression) {
		super(dynamoDBOperations, Long.class);
		this.queryExpression = queryExpression;
		this.domainClass = clazz;
	}

	@Override
	public Long getSingleResult() {
		return Long.valueOf(dynamoDBOperations.count(domainClass, queryExpression));
	}

}

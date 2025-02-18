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

import java.util.List;

/**
 * @author Michael Lavelle
 * @author Sebastian Just
 */
public class MultipleEntityQueryExpressionQuery<T> extends AbstractMultipleEntityQuery<T> {

	private DynamoDBQueryExpression<T> queryExpression;

	public MultipleEntityQueryExpressionQuery(DynamoDBOperations dynamoDBOperations, Class<T> clazz,
			DynamoDBQueryExpression<T> queryExpression) {
		super(dynamoDBOperations, clazz);
		this.queryExpression = queryExpression;
	}

	@Override
	public List<T> getResultList() {
		return dynamoDBOperations.query(clazz, queryExpression);
	}

}

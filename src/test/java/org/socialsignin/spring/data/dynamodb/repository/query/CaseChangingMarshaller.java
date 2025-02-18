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

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;

@SuppressWarnings("deprecation")
public class CaseChangingMarshaller implements DynamoDBMarshaller<String> {

	@Override
	public String marshall(String getterReturnResult) {
		return getterReturnResult == null ? null : getterReturnResult.toLowerCase();
	}

	@Override
	public String unmarshall(Class<String> clazz, String obj) {
		return obj == null ? null : obj.toUpperCase();
	}

}

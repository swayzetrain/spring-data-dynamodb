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

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import java.time.LocalDateTime;

@DynamoDBTable(tableName = "gz_feed")
public class Feed {
	private String idx;
	private int userIdx;
	private String message;
	private int paymentType;
	private LocalDateTime regDate;

	@DynamoDBHashKey
	@DynamoDBAutoGeneratedKey
	public String getIdx() {
		return idx;
	}

	@DynamoDBAttribute
	public int getUserIdx() {
		return userIdx;
	}

	@DynamoDBAttribute
	@DynamoDBIndexHashKey(globalSecondaryIndexName = "aaa")
	public String getMessage() {
		return message;
	}

	// @DynamoDBIndexRangeKey(attributeName = "PaymentType",
	// globalSecondaryIndexName = "aaa")
	@DynamoDBAttribute
	public int getPaymentType() {
		return paymentType;
	}

	@DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
	@DynamoDBAttribute
	@DynamoDBIndexRangeKey(globalSecondaryIndexName = "aaa")
	public LocalDateTime getRegDate() {
		return regDate;
	}

	public void setIdx(String idx) {
		this.idx = idx;
	}

	public void setUserIdx(int userIdx) {
		this.userIdx = userIdx;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setPaymentType(int paymentType) {
		this.paymentType = paymentType;
	}

	public void setRegDate(LocalDateTime regDate) {
		this.regDate = regDate;
	}

	static public class LocalDateTimeConverter implements DynamoDBTypeConverter<String, LocalDateTime> {

		@Override
		public String convert(final LocalDateTime time) {

			return time.toString();
		}

		@Override
		public LocalDateTime unconvert(final String stringValue) {

			return LocalDateTime.parse(stringValue);
		}
	}
}
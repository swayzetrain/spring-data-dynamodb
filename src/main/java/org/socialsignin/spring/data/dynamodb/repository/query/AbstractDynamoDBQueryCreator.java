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

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperTableModel;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;
import org.socialsignin.spring.data.dynamodb.query.Query;
import org.socialsignin.spring.data.dynamodb.repository.ExpressionAttribute;
import org.socialsignin.spring.data.dynamodb.repository.QueryConstants;
import org.socialsignin.spring.data.dynamodb.repository.support.DynamoDBEntityInformation;
import org.socialsignin.spring.data.dynamodb.repository.support.DynamoDBIdIsHashAndRangeKeyEntityInformation;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.Part.IgnoreCaseType;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Michael Lavelle
 * @author Sebastian Just
 */
public abstract class AbstractDynamoDBQueryCreator<T, ID, R>
		extends
			AbstractQueryCreator<Query<R>, DynamoDBQueryCriteria<T, ID>> {

	protected final DynamoDBEntityInformation<T, ID> entityMetadata;
	protected final DynamoDBOperations dynamoDBOperations;
	protected final Optional<String> projection;
	protected final Optional<Integer> limit;
	protected final Optional<String> filterExpression;
	protected final ExpressionAttribute[] expressionAttributeNames;
	protected final ExpressionAttribute[] expressionAttributeValues;
	protected final Map<String, String> mappedExpressionValues = new HashMap<>();
	protected final QueryConstants.ConsistentReadMode consistentReads;

	public AbstractDynamoDBQueryCreator(PartTree tree, DynamoDBEntityInformation<T, ID> entityMetadata,
										Optional<String> projection, Optional<Integer> limitResults, QueryConstants.ConsistentReadMode consistentReads,
										Optional<String> filterExpression, ExpressionAttribute[] names, ExpressionAttribute[] values, DynamoDBOperations dynamoDBOperations) {
		super(tree);
		this.entityMetadata = entityMetadata;
		this.projection = projection;
		this.limit = limitResults;
		this.consistentReads = consistentReads;
		this.filterExpression = filterExpression;
		if(names != null) {
			this.expressionAttributeNames = names.clone();
		} else{
			this.expressionAttributeNames = null;
		}
		if(values != null) {
			this.expressionAttributeValues = values.clone();
		} else{
			this.expressionAttributeValues = null;
		}
		this.dynamoDBOperations = dynamoDBOperations;
	}

	public AbstractDynamoDBQueryCreator(PartTree tree, ParameterAccessor parameterAccessor,
										DynamoDBEntityInformation<T, ID> entityMetadata, Optional<String> projection,
										Optional<Integer> limitResults, QueryConstants.ConsistentReadMode consistentReads, Optional<String> filterExpression, ExpressionAttribute[] names, ExpressionAttribute[] values, DynamoDBOperations dynamoDBOperations) {
		super(tree, parameterAccessor);
		this.entityMetadata = entityMetadata;
		this.projection = projection;
		this.limit = limitResults;
		this.filterExpression = filterExpression;
		this.consistentReads = consistentReads;
		if(names != null) {
			this.expressionAttributeNames = names.clone();
		} else{
			this.expressionAttributeNames = null;
		}
		if(values != null) {
			this.expressionAttributeValues = values.clone();
			for(ExpressionAttribute value: expressionAttributeValues) {
				if(StringUtils.hasLength(value.parameterName())) {
					for(Parameter p : ((ParametersParameterAccessor)parameterAccessor).getParameters()) {
						if(p.getName().isPresent() && p.getName().get().equals(value.parameterName())) {
							mappedExpressionValues.put(value.parameterName(), (String) parameterAccessor.getBindableValue(p.getIndex()));
						}
					}
				}
			}
		} else {
			this.expressionAttributeValues = null;
		}
		this.dynamoDBOperations = dynamoDBOperations;
	}

	@Override
	protected DynamoDBQueryCriteria<T, ID> create(Part part, Iterator<Object> iterator) {
		final DynamoDBMapperTableModel<T> tableModel = dynamoDBOperations.getTableModel(entityMetadata.getJavaType());
		DynamoDBQueryCriteria<T, ID> criteria = entityMetadata.isRangeKeyAware()
				? new DynamoDBEntityWithHashAndRangeKeyCriteria<>(
				(DynamoDBIdIsHashAndRangeKeyEntityInformation<T, ID>) entityMetadata, tableModel)
				: new DynamoDBEntityWithHashKeyOnlyCriteria<>(entityMetadata, tableModel);
		return addCriteria(criteria, part, iterator);
	}

	protected DynamoDBQueryCriteria<T, ID> addCriteria(DynamoDBQueryCriteria<T, ID> criteria, Part part,
			Iterator<Object> iterator) {
		if (part.shouldIgnoreCase().equals(IgnoreCaseType.ALWAYS))
			throw new UnsupportedOperationException("Case insensitivity not supported");

		Class<?> leafNodePropertyType = part.getProperty().getLeafProperty().getType();

		PropertyPath leafNodePropertyPath = part.getProperty().getLeafProperty();
		String leafNodePropertyName = leafNodePropertyPath.toDotPath();
		if (leafNodePropertyName.contains(".")) {
			int index = leafNodePropertyName.lastIndexOf(".");
			leafNodePropertyName = leafNodePropertyName.substring(index);
		}

		switch (part.getType()) {
			case IN :
                return getInProperty(criteria, iterator, leafNodePropertyType, leafNodePropertyName);
            case CONTAINING :
                return getItemsProperty(criteria, ComparisonOperator.CONTAINS, iterator, leafNodePropertyType, leafNodePropertyName);
            case NOT_CONTAINING:
                return getItemsProperty(criteria, ComparisonOperator.NOT_CONTAINS, iterator, leafNodePropertyType, leafNodePropertyName);
            case STARTING_WITH :
				return criteria.withSingleValueCriteria(leafNodePropertyName, ComparisonOperator.BEGINS_WITH,
						iterator.next(), leafNodePropertyType);
			case BETWEEN :
				Object first = iterator.next();
				Object second = iterator.next();
				return criteria.withPropertyBetween(leafNodePropertyName, first, second, leafNodePropertyType);
			case AFTER :
			case GREATER_THAN :
				return criteria.withSingleValueCriteria(leafNodePropertyName, ComparisonOperator.GT, iterator.next(),
						leafNodePropertyType);
			case BEFORE :
			case LESS_THAN :
				return criteria.withSingleValueCriteria(leafNodePropertyName, ComparisonOperator.LT, iterator.next(),
						leafNodePropertyType);
			case GREATER_THAN_EQUAL :
				return criteria.withSingleValueCriteria(leafNodePropertyName, ComparisonOperator.GE, iterator.next(),
						leafNodePropertyType);
			case LESS_THAN_EQUAL :
				return criteria.withSingleValueCriteria(leafNodePropertyName, ComparisonOperator.LE, iterator.next(),
						leafNodePropertyType);
			case IS_NULL :
				return criteria.withNoValuedCriteria(leafNodePropertyName, ComparisonOperator.NULL);
			case IS_NOT_NULL :
				return criteria.withNoValuedCriteria(leafNodePropertyName, ComparisonOperator.NOT_NULL);
			case TRUE :
				return criteria.withSingleValueCriteria(leafNodePropertyName, ComparisonOperator.EQ, Boolean.TRUE,
						leafNodePropertyType);
			case FALSE :
				return criteria.withSingleValueCriteria(leafNodePropertyName, ComparisonOperator.EQ, Boolean.FALSE,
						leafNodePropertyType);
			case SIMPLE_PROPERTY :
				return criteria.withPropertyEquals(leafNodePropertyName, iterator.next(), leafNodePropertyType);
			case NEGATING_SIMPLE_PROPERTY :
				return criteria.withSingleValueCriteria(leafNodePropertyName, ComparisonOperator.NE, iterator.next(),
						leafNodePropertyType);
			default :
				throw new IllegalArgumentException("Unsupported keyword " + part.getType());
		}

	}

    private DynamoDBQueryCriteria<T, ID> getItemsProperty(DynamoDBQueryCriteria<T, ID> criteria, ComparisonOperator comparisonOperator, Iterator<Object> iterator, Class<?> leafNodePropertyType, String leafNodePropertyName) {
        Object in = iterator.next();
        Assert.notNull(in, "Creating conditions on null parameters not supported: please specify a value for '" + leafNodePropertyName + "'");

        if(ObjectUtils.isArray(in)) {
            List<?> list = Arrays.asList(ObjectUtils.toObjectArray(in));
            Assert.isTrue(list.size()==1, "Only one value is supported: please specify a value for '\" + leafNodePropertyName + \"'\"");
            Object value = list.get(0);
            return criteria.withSingleValueCriteria(leafNodePropertyName, comparisonOperator, value, leafNodePropertyType);
        } else if(ClassUtils.isAssignable(Iterable.class, in.getClass())) {
            Iterator<?> iter = ((Iterable<?>) in).iterator();
            Assert.isTrue(iter.hasNext(), "Creating conditions on empty parameters not supported: please specify a value for '\" + leafNodePropertyName + \"'\"");
            Object value = iter.next();
            Assert.isTrue(!iter.hasNext(), "Only one value is supported: please specify a value for '\" + leafNodePropertyName + \"'\"");
            return criteria.withSingleValueCriteria(leafNodePropertyName, comparisonOperator, value, leafNodePropertyType);
        } else {
            return criteria.withSingleValueCriteria(leafNodePropertyName, comparisonOperator, in, leafNodePropertyType);
        }
    }

    private DynamoDBQueryCriteria<T, ID> getInProperty(DynamoDBQueryCriteria<T, ID> criteria, Iterator<Object> iterator, Class<?> leafNodePropertyType, String leafNodePropertyName) {
        Object in = iterator.next();
        Assert.notNull(in, "Creating conditions on null parameters not supported: please specify a value for '"
                + leafNodePropertyName + "'");
        boolean isIterable = ClassUtils.isAssignable(Iterable.class, in.getClass());
        boolean isArray = ObjectUtils.isArray(in);
        Assert.isTrue(isIterable || isArray, "In criteria can only operate with Iterable or Array parameters");
        Iterable<?> iterable = isIterable ? ((Iterable<?>) in) : Arrays.asList(ObjectUtils.toObjectArray(in));
        return criteria.withPropertyIn(leafNodePropertyName, iterable, leafNodePropertyType);
    }

    @Override
	protected DynamoDBQueryCriteria<T, ID> and(Part part, DynamoDBQueryCriteria<T, ID> base,
			Iterator<Object> iterator) {
		return addCriteria(base, part, iterator);

	}

	@Override
	protected DynamoDBQueryCriteria<T, ID> or(DynamoDBQueryCriteria<T, ID> base,
			DynamoDBQueryCriteria<T, ID> criteria) {
		throw new UnsupportedOperationException("Or queries not supported");
	}

}

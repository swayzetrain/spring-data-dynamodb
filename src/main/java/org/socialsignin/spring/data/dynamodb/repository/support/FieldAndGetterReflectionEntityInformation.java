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

import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * {@link org.springframework.data.repository.core.EntityInformation}
 * implementation that inspects getters for an annotation and invokes this
 * getter's value to retrieve the id.
 *
 * @author Michael Lavelle
 * @author Sebastian Just
 */
public class FieldAndGetterReflectionEntityInformation<T, ID> extends AbstractEntityInformation<T, ID> {

	protected Method method;
	private Field field;

	/**
	 * Creates a new {@link FieldAndGetterReflectionEntityInformation} inspecting
	 * the given domain class for a getter carrying the given annotation.
	 *
	 * @param domainClass
	 *            must not be {@literal null}.
	 * @param annotation
	 *            must not be {@literal null}.
	 */
	public FieldAndGetterReflectionEntityInformation(@NonNull Class<T> domainClass,
			@NonNull final Class<? extends Annotation> annotation) {

		super(domainClass);
		Assert.notNull(annotation, "annotation must not be null!");

		ReflectionUtils.doWithMethods(domainClass, (method) -> {
			if (method.getAnnotation(annotation) != null) {
				this.method = method;
				return;
			}
		});

		if (method == null) {
			field = null;
			ReflectionUtils.doWithFields(domainClass, (field) -> {
				if (field.getAnnotation(annotation) != null) {
					this.field = field;
					return;
				}
			});
		}

		Assert.isTrue(this.method != null || this.field != null,
				String.format("No field or method annotated with %s found!", annotation.toString()));
		Assert.isTrue(this.method == null || this.field == null,
				String.format("Both field and method annotated with %s found!", annotation.toString()));

		if (method != null) {
			ReflectionUtils.makeAccessible(method);
		}
		if (field != null) {
			ReflectionUtils.makeAccessible(field);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.data.repository.core.EntityInformation#getId(java
	 * .lang.Object)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ID getId(T entity) {

		if (method != null) {
			return entity == null ? null : (ID) ReflectionUtils.invokeMethod(method, entity);
		} else {
			return entity == null ? null : (ID) ReflectionUtils.getField(field, entity);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.data.repository.core.EntityInformation#getIdType()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Class<ID> getIdType() {
		return (Class<ID>) (method != null ? method.getReturnType() : field.getType());
	}
}

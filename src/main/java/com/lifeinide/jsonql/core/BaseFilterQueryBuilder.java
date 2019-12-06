package com.lifeinide.jsonql.core;

import com.lifeinide.jsonql.core.dto.Page;
import com.lifeinide.jsonql.core.intr.FilterQueryBuilder;
import com.lifeinide.jsonql.core.intr.QueryFilter;

import java.util.List;

/**
 * A base for all {@link FilterQueryBuilder} implementations.
 *
 * @author Lukasz Frankowski
 */
public abstract class BaseFilterQueryBuilder<E, P extends Page<E>, Q, C extends BaseQueryBuilderContext,
	SELF extends BaseFilterQueryBuilder<E, P, Q, C, SELF>>
implements FilterQueryBuilder<E, P, Q, SELF> {

	public abstract C context();

	protected <T> Page<T> buildPageableResult(Integer pageSize, Integer page, long count, List<T> data) {
		return new Page<>(pageSize, page, count, data);
	}

	@Override
	public SELF add(String field, QueryFilter filter) {
		throw new IllegalStateException(String.format("Support for filter: %s in builder: %s is not implemented",
			filter.getClass().getSimpleName(), getClass().getSimpleName()));
	}

	protected String createAlias(Class clazz) {
		return clazz.getSimpleName().replaceAll("[a-z]", "").toLowerCase();
	}

}

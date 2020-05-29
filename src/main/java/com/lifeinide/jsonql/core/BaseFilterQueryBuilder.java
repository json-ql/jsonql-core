package com.lifeinide.jsonql.core;

import com.lifeinide.jsonql.core.dto.Page;
import com.lifeinide.jsonql.core.intr.FilterQueryBuilder;
import com.lifeinide.jsonql.core.intr.QueryFilter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * A base for all {@link FilterQueryBuilder} implementations.
 *
 * @author Lukasz Frankowski
 */
@SuppressWarnings("unchecked")
public abstract class BaseFilterQueryBuilder<E, P extends Page<E>, Q, C extends BaseQueryBuilderContext,
	SELF extends BaseFilterQueryBuilder<E, P, Q, C, SELF>>
implements FilterQueryBuilder<E, P, Q, SELF> {

	/**
	 * Limits returned items count in case of no pagination is requested. Null means infinity.
	 */
	protected Integer maxResults = 100;

	@Nonnull public abstract C context();

	@Nonnull protected <T> Page<T> buildPageableResult(Integer pageSize, Integer page, long count, List<T> data) {
		return new Page<>(pageSize, page, count, data);
	}

	@Nonnull
	@Override
	public SELF add(@Nonnull String field, QueryFilter filter) {
		throw new IllegalStateException(String.format("Support for filter: %s in builder: %s is not implemented",
			filter.getClass().getSimpleName(), getClass().getSimpleName()));
	}

	protected String createAlias(Class<?> clazz) {
		return clazz.getSimpleName().replaceAll("[a-z]", "").toLowerCase();
	}

	@Nullable public Integer getMaxResults() {
		return maxResults;
	}

	public SELF withMaxResults(int maxResults) {
		this.maxResults = maxResults;
		return (SELF) this;
	}

	public SELF withUnlimitedResults() {
		this.maxResults = null;
		return (SELF) this;

	}

}

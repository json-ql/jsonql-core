package com.lifeinide.jsonql.core.intr;

import com.lifeinide.jsonql.core.filters.*;

/**
 * Visits all possible {@link QueryFilter -s} to build the query.
 *
 * @param <E> represents entity type
 * @param <P> represents type of pageable result returned from {@link #list(Pageable, Sortable)}
 * @param <Q> represents the type of query build from all criterial
 *
 * @author Lukasz Frankowski
 */
public interface FilterQueryBuilder<E, P extends PageableResult<E>, Q, SELF extends FilterQueryBuilder<E, P, Q, SELF>> {

	SELF add(String field, DateRangeQueryFilter filter);
	SELF add(String field, EntityQueryFilter<?> filter);
	SELF add(String field, ListQueryFilter<? extends QueryFilter> filter);
	SELF add(String field, SingleValueQueryFilter<?> filter);
	SELF add(String field, ValueRangeQueryFilter<? extends Number> filter);

	/**
	 * The bridge for custom filters in concrete database implementations.
	 */
	SELF add(String field, QueryFilter filter);

	/**
	 * Allows to construct "or" clauses. Eg: {@code filter.or(() -> filter.add(CONDITION_1).add(CONDITION_2))}.
	 */
	default SELF or(Runnable r) {
		throw new IllegalStateException("Not implemented");
	}

	/**
	 * Allows to construct "and" clauses. Eg: {@code filter.and(() -> filter.add(CONDITION_1).add(CONDITION_2))}.
	 */
	default SELF and(Runnable r) {
		throw new IllegalStateException("Not implemented");
	}

	/**
	 * Builds the final query from criterias
	 */
	Q build(Pageable pageable, Sortable<?> sortable);

	P list(Pageable pageable, Sortable<?> sortable);

	default P list() {
		return list(null, null);
	}
	
	default P list(Pageable pageable) {
		return list(pageable, null);
	}

	default P list(Sortable<?> sortable) {
		return list(null, sortable);
	}

	default P list(PageableSortable<?> ps) {
		return list(ps, ps);
	}

}

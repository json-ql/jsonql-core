package com.lifeinide.jsonql.core.intr;

import com.lifeinide.jsonql.core.filters.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

	@Nonnull SELF add(@Nonnull String field, @Nullable DateRangeQueryFilter filter);
	@Nonnull SELF add(@Nonnull String field, @Nullable EntityQueryFilter<?> filter);
	@Nonnull SELF add(@Nonnull String field, @Nullable ListQueryFilter<? extends QueryFilter> filter);
	@Nonnull SELF add(@Nonnull String field, @Nullable SingleValueQueryFilter<?> filter);
	@Nonnull SELF add(@Nonnull String field, @Nullable ValueRangeQueryFilter<? extends Number> filter);

	/**
	 * The bridge for custom filters in concrete database implementations.
	 */
	@Nonnull SELF add(@Nonnull String field, @Nullable QueryFilter filter);

	/**
	 * Allows to construct "or" clauses. Eg: {@code filter.or(() -> filter.add(CONDITION_1).add(CONDITION_2))}.
	 */
	@Nonnull default SELF or(@Nonnull Runnable r) {
		throw new IllegalStateException("Not implemented");
	}

	/**
	 * Allows to construct "and" clauses. Eg: {@code filter.and(() -> filter.add(CONDITION_1).add(CONDITION_2))}.
	 */
	@Nonnull default SELF and(@Nonnull Runnable r) {
		throw new IllegalStateException("Not implemented");
	}

	/**
	 * Builds the final query from criterias
	 */
	@Nonnull Q build(@Nonnull Pageable pageable, @Nonnull Sortable<?> sortable);

	@Nonnull P list(@Nullable Pageable pageable, @Nullable Sortable<?> sortable);

	@Nonnull default P list() {
		return list(null, null);
	}

	@Nonnull default P list(@Nullable Pageable pageable) {
		return list(pageable, null);
	}

	@Nonnull default P list(@Nullable Sortable<?> sortable) {
		return list(null, sortable);
	}

	@Nonnull default P list(@Nullable PageableSortable<?> ps) {
		return list(ps, ps);
	}

}

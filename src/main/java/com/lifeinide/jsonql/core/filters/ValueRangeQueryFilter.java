package com.lifeinide.jsonql.core.filters;

import com.lifeinide.jsonql.core.intr.FilterQueryBuilder;
import com.lifeinide.jsonql.core.intr.QueryFilter;

/**
 * Filter for number range.
 * 
 * @author Lukasz Frankowski
 */
public class ValueRangeQueryFilter<N extends Number> implements QueryFilter {

	protected N from;

	protected N to;

	public ValueRangeQueryFilter() {
	}

	public ValueRangeQueryFilter<N> with(N from, N to) {
		setFrom(from);
		setTo(to);
		return this;
	}

	public ValueRangeQueryFilter<N> from(N from) {
		setFrom(from);
		return this;
	}

	public ValueRangeQueryFilter<N> to(N to) {
		setTo(to);
		return this;
	}

	public N getFrom() {
		return from;
	}

	public void setFrom(N from) {
		this.from = from;
	}

	public N getTo() {
		return to;
	}

	public void setTo(N to) {
		this.to = to;
	}

	@Override
	public void accept(FilterQueryBuilder builder, String field) {
		builder.add(field, this);
	}

	public static <N extends Number> ValueRangeQueryFilter<N> of(N from, N to) {
		return new ValueRangeQueryFilter<N>().with(from, to);
	}

	public static <N extends Number> ValueRangeQueryFilter<N> ofFrom(N from) {
		return new ValueRangeQueryFilter<N>().from(from);
	}

	public static <N extends Number> ValueRangeQueryFilter<N> ofTo(N to) {
		return new ValueRangeQueryFilter<N>().to(to);
	}

}

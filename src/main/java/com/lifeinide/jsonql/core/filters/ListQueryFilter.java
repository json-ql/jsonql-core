package com.lifeinide.jsonql.core.filters;

import com.lifeinide.jsonql.core.enums.QueryConjunction;
import com.lifeinide.jsonql.core.intr.FilterQueryBuilder;
import com.lifeinide.jsonql.core.intr.QueryFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Combines multiple {@link QueryFilter}-s with and/or conjunction.
 *
 * @author Lukasz Frankowski
 */
public class ListQueryFilter<F extends QueryFilter> implements QueryFilter {

	protected List<F> filters = new ArrayList<>();

	protected QueryConjunction conjunction = QueryConjunction.or;

	public ListQueryFilter() {
	}

	public ListQueryFilter(QueryConjunction conjunction) {
		this.conjunction = conjunction;
	}

	public List<F> getFilters() {
		return filters;
	}

	public void setFilters(List<F> filters) {
		this.filters = filters;
	}

	public ListQueryFilter<F> addFilter(F queryFilter) {
		getFilters().add(queryFilter);
		return this;
	}

	public ListQueryFilter<F> and() {
		setConjunction(QueryConjunction.and);
		return this;
	}

	public QueryConjunction getConjunction() {
		return conjunction;
	}

	public void setConjunction(QueryConjunction conjunction) {
		this.conjunction = conjunction;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void accept(FilterQueryBuilder builder, String field) {
		builder.add(field, this);
	}

	@SafeVarargs
	public static <F extends QueryFilter> ListQueryFilter<F> of(F ... filters) {
		ListQueryFilter<F> filter = new ListQueryFilter<>();
		Arrays.stream(filters).forEach(filter::addFilter);
		return filter;
	}

}

package com.lifeinide.jsonql.core.filters;

import com.lifeinide.jsonql.core.enums.QueryCondition;
import com.lifeinide.jsonql.core.intr.FilterQueryBuilder;
import com.lifeinide.jsonql.core.intr.QueryFilter;

/**
 * Filter for any single value.
 *
 * @author Lukasz Frankowski
 */
public class SingleValueQueryFilter<T> implements QueryFilter {

	protected T value;

	protected QueryCondition condition = QueryCondition.eq;

	public SingleValueQueryFilter() {
	}

	public SingleValueQueryFilter<T> with(T value) {
		setValue(value);
		return this;
	}

	public SingleValueQueryFilter<T> eq() {
		setCondition(QueryCondition.eq);
		return this;
	}

	public SingleValueQueryFilter<T> ne() {
		setCondition(QueryCondition.ne);
		return this;
	}

	public SingleValueQueryFilter<T> gt() {
		setCondition(QueryCondition.gt);
		return this;
	}

	public SingleValueQueryFilter<T> ge() {
		setCondition(QueryCondition.ge);
		return this;
	}

	public SingleValueQueryFilter<T> lt() {
		setCondition(QueryCondition.lt);
		return this;
	}

	public SingleValueQueryFilter<T> le() {
		setCondition(QueryCondition.le);
		return this;
	}

	public SingleValueQueryFilter<T> isNull() {
		setCondition(QueryCondition.isNull);
		return this;
	}

	public SingleValueQueryFilter<T> notNull() {
		setCondition(QueryCondition.notNull);
		return this;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public QueryCondition getCondition() {
		return condition;
	}

	public void setCondition(QueryCondition condition) {
		this.condition = condition;
	}

	@Override
	public void accept(FilterQueryBuilder builder, String field) {
		builder.add(field, this);
	}

	public static <T> SingleValueQueryFilter<T> of(T value) {
		return new SingleValueQueryFilter<T>().with(value);
	}

	public static SingleValueQueryFilter ofNull() {
		return new SingleValueQueryFilter().isNull();
	}

	public static SingleValueQueryFilter ofNotNull() {
		return new SingleValueQueryFilter().notNull();
	}

}

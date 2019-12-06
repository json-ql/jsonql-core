package com.lifeinide.jsonql.core.filters;

import com.lifeinide.jsonql.core.intr.FilterQueryBuilder;

import java.io.Serializable;

/**
 * Filter for entities. Special kind of {@link SingleValueQueryFilter} carrying the entity ID.
 *
 * @author Lukasz Frankowski
 */
@SuppressWarnings("unchecked")
public class EntityQueryFilter<ID extends Serializable> extends SingleValueQueryFilter<ID> {

	public EntityQueryFilter() {
	}

	@Override
	public void accept(FilterQueryBuilder builder, String field) {
		builder.add(field, this);
	}

	public static <ID extends Serializable> EntityQueryFilter<ID> of(ID value) {
		return (EntityQueryFilter<ID>) new EntityQueryFilter<>().with(value);
	}

	public static EntityQueryFilter ofNull() {
		return (EntityQueryFilter) new EntityQueryFilter().isNull();
	}

	public static EntityQueryFilter ofNotNull() {
		return (EntityQueryFilter) new EntityQueryFilter().notNull();
	}

}

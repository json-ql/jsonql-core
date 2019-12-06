package com.lifeinide.jsonql.core.intr;

/**
 * Default interface for list filter. The filter gets the data from the client (usually in JSON) and then is converted to database query
 * using appropriate {@link FilterQueryBuilder}.
 *
 * @author Lukasz Frankowski
 */
public interface QueryFilter {

	default void accept(FilterQueryBuilder builder, String field) {
		builder.add(field, this);
	}

}

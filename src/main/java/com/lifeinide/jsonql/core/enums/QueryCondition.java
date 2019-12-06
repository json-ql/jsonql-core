package com.lifeinide.jsonql.core.enums;

import com.lifeinide.jsonql.core.filters.SingleValueQueryFilter;

/**
 * Condition for {@link SingleValueQueryFilter}.
 *
 * @author Lukasz Frankowski
 */
public enum QueryCondition {

	/** Equals **/
	eq,

	/** Not equals **/
	ne,

	/** Greater than **/
	gt,

	/** Greater or equal than **/
	ge,

	/** Lower than **/
	lt,

	/** Lower or equal than **/
	le,

	/** Is null **/
	isNull,

	/** Is not null **/
	notNull

}

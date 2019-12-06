package com.lifeinide.jsonql.core.intr;

import com.lifeinide.jsonql.core.enums.SortDirection;

/**
 * @author Lukasz Frankowski
 */
public interface SortField {

	String getSortField();
	SortDirection getSortDirection();

	default boolean isAsc() {
		return SortDirection.ASC.equals(getSortDirection());
	}

	default boolean isDesc() {
		return !isAsc();
	}

}

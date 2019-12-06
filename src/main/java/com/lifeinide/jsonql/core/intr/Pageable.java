package com.lifeinide.jsonql.core.intr;

/**
 * @author Lukasz Frankowski
 */
public interface Pageable {

	Integer getPageSize();
	Integer getPage();

	default Integer getOffset() {
		if (isPaged())
			return (getPage()-1) * getPageSize();
		return null;
	}

	default boolean isPaged() {
		return getPage()!=null && getPageSize()!=null;
	}

}

package com.lifeinide.jsonql.core.intr;

import java.util.Iterator;
import java.util.List;

/**
 * @author Lukasz Frankowski
 */
public interface PageableResult<T> extends Pageable, Iterable<T> {

	long getCount();
	Integer getPagesCount();
	List<T> getData();

	@Override
	default Iterator<T> iterator() {
		return getData().iterator();
	}

}

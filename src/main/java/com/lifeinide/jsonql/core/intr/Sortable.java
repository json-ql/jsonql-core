package com.lifeinide.jsonql.core.intr;

import java.util.List;

/**
 * @author Lukasz Frankowski
 */
public interface Sortable<S extends SortField> {

	List<S> getSort();

}

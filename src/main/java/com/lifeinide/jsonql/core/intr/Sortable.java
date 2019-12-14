package com.lifeinide.jsonql.core.intr;

import java.util.Set;

/**
 * @author Lukasz Frankowski
 */
public interface Sortable<S extends SortField> {

	Set<S> getSort();

}

package com.lifeinide.jsonql.core.intr;

/**
 * @author lukasz.frankowski@gmail.com
 */
public interface PageableSortable<S extends SortField> extends Pageable, Sortable<S> {
}

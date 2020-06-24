package com.lifeinide.jsonql.core.dto;

import com.lifeinide.jsonql.core.intr.PageableSortable;
import com.lifeinide.jsonql.core.intr.SortField;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Base common request for filtering.
 *
 * @author Lukasz Frankowski
 */
public class BasePageableRequest<S extends SortField> implements Serializable, PageableSortable<S> {

	protected Integer pageSize;

	protected Integer page = 1;

	protected Set<S> sort = new LinkedHashSet<>();

	@Override
	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public BasePageableRequest withPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	@Override
	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public BasePageableRequest withPage(Integer page) {
		this.page = page;
		return this;
	}

	@Override
	public Set<S> getSort() {
		return sort;
	}

	public void setSort(Set<S> sort) {
		this.sort = sort;
	}

	public BasePageableRequest withSort(S sort) {
		if (sort != null) {
			getSort().add(sort);
		}
		return this;
	}

	public static BasePageableRequest ofDefault() {
		return new BasePageableRequest();
	}

	public static BasePageableRequest ofUnpaged() {
		return ofDefault();
	}

}

package com.lifeinide.jsonql.core.dto;

import com.lifeinide.jsonql.core.enums.SortDirection;
import com.lifeinide.jsonql.core.intr.SortField;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Lukasz Frankowski
 */
public class Sort implements Serializable, SortField {

	protected String sortField;
	protected SortDirection sortDirection = SortDirection.ASC;

	@Override
	public String getSortField() {
		return sortField;
	}

	public void setField(String sortField) {
		this.sortField = sortField;
	}

	@Override
	public SortDirection getSortDirection() {
		return sortDirection;
	}

	public void setDirection(SortDirection sortDirection) {
		this.sortDirection = sortDirection;
	}

	public Sort with(String sortField) {
		setField(sortField);
		return this;
	}

	public Sort asc() {
		setDirection(SortDirection.ASC);
		return this;
	}

	public Sort desc() {
		setDirection(SortDirection.DESC);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Sort)) return false;
		Sort sort = (Sort) o;
		return Objects.equals(getSortField(), sort.getSortField()) &&
			getSortDirection() == sort.getSortDirection();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getSortField(), getSortDirection());
	}

	public static Sort of(String sortField) {
		return new Sort().with(sortField);
	}

	public static Sort ofAsc(String sortField) {
		return of(sortField);
	}

	public static Sort ofDesc(String sortField) {
		return of(sortField).desc();
	}

}

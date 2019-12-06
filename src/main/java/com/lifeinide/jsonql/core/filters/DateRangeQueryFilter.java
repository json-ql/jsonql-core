package com.lifeinide.jsonql.core.filters;

import com.lifeinide.jsonql.core.enums.DateRange;
import com.lifeinide.jsonql.core.intr.FilterQueryBuilder;
import com.lifeinide.jsonql.core.intr.QueryFilter;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.Temporal;

/**
 * Filter for dates range.
 *
 * @author Lukasz Frankowski
 */
public class DateRangeQueryFilter implements QueryFilter {

	protected DateRange range = DateRange.CUSTOM;

	protected LocalDate from;

	protected LocalDate to;

	public LocalDate getFrom() {
		return from;
	}

	public void setFrom(LocalDate from) {
		this.from = from;
	}

	public LocalDate getTo() {
		return to;
	}

	public void setTo(LocalDate to) {
		this.to = to;
	}

	public DateRange getRange() {
		return range;
	}

	public void setRange(DateRange range) {
		this.range = range;
	}

	protected LocalDate getFixedTo(LocalDate date) {
		if (date==null)
			return null;

		return date.plusDays(1);
	}

	public LocalDate calculateFrom() {
		LocalDate ret = range.getFrom();
		if (ret==null)
			return from;
		return ret;
	}

	public LocalDate calculateTo() {
		LocalDate ret = getFixedTo(range.getTo());
		if (ret==null)
			return getFixedTo(to);
		return ret;
	}

	public Temporal convert(LocalDate date, Field field) {
		if (field==null)
			return date;

		return convert(date, field.getType());
	}

	public Temporal convert(LocalDate date, Class clazz) {
		if (LocalDate.class.isAssignableFrom(clazz))
			return date;

		if (Instant.class.isAssignableFrom(clazz))
			return from.atStartOfDay(ZoneId.systemDefault()).toInstant();

		throw new UnsupportedOperationException(String.format("Conversion between LocalDate and: %s is not supported", clazz.getSimpleName()));
	}

	public DateRangeQueryFilter with(DateRange range) {
		setRange(range);
		return this;
	}

	public DateRangeQueryFilter with(LocalDate from, LocalDate to) {
		setRange(DateRange.CUSTOM);
		setFrom(from);
		setTo(to);
		return this;
	}

	public DateRangeQueryFilter from(LocalDate from) {
		setRange(DateRange.CUSTOM);
		setFrom(from);
		return this;
	}

	public DateRangeQueryFilter to(LocalDate to) {
		setRange(DateRange.CUSTOM);
		setTo(to);
		return this;
	}

	public DateRangeQueryFilter last30Days() {
		return with(DateRange.LAST_30_DAYS);
	}

	public DateRangeQueryFilter last90Days() {
		return with(DateRange.LAST_90_DAYS);
	}

	public DateRangeQueryFilter currentMonth() {
		return with(DateRange.CURRENT_MONTH);
	}

	public DateRangeQueryFilter previousMonth() {
		return with(DateRange.PREVIOUS_MONTH);
	}

	public DateRangeQueryFilter currentYear() {
		return with(DateRange.CURRENT_YEAR);
	}

	public DateRangeQueryFilter previousYear() {
		return with(DateRange.PREVIOUS_YEAR);
	}

	public static DateRangeQueryFilter of() {
		return new DateRangeQueryFilter();
	}

	public static DateRangeQueryFilter ofLast30Days() {
		return of().with(DateRange.LAST_30_DAYS);
	}

	public static DateRangeQueryFilter ofLast90Days() {
		return of().with(DateRange.LAST_90_DAYS);
	}

	public static DateRangeQueryFilter ofCurrentMonth() {
		return of().with(DateRange.CURRENT_MONTH);
	}

	public static DateRangeQueryFilter ofPreviousMonth() {
		return of().with(DateRange.PREVIOUS_MONTH);
	}

	public static DateRangeQueryFilter ofCurrentYear() {
		return of().with(DateRange.CURRENT_YEAR);
	}

	public static DateRangeQueryFilter ofPreviousYear() {
		return of().with(DateRange.PREVIOUS_YEAR);
	}

	public static DateRangeQueryFilter of(DateRange range) {
		return of().with(range);
	}

	public static DateRangeQueryFilter of(LocalDate from, LocalDate to) {
		return of().with(from ,to);
	}

	public static DateRangeQueryFilter ofFrom(LocalDate from) {
		return of().from(from);
	}

	public static DateRangeQueryFilter ofTo(LocalDate to) {
		return of().to(to);
	}

	@Override
	public void accept(FilterQueryBuilder builder, String field) {
		builder.add(field, this);
	}

}

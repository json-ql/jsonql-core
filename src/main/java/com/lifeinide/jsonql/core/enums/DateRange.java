package com.lifeinide.jsonql.core.enums;

import com.lifeinide.jsonql.core.filters.DateRangeQueryFilter;

import java.time.LocalDate;
import java.util.function.Supplier;

/**
 * Constants for {@link DateRangeQueryFilter}.
 *
 * If you want to change the logic of providing today date, use {@link #setToday(Supplier)} static method.
 *
 * @author Lukasz Frankowski
 */
public enum DateRange {

	CUSTOM,

	LAST_30_DAYS {

		@Override
		public LocalDate getFrom() {
			return today().atStartOfDay().minusDays(30).toLocalDate();
		}

		@Override
		public LocalDate getTo() {
			return today().atStartOfDay().toLocalDate();
		}
		
	},

	LAST_90_DAYS {

		@Override
		public LocalDate getFrom() {
			return today().atStartOfDay().minusDays(90).toLocalDate();
		}

		@Override
		public LocalDate getTo() {
			return today().atStartOfDay().toLocalDate();
		}

	},

	CURRENT_MONTH {

		@Override
		public LocalDate getFrom() {
			return today().atStartOfDay().withDayOfMonth(1).toLocalDate();
		}

		@Override
		public LocalDate getTo() {
			return getFrom().plusMonths(1).minusDays(1);
		}
		
	},

	PREVIOUS_MONTH {

		@Override
		public LocalDate getFrom() {
			return CURRENT_MONTH.getFrom().minusMonths(1);
		}

		@Override
		public LocalDate getTo() {
			return getFrom().plusMonths(1).minusDays(1);
		}

	},

	CURRENT_YEAR {

		@Override
		public LocalDate getFrom() {
			return CURRENT_MONTH.getFrom().withMonth(1);
		}

		@Override
		public LocalDate getTo() {
			return getFrom().plusYears(1).minusDays(1);
		}

	},

	PREVIOUS_YEAR {

		@Override
		public LocalDate getFrom() {
			return CURRENT_YEAR.getFrom().minusYears(1);
		}

		@Override
		public LocalDate getTo() {
			return CURRENT_YEAR.getTo().minusYears(1);
		}

	};

	public LocalDate getFrom() {
		return null;
	}

	public LocalDate getTo() {
		return null;
	}

	/**********************************************************************************************************
	 * Today change support
	 **********************************************************************************************************/

	protected static Supplier<LocalDate> TODAY_SUPPLIER = LocalDate::now;

	public static void setToday(Supplier<LocalDate> s) {
		TODAY_SUPPLIER = s;
	}

	public static void resetToday() {
		TODAY_SUPPLIER = LocalDate::now;
	}

	public static LocalDate today() {
		return TODAY_SUPPLIER.get();
	}

}

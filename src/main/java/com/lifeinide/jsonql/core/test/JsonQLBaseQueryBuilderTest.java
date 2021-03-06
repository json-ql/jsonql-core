package com.lifeinide.jsonql.core.test;

import com.lifeinide.jsonql.core.dto.BasePageableRequest;
import com.lifeinide.jsonql.core.dto.Page;
import com.lifeinide.jsonql.core.dto.Sort;
import com.lifeinide.jsonql.core.enums.DateRange;
import com.lifeinide.jsonql.core.filters.*;
import com.lifeinide.jsonql.core.intr.FilterQueryBuilder;
import com.lifeinide.jsonql.core.intr.PageableResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @param <PC> Persistence context used by the concrete implementation of this test
 * @param <ID> Id type
 * @param <E> Entity
 * @author Lukasz Frankowski
 */
@TestInstance(Lifecycle.PER_CLASS)
public abstract class JsonQLBaseQueryBuilderTest<
	PC,
	ID extends Serializable,
	E extends IJsonQLTestEntity<ID>,
	F extends FilterQueryBuilder<E, Page<E>, ?, F>
> {

	public static final LocalDate TODAY = LocalDate.of(2018, Month.APRIL, 1);

	protected ID associatedEntityId = null;

	/**
	 * Builds empty entity object. Depending on the mapping ID should be set on this object already by this method, or it can be left to
	 * be autogenerated by the underlying persistence technology.
	 */
	@Nonnull protected abstract E buildEntity(ID previousId);

	/**
	 * Builds empty associated entity object. Depending on the mapping ID should be set on this object already by this method, or it can
	 * be left to be autogenerated by the underlying persistence technology.
	 *
	 * If the {@code E} class of main entity doesn't implement {@link IJsonQLTestParentEntity}, it can return null.
	 */
	@Nullable protected abstract Object buildAssociatedEntity();

	/**
	 * Executes the filter query builder test
	 */
	protected abstract void doTest(BiConsumer<PC, F> c);

	/**
	 * Should be executed in {@link BeforeAll} method to populate entities to the db
	 * @param save The consumer executing entity save
	 */
	@SuppressWarnings({"unchecked", "ConstantConditions"})
	protected void populateData(Consumer<IJsonQLBaseTestEntity> save) {
		Object associatedEntity = buildAssociatedEntity();
		if (associatedEntity instanceof IJsonQLBaseTestEntity) {
			save.accept((IJsonQLBaseTestEntity) associatedEntity);
			associatedEntityId = (ID) ((IJsonQLBaseTestEntity) associatedEntity).getId();
		}

		ID prevId = null;
		StringGen sg = new StringGen();
		
		for (int i = 1; i <=100; i++) {
			IJsonQLTestEntity<ID> entity = buildEntity(prevId);
			entity.setStringVal(phrase(sg.nextStr()));
			entity.setBooleanVal(i%2==0);
			entity.setLongVal((long) i);
			entity.setDecimalVal(new BigDecimal(i+".99"));
			entity.setDateVal(LocalDate.of(2018, Month.JANUARY, 1).plusDays(i-1));
			entity.setEnumVal(JsonQLTestEntityEnum.values()[i % JsonQLTestEntityEnum.values().length]);
			if (supports(JsonQLQueryBuilderTestFeature.ASSOCIATED_ENTITY) && i%3==0)
				((IJsonQLTestParentEntity) entity).setEntityVal((IJsonQLBaseTestEntity) associatedEntity);
			save.accept(entity);
			prevId = entity.getId();
		}
	}

	protected String phrase(String s) {
		return "phrase-"+s;
	}

	/**
	 * Whether this persistence storage supports specific test feature.
	 * @see JsonQLQueryBuilderTestFeature
	 */
	protected boolean supports(JsonQLQueryBuilderTestFeature feature) {
		if (JsonQLQueryBuilderTestFeature.ASSOCIATED_ENTITY.equals(feature))
			return buildEntity(null) instanceof IJsonQLTestParentEntity;

		return true;
	}

	@SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
	protected BigDecimal decimal(String s) {
		BigDecimal decimal = new BigDecimal(s);
		if (supports(JsonQLQueryBuilderTestFeature.STRICT_INEQUALITIES))
			return decimal;

		// remove unncessary zeros
		while (decimal.scale()>0) {
			try {
				decimal = decimal.setScale(decimal.scale()-1);
			} catch (ArithmeticException e) {
				break;
			}
		}

		return decimal;
	}

	@BeforeAll
	public void setToday() {
		DateRange.setToday(() -> TODAY);
	}

	@AfterAll
	public void resetToday() {
		DateRange.resetToday();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void nullCheckTest() {
		doTest((pc, qb) -> {
			Assertions.assertEquals(100, qb
				.add("dateVal", (DateRangeQueryFilter) null)
				.add("entityVal", (EntityQueryFilter) null)
				.add("enumVal", (SingleValueQueryFilter) null)
				.add("longVal", (ValueRangeQueryFilter) null)
				.add("decimalVal", (ListQueryFilter) null)
				.list(null)
				.getCount());
		});
	}

	@Test
	public void testUnpaged() {
		doTest((pc, qb) -> {
			PageableResult<E> res = qb.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(100, res.getCount());
			Assertions.assertEquals(100, res.getData().size());
			Assertions.assertNull(res.getPageSize());
			Assertions.assertNull(res.getPagesCount());
			Assertions.assertEquals(1, (int) res.getPage());
		});
	}

	@Test
	public void testPaged() {
		doTest((pc, qb) -> {
			PageableResult<E> res = qb.list(BasePageableRequest.ofDefault().withPageSize(20));
			Assertions.assertEquals(100, res.getCount());
			Assertions.assertEquals(20, res.getData().size());
			Assertions.assertEquals(Integer.valueOf(20), res.getPageSize());
			Assertions.assertEquals(Integer.valueOf(5), res.getPagesCount());
			Assertions.assertEquals(Integer.valueOf(1), res.getPage());

			PageableResult<E> res2 = qb.list(BasePageableRequest.ofDefault().withPageSize(20).withPage(2));
			Assertions.assertEquals(100, res2.getCount());
			Assertions.assertEquals(20, res2.getData().size());
			Assertions.assertEquals(Integer.valueOf(20), res2.getPageSize());
			Assertions.assertEquals(Integer.valueOf(5), res2.getPagesCount());
			Assertions.assertEquals(Integer.valueOf(2), res2.getPage());

			for (E e: res2.getData()) {
				Assertions.assertFalse(res.getData().contains(e));
			}
		});
	}

	@Test
	public void testStringFilter() {
		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("stringVal", SingleValueQueryFilter.of(phrase("aa")))
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(1, res.getCount());
			Assertions.assertEquals(phrase("aa"), res.getData().iterator().next().getStringVal());
		});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("stringVal", SingleValueQueryFilter.of(phrase("aa")).ne())
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(99, res.getCount());
		});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("stringVal", SingleValueQueryFilter.of(phrase("aa")).ge())
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(100, res.getCount());
		});

		if (supports(JsonQLQueryBuilderTestFeature.STRICT_INEQUALITIES))
			doTest((pc, qb) -> {
				PageableResult<E> res = qb
					.add("stringVal", SingleValueQueryFilter.of(phrase("aa")).gt())
					.list(BasePageableRequest.ofUnpaged());
				Assertions.assertEquals(99, res.getCount());
			});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("stringVal", SingleValueQueryFilter.of(phrase("ba")).le())
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(27, res.getCount());
		});

		if (supports(JsonQLQueryBuilderTestFeature.STRICT_INEQUALITIES))
			doTest((pc, qb) -> {
				PageableResult<E> res = qb
					.add("stringVal", SingleValueQueryFilter.of(phrase("ba")).lt())
					.list(BasePageableRequest.ofUnpaged());
				Assertions.assertEquals(26, res.getCount());
			});

		if (supports(JsonQLQueryBuilderTestFeature.NULLS))
			doTest((pc, qb) -> {
				PageableResult<E> res = qb
					.add("stringVal", SingleValueQueryFilter.ofNotNull())
					.list(BasePageableRequest.ofUnpaged());
				Assertions.assertEquals(100, res.getCount());
			});

		if (supports(JsonQLQueryBuilderTestFeature.NULLS))
			doTest((pc, qb) -> {
				PageableResult<E> res = qb
					.add("stringVal", SingleValueQueryFilter.ofNull())
					.list(BasePageableRequest.ofUnpaged());
				Assertions.assertEquals(0, res.getCount());
			});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("stringVal", ListQueryFilter.of(SingleValueQueryFilter.of(phrase("aa")), SingleValueQueryFilter.of(phrase("ab"))).and())
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(0, res.getCount());
		});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("stringVal", ListQueryFilter.of(SingleValueQueryFilter.of(phrase("aa")), SingleValueQueryFilter.of(phrase("ab"))))
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(2, res.getCount());
		});
	}

	@Test
	public void testBooleanFilter() {
		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("booleanVal", SingleValueQueryFilter.of(true))
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(50, res.getCount());
			for (E e: res)
				Assertions.assertTrue(e.isBooleanVal());
		});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("booleanVal", SingleValueQueryFilter.of(true).ne())
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(50, res.getCount());
			for (E e: res)
				Assertions.assertFalse(e.isBooleanVal());
		});
	}

	@Test
	public void testEnumFilter() {
		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("enumVal", SingleValueQueryFilter.of(JsonQLTestEntityEnum.A))
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(33, res.getCount());
			for (E e: res)
				Assertions.assertEquals(JsonQLTestEntityEnum.A, e.getEnumVal());
		});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("enumVal", SingleValueQueryFilter.of(JsonQLTestEntityEnum.A).ne())
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(67, res.getCount());
			for (E e: res)
				Assertions.assertNotEquals(JsonQLTestEntityEnum.A, e.getEnumVal());
		});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("enumVal", ListQueryFilter.of(SingleValueQueryFilter.of(JsonQLTestEntityEnum.A), SingleValueQueryFilter.of(JsonQLTestEntityEnum.B)))
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(67, res.getCount());
			for (E e: res)
				Assertions.assertNotEquals(JsonQLTestEntityEnum.C, e.getEnumVal());
		});
	}

	@Test
	public void testLongFilter() {
		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("longVal", SingleValueQueryFilter.of(1L))
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(1, res.getCount());
			Assertions.assertEquals(1L, (long) res.iterator().next().getLongVal());
		});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("longVal", SingleValueQueryFilter.of(1L).ge())
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(100, res.getCount());
		});

		if (supports(JsonQLQueryBuilderTestFeature.STRICT_INEQUALITIES))
			doTest((pc, qb) -> {
				PageableResult<E> res = qb
					.add("longVal", SingleValueQueryFilter.of(1L).gt())
					.list(BasePageableRequest.ofUnpaged());
				Assertions.assertEquals(99, res.getCount());
			});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("longVal", ValueRangeQueryFilter.ofFrom(10L))
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(91, res.getCount());
		});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("longVal", ValueRangeQueryFilter.ofTo(10L))
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(10, res.getCount());
		});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("longVal", ValueRangeQueryFilter.of(10L, 20L))
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(11, res.getCount());
		});
	}

	@Test
	public void testDecimalFilter() {
		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("decimalVal", SingleValueQueryFilter.of(decimal("1.99")))
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(1, res.getCount());
			Assertions.assertEquals(new BigDecimal("1.99"), res.iterator().next().getDecimalVal());
		});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("decimalVal", SingleValueQueryFilter.of(decimal("1.99")).ge())
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(100, res.getCount());
		});

		if (supports(JsonQLQueryBuilderTestFeature.STRICT_INEQUALITIES))
			doTest((pc, qb) -> {
				PageableResult<E> res = qb
					.add("decimalVal", SingleValueQueryFilter.of(decimal("1.99")).gt())
					.list(BasePageableRequest.ofUnpaged());
				Assertions.assertEquals(99, res.getCount());
			});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("decimalVal", ValueRangeQueryFilter.ofFrom(decimal("10.99")))
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(91, res.getCount());
		});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("decimalVal", ValueRangeQueryFilter.ofTo(decimal("10.99")))
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(10, res.getCount());
		});

		// this is important test for BigDecimal because it checks if (10.99, 11.99 ... 19.99) are not included between 1.99 and 2.99 for
		// full text indexes and other storages treating BigDecimal-s as String-s
		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("decimalVal", ValueRangeQueryFilter.of(decimal("1.99"), decimal("2.99")))
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(2, res.getCount());
		});
	}

	@Test
	public void testDateFilter() {
		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("dateVal", DateRangeQueryFilter.ofCurrentMonth())
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(10, res.getCount());
			for (E e: res)
				Assertions.assertEquals(Month.APRIL, e.getDateVal().getMonth());
		});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("dateVal", DateRangeQueryFilter.ofPreviousMonth())
				.list(BasePageableRequest.ofUnpaged());
			if (supports(JsonQLQueryBuilderTestFeature.STRICT_INEQUALITIES)) {
				Assertions.assertEquals(31, res.getCount());
				for (E e: res)
					Assertions.assertEquals(Month.MARCH, e.getDateVal().getMonth());
			} else
				Assertions.assertEquals(32, res.getCount());
		});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("dateVal", DateRangeQueryFilter.ofCurrentYear())
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(100, res.getCount());
		});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("dateVal", DateRangeQueryFilter.ofPreviousYear())
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(supports(JsonQLQueryBuilderTestFeature.STRICT_INEQUALITIES) ? 0 : 1, res.getCount());
		});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("dateVal", DateRangeQueryFilter.ofLast30Days())
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(supports(JsonQLQueryBuilderTestFeature.STRICT_INEQUALITIES) ? 31 : 32, res.getCount());
		});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("dateVal", DateRangeQueryFilter.ofLast90Days())
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(supports(JsonQLQueryBuilderTestFeature.STRICT_INEQUALITIES) ? 91 : 92, res.getCount());
		});

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("dateVal", DateRangeQueryFilter.of(LocalDate.of(2018, Month.FEBRUARY, 1), LocalDate.of(2018, Month.FEBRUARY, 10)))
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(supports(JsonQLQueryBuilderTestFeature.STRICT_INEQUALITIES) ? 10 : 11, res.getCount());
		});
	}

	@Test
	public void testListFilter() {
		// and condition
		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("longVal", ListQueryFilter.of(SingleValueQueryFilter.of(10L).ge(), SingleValueQueryFilter.of(20L).le()).and())
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(11, res.getCount());
			for (E e: res)
				Assertions.assertTrue(e.getLongVal() >= 10 && e.getLongVal() <= 20);
		});

		// or condition
		if (supports(JsonQLQueryBuilderTestFeature.STRICT_INEQUALITIES))
			doTest((pc, qb) -> {
				PageableResult<E> res = qb
					.add("longVal", ListQueryFilter.of(SingleValueQueryFilter.of(10L).lt(), SingleValueQueryFilter.of(20L).gt()))
					.list(BasePageableRequest.ofUnpaged());
				Assertions.assertEquals(89, res.getCount());
				for (E e: res)
					Assertions.assertTrue(e.getLongVal() < 10 || e.getLongVal() > 20);
			});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testMultipleFilters() {
		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("stringVal", SingleValueQueryFilter.of(phrase("ba")).ge())
				.add("longVal", ListQueryFilter.of(SingleValueQueryFilter.of(36L).le(), SingleValueQueryFilter.of(50L).ge()))
				.add("enumVal", SingleValueQueryFilter.of(JsonQLTestEntityEnum.A))
				.add("dateVal", DateRangeQueryFilter.ofTo(TODAY))
				.list(BasePageableRequest.ofUnpaged().withSort(Sort.ofDesc("longVal")));
			Assertions.assertEquals(18, res.getCount());
			E prev = null;
			for (E e: res) {
				Assertions.assertTrue(e.getStringVal().compareTo("ba") >= 0);
				Assertions.assertTrue(e.getLongVal() <= 36L || e.getLongVal() >= 50L);
				Assertions.assertEquals(JsonQLTestEntityEnum.A, e.getEnumVal());
				Assertions.assertTrue(e.getDateVal().isBefore(TODAY));

				if (supports(JsonQLQueryBuilderTestFeature.SORTING)) {
					// test sorting
					if (prev != null)
						Assertions.assertTrue(prev.getLongVal() > e.getLongVal());
					prev = e;
				}
			}
		});
	}

	@Test
	public void testEntityFilter() {
		if (!supports(JsonQLQueryBuilderTestFeature.ASSOCIATED_ENTITY))
			return;

		doTest((pc, qb) -> {
			PageableResult<E> res = qb
				.add("entityVal", EntityQueryFilter.of(associatedEntityId))
				.list(BasePageableRequest.ofUnpaged());
			Assertions.assertEquals(33, res.getCount());
			for (E e: res)
				Assertions.assertEquals(associatedEntityId, ((IJsonQLTestParentEntity) e).getEntityVal().getId());
		});

		if (supports(JsonQLQueryBuilderTestFeature.NULLS))
			doTest((pc, qb) -> {
				PageableResult<E> res = qb
					.add("entityVal", EntityQueryFilter.ofNotNull())
					.list(BasePageableRequest.ofUnpaged());
				Assertions.assertEquals(33, res.getCount());
				for (E e: res)
					Assertions.assertEquals(associatedEntityId, ((IJsonQLTestParentEntity) e).getEntityVal().getId());
			});

		if (supports(JsonQLQueryBuilderTestFeature.NULLS))
			doTest((pc, qb) -> {
				PageableResult<E> res = qb
					.add("entityVal", EntityQueryFilter.ofNull())
					.list(BasePageableRequest.ofUnpaged());
				Assertions.assertEquals(67, res.getCount());
				for (E e: res)
					Assertions.assertNull(((IJsonQLTestParentEntity) e).getEntityVal());
			});
	}

}

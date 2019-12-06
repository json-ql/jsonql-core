package com.lifeinide.jsonql.core.test;

import com.lifeinide.jsonql.core.enums.QueryCondition;

import java.math.BigDecimal;

/**
 * @author Lukasz Frankowski
 */
public enum QueryBuilderTestFeature {

	/**
	 * Whether supports {@code >} and {@code <} inequalites, i.e. {@link QueryCondition#ge} and {@link QueryCondition#le}. For example
	 * lucene queries don't support them. They support only non-strict equalites {@code >=} and {@code <=}.
	 */
	STRICT_INEQUALITIES,

	/**
	 * Whether isNull / notNull queries are supported. Unsupported for example for Lucene queries.
	 */
	NULLS,

	/**
	 * Whether the storage stores {@link BigDecimal} in strict mode, e.g. 1.00 = 1.00. Lucene rounds it by default and 1.00 = 1.
	 */
	STRICT_DECIMALS,

	/**
	 * Whether the storage supports custom elements ordering. Lucene doesn't support it but returns data according to their search score.
	 */
	SORTING,

}

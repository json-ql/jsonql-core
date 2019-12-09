package com.lifeinide.jsonql.core.test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @param <A> Associated entity type.
 * @author Lukasz Frankowski
 */
public interface IJsonQLTestEntity<ID extends Serializable, A extends IJsonQLBaseTestEntity> extends IJsonQLBaseTestEntity<ID> {

	String getStringVal();

	void setStringVal(String stringVal);

	Long getLongVal();

	void setLongVal(Long longVal);

	BigDecimal getDecimalVal();

	void setDecimalVal(BigDecimal decimalVal);

	LocalDate getDateVal();

	void setDateVal(LocalDate dateVal);

	JsonQLTestEntityEnum getEnumVal();

	void setEnumVal(JsonQLTestEntityEnum enumVal);

	A getEntityVal();

	void setEntityVal(A entityVal);
	
}

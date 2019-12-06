package com.lifeinide.jsonql.core.test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @param <A> Associated entity type.
 * @author Lukasz Frankowski
 */
public interface IEntity<ID extends Serializable, A extends IBaseEntity> extends IBaseEntity<ID> {

	String getStringVal();

	void setStringVal(String stringVal);

	Long getLongVal();

	void setLongVal(Long longVal);

	BigDecimal getDecimalVal();

	void setDecimalVal(BigDecimal decimalVal);

	LocalDate getDateVal();

	void setDateVal(LocalDate dateVal);

	EntityEnum getEnumVal();

	void setEnumVal(EntityEnum enumVal);

	A getEntityVal();

	void setEntityVal(A entityVal);
	
}

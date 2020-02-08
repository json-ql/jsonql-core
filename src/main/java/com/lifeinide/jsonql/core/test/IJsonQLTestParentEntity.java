package com.lifeinide.jsonql.core.test;

import java.io.Serializable;

/**
 * An extension to {@link IJsonQLTestEntity} working with associated entity and corresponding
 * {@link JsonQLBaseQueryBuilderTest#testEntityFilter()} test.
 *
 * @author Lukasz Frankowski
 */
public interface IJsonQLTestParentEntity<ID extends Serializable, A extends IJsonQLBaseTestEntity> extends IJsonQLBaseTestEntity<ID> {

	A getEntityVal();

	void setEntityVal(A entityVal);

}

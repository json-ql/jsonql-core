package com.lifeinide.jsonql.core.test;

import java.io.Serializable;

/**
 * @author Lukasz Frankowski
 */
public interface IJsonQLTestParentEntity<ID extends Serializable, A extends IJsonQLBaseTestEntity> extends IJsonQLBaseTestEntity<ID> {

	A getEntityVal();

	void setEntityVal(A entityVal);

}

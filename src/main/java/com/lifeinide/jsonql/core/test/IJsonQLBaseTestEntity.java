package com.lifeinide.jsonql.core.test;

import java.io.Serializable;

/**
 * @author Lukasz Frankowski
 */
public interface IJsonQLBaseTestEntity<ID extends Serializable> {

	ID getId();

	void setId(ID id);
	
}

package com.tproject.nucleus;

/**
 * Example with dependency to ModuleA
 */
public class NucleusUtilsB {

	public boolean example() {
		return getNucleusUtils().checkNucleus();
	}

	public NucleusUtils getNucleusUtils() {
		return new NucleusUtils();
	}

}
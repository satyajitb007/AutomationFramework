package com.framework.interfaces;

import org.testng.Assert;

public interface IExceptionHandler {
	default void exception(Exception e) {
	}

	default void exception(Exception e, boolean terminate) {
	}

}
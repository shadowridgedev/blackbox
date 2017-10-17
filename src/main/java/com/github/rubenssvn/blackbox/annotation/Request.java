package com.github.rubenssvn.blackbox.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.rubenssvn.blackbox.enums.RequestMethod;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Request {

	String body() default "";
	RequestMethod method();
	String path() default "";
	
}

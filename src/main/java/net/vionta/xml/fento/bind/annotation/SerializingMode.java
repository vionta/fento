package net.vionta.xml.fento.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SerializingMode {

	public int mode() default CREATE_ON_NOT_EXISTING ; 

	public static final int FAIL_ON_NOT_EXISTING = 1;
	public static final int CREATE_ON_NOT_EXISTING = 2;
	public static final int AVOID_ON_NOT_EXISTING = 3;
	
}

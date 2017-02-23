package io.owenrbee.xls.xls2pojo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the target class for XLS row to java object transformation. 
 * <p>The row data must matched the provided @{@link Marker} hints before the transformation.
 * 
 * @author owenrbee@gmail.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Row {
	
	/**
	 * 
	 * @return the configured marker hints.
	 */
	Marker[] value() default {};

}

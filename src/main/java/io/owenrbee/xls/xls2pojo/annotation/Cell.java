package io.owenrbee.xls.xls2pojo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use to identify the source XLS cell of the Java object field. 
 * 
 * @author owenrbee@gmail.com
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cell {

	/**
	 * the XLS column reference value
	 * @return example, first column is "A".
	 */
	String value();

	/**
	 * Applicable only for date/time data type.
	 * @return Format string for parsing date/time.
	 */
	String format() default "";
}

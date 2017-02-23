package io.owenrbee.xls.xls2pojo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is the marker or hint used in conjunction with the @{@link Row} annotation 
 * to specify the criteria set of the target class. 
 * 
 * @author owenrbee@gmail.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Marker {

	/**
	 * @return the static value to compare against the XLS cell.
	 */
	String value();

	/**
	 * @return true to enable regex matching
	 */
	boolean regex() default false;

	/**
	 * @return the reference to the XLS cell.
	 */
	Cell cell();
}

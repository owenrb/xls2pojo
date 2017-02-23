package io.owenrbee.xls.xls2pojo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation marker for the observer class.
 * This contains meta-data information about the source XLS sheet name and where to locate the target Java class for the transformation. 
 * 
 * <p>Additionally, all subscriber methods, annotated with @{@link com.google.common.eventbus.Subscribe}, should be declared in this observer class.
 * 
 * @author owenrbee@gmail.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Sheet {

	/**
	 * Specify the target XLS sheet name; or 0-based index value of the sheet. 
	 * @return default is "0", the first sheet in the XLS file.
	 */
	String value() default "0";
	
	/**
	 * Specify the package name/s of the target @{@link Row} class.
	 * @return the set of package name where the target class/es, annotated with @{@link Row}, is/are declared.
	 */
	String[] scanPackage() default {};
}

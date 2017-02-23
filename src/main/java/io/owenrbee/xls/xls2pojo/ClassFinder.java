package io.owenrbee.xls.xls2pojo;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.google.common.reflect.ClassPath;

/**
 * Facade for retrieving all classes under the given package.
 * 
 * @author owenrbee@gmail.com
 * 
 */
public final class ClassFinder {

	/**
	 * Find all classes under the given package name.
	 * <p>Note that {@link com.google.common.reflect.ClassPath} is used to determine the class information.
	 *  
	 * @param scannedPackage - the package name
	 * @return the set of class information
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Set<Class<?>> find(String scannedPackage) throws IOException, ClassNotFoundException {

		Set<Class<?>> classes = new HashSet<Class<?>>();

		ClassPath cp = ClassPath.from(Thread.currentThread()
				.getContextClassLoader());

		for (ClassPath.ClassInfo info : cp.getTopLevelClassesRecursive(scannedPackage)) {
			classes.add(Class.forName(info.getName()));
		}

		return classes;
	}

}

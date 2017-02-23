package io.owenrbee.xls.xls2pojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.owenrbee.xls.xls2pojo.annotation.Cell;
import io.owenrbee.xls.xls2pojo.annotation.Marker;
import io.owenrbee.xls.xls2pojo.annotation.Row;
import io.owenrbee.xls.xls2pojo.annotation.Sheet;

/**
 * The main parser module.
 * 
 * @author owenrbee@gmail.com
 *
 */
public class Parser {
	
	private static final Logger LOG = LoggerFactory.getLogger(Parser.class);

	private InputStream source;
	private HSSFSheet xls;
	
	/**
	 * Constructor using input stream.
	 * @param source
	 */
	public Parser(InputStream source) {
		this.source = source;
	}
	
	/**
	 * Constructor using file pointer.
	 * @param file
	 * @throws FileNotFoundException
	 */
	public Parser(File file) throws FileNotFoundException {
		this(new FileInputStream(file));
	}
	
	/**
	 * 
	 * @param <T>
	 * @param observerClass - the observer class that must be annotate with {@link Sheet}.
	 * <p>					All subscriber methods, annotated with @{@link com.google.common.eventbus.Subscribe}, should be declared in this class. 
	 * @return
	 * @throws ParserException
	 */
	public <T> T read(Class<T> observerClass) throws ParserException {
		
		LOG.debug("target class: " + observerClass);
		
		// find entry point annotation
		if(!observerClass.isAnnotationPresent(Sheet.class)) {
			LOG.error(observerClass + " must be annotated with @Sheet.");
			return null;
		}
		
		// get sheet info
		Sheet sheet = observerClass.getAnnotation(Sheet.class);
		String sheetName = sheet.value();
		Set<Class<?>> classes = getRowClasses(sheet);

		LOG.debug("target class count: " + classes.size());
		
		// attempt to open the spreadsheet.
		try {
			xls = openSheet(sheetName);
			if(xls == null) {
				throw new ParserException("Sheet not found in XLS file: " + sheetName);
			}
		} catch (IOException e) {
			throw new ParserException("Unable read XLS file.", e);
		}
		
		T instance = null;
		try {
			instance = observerClass.newInstance();
			ParserEventBus.getInstance().register(instance);
		} catch (Exception e) {
			throw new ParserException("Unable to instatiate " + observerClass, e);
		}
		
		process(classes);
		
		boolean res = ParserEventBus.getInstance().unregister(instance);
		
		LOG.debug("Unregister status : " + res + "; subscriber count = " + ParserEventBus.getInstance().getSubscribers().size());
				
		return instance;
	}
	
	/**
	 * Read each row in the spreadsheet and attempt to transform each to any of the given class provided. 
	 * 
	 * @param classes - the set class annotated with {@link Row}.
	 * @throws ParserException
	 */
	protected void process(Set<Class<?>> classes) throws ParserException {
		
		int max = xls.getLastRowNum();
		
		// traverse all rows
		for(int rowNum = 0; rowNum < max; rowNum++) {
			
			HSSFRow row = xls.getRow(rowNum);
			
			// determine the target class
			Set<Class<?>> targets = detectClass(row, classes);
			
			if(targets == null || targets.isEmpty()) {
				onIgnoredRow(rowNum, row);
			} else {
			
				for (Class<?> klass : targets) {
					
					// create the object based from the given row.
					Object instance = tranformRow(row, klass);
					
					ParserEventBus.getInstance().post(instance);
				}
			}
			
		}
		
	}
	
	/**
	 * The method is invoked if the given row did not match any of the target classes.
	 * Override this method for special case, i.e, debugging.
	 * 
	 * @param row
	 */
	protected void onIgnoredRow(int rowNum, HSSFRow row) {
		LOG.debug("ignored row @" + (rowNum + 1));
		
	}

	/**
	 * Transform the XLS row to java object, and perform the data-mapping
	 * 
	 * @param row - the source XLS row
	 * @param klass - the target Java class type
	 * @return the class object instance
	 * @throws ParserException
	 */
	protected Object tranformRow(HSSFRow row, Class<?> klass) throws ParserException {
		
		Object instance = null;
		try {
			instance = klass.newInstance();	
		} catch (Exception e) {			
			throw new ParserException("Unable to instantiate class", e);
		}
		

		for (Field field : klass.getDeclaredFields()) {

			// exclude static variables
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			
			// look for @Cell field annotation
			if(field.isAnnotationPresent(Cell.class)) {
				Cell cell = field.getAnnotation(Cell.class);
				processCell(cell, instance, field, row);
			}
		}
		
		return instance;
	}
	
	/**
	 * Determine the source XLS column provided in the {@link Cell} annotation declaration.
	 * 
	 * @param cell - contains source meta-data information
	 * @param object - the target object instance.
	 * @param field - the target object property
	 * @param row - the source XLS row.
	 * @throws ParserException
	 */
	protected void processCell(Cell cell, Object object, Field field, HSSFRow row) throws ParserException {
		
		Column column = new Column(cell.value());
		
		assignValue(field, column, object, row, cell.format());
	}
	
	/**
	 * Assign row column vale to the target Java object field.
	 * 
	 * @param field - the target field information
	 * @param column - the XLS column reference
	 * @param target - the target object instance
	 * @param row - the source XLS row
	 * @param format - the optional data format; useful for parsing date string.
	 * @throws ParserException
	 */
	protected void assignValue(Field field, Column column, Object target, HSSFRow row, String format) throws ParserException {
		
		HSSFCell col = row == null ? null : row.getCell(column.getIndex());
		String value = col == null ? null : col.toString();
		
		if(value == null) {
			return;
		}
		
		field.setAccessible(true);
		try {
			if(Integer.class.equals(field.getType())) {
				Integer i = ((Double) Double.parseDouble(value)).intValue();
				field.set(target, i);
			} else if(Long.class.equals(field.getType())) {
				Long l = ((Double) Double.parseDouble(value)).longValue();
				field.set(target, l);
			} else if(Date.class.equals(field.getType())) {
				SimpleDateFormat dateParser=new SimpleDateFormat(format);
				Date date = dateParser.parse(value);
				field.set(target, date);
			} else if (Double.class.equals(field.getType())) {
				Double d = Double.parseDouble(value);
				field.set(target, d);
			} else {
				field.set(target, value);
			}
		} catch (Exception e) {
			String fieldName = field.getName();
			LOG.warn("Unable to assign field: " + fieldName + "; value: [" + value + "] for type: " + field.getType(), e);
		}
	}

	/**
	 * Detect the target class by analysing the row content.
	 * 
	 * @param row - the row in question
	 * @param classes - the set of candidate target class
	 * @return
	 */
	protected Set<Class<?>> detectClass(HSSFRow row, Set<Class<?>> classes) {
		
		if(row == null) {
			return null;
		}
		
		Set<Class<?>> target = new HashSet<Class<?>>();
		
		for(Class<?> klass : classes) {
		
			// check validity of the target class
			Row annotation = klass.getAnnotation(Row.class);
			if(annotation == null) {
				continue;
			}
			
			int length = annotation.value().length;
			if(length == 0) {
				continue;
			}
			
			boolean match = true;
			
			for(int k = 0; k < length; k++) {
				
				// get meta-data information
				Marker labelMeta = annotation.value()[k];
				String marker = labelMeta.value();
				Cell cellMeta = labelMeta.cell();
				
				String loc = cellMeta.value();
				Column column = new Column(loc);
				int x = column.getIndex();
				
				// get the source cell 
				HSSFCell cell = row.getCell(x);
				
				if(cell != null) {
					// get the XLS string value
					String value = cell.toString();
					
					// check for regular expression checking option
					if(value != null && labelMeta.regex()) {
						if (!value.matches(marker)) {
							LOG.debug(value + " does not match with " + marker);
							match = false;
							break;
						}
					} else if(!marker.equals(value)) { // else perform normal comparison
						match = false;
						break;
					}
				} else {
					match = false;
					break;
				}
			}
			
			if(match) {
				// if all matches the criteria
				target.add(klass);
			}
		}
		
		return target;
	}

	/**
	 * Determine the target classes provided in the {@link Sheet} class annotation declaration. 
	 * 
	 * @param <T>
	 * @param sheet - contains meta-data information of the target package for scanning
	 * @return
	 * @throws ParserException
	 */
	protected <T> Set<Class<?>> getRowClasses(Sheet sheet) throws ParserException {
		
		String[] packages = sheet.scanPackage();
		
		Set<Class<?>> rows = new HashSet<Class<?>>(); 
		
		for(String scanPackage : packages) {
			LOG.debug("scan package: " + scanPackage);
			
			try {
				Set<Class<?>> classes = ClassFinder.find(scanPackage);
			
				for(Class<?> klass : classes) {
					if(klass.isAnnotationPresent(Row.class)) {
						rows.add(klass);
					}
				}
				
			} catch (Exception e) {
				throw new ParserException("Unable to retrieve class information from package: " + scanPackage, e);
			} 
			
			
		}
		
		return rows;
	}


	/**
	 * Open the source XLS sheet for reading.
	 * 
	 * @param sheetName - the source sheet name; or 0-based index integer string value.
	 * @return
	 * @throws IOException
	 */
	protected HSSFSheet openSheet(String sheetName) throws IOException {

		POIFSFileSystem fs = new POIFSFileSystem(source);
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		
		return NumberUtils.isNumber(sheetName) 
			? wb.getSheetAt(Integer.parseInt(sheetName))
			: wb.getSheet(sheetName);
	}
	

}

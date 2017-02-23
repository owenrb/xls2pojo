package io.owenrbee.xls.xls2pojo;

/**
 * The general exception class for this module.
 * 
 * @author owenrbee@gmail.com
 */
public class ParserException extends Exception {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public ParserException() {
		super();
	}

	/**
	 * Chained constructor with error message.
	 * 
	 * @param message
	 */
	public ParserException(String message) {
		super(message);
	}
	
	/**
	 * Chained constructor with error message and source exception.
	 * 
	 * @param message
	 * @param e
	 */
	public ParserException(String message, Exception e) {
		super(message, e);
	}

}

package io.owenrbee.xls.xls2pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

/**
 * The singleton class for the event bus.
 * 
 * @author owenrbee@gmail.com
 */
public class ParserEventBus implements SubscriberExceptionHandler {

    private final EventBus eventBus = new EventBus(this);
    private static ParserEventBus dis;
    
    private List<Object> subscribers;
    
    private ParserEventBus() {
    	subscribers = new ArrayList<Object>();
    }
    
    
    /**
     * Get the event bus object. 
     * 
     * @return the one and only instance of the event bus.
     */
    public static ParserEventBus getInstance() {
    	if(dis == null) {
    		synchronized(ParserEventBus.class) {
    			dis = new ParserEventBus();
    		}
    	}
    	
    	return dis;
    }

    /**
     * Post a subject
     * @param event
     */
    public void post(final Object event) {
        eventBus.post(event);
    }

    /**
     * Register an observer
     * @param object
     */
    public void register(final Object object) {
        eventBus.register(object);
        
        subscribers.add(object);
    }

    /**
     * Unregister an observer.
     * 
     * @param object
     * @return
     */
    public boolean unregister(final Object object) {
        eventBus.unregister(object);
        
        return subscribers.remove(object);
    }

    /**
     * Override this exception handler method for specific need.
     *  
     */
    @Override
    public  void handleException(final Throwable exception,
            final SubscriberExceptionContext context) {
       
    }

    /**
     * Return all the observer instance.
     * @return the unmodifiable list of subscribers.
     */
	public List<Object> getSubscribers() {
		return Collections.unmodifiableList(subscribers);
	}

}

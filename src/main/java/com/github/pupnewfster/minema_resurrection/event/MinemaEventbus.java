package com.github.pupnewfster.minema_resurrection.event;

import java.util.ArrayList;
import java.util.List;

/**
 * EventBus mechanism for a global observer pattern
 * <p>
 * Use the static EventBus instances declared by this class!
 *
 * @param <X> Generic parameter specifying the type of event
 */
public class MinemaEventbus<X> {

    public static final MinemaEventbus<CaptureEvent.Mid> midRenderBUS = new MinemaEventbus<>();
    public static final MinemaEventbus<CaptureEvent.End> endRenderBUS = new MinemaEventbus<>();

    private final List<IEventListener<X>> listeners;

    private MinemaEventbus() {
        this.listeners = new ArrayList<>(1);
    }

    /**
     * @param listener A listener now getting all events thrown by anyone calling {@link MinemaEventbus#throwEvent(Object)} on this EventBus instance
     */
    public void registerListener(IEventListener<X> listener) {
        this.listeners.add(listener);
    }

    /**
     * @param event Throw in an event to be listened by all listeners of this EventBus instance
     */
    public void throwEvent(X event) throws Exception {
        for (IEventListener<X> listener : this.listeners) {
            listener.onEvent(event);
        }
    }

    /**
     * Dereferences all registered listeners in all buses
     */
    public static void reset() {
        midRenderBUS.listeners.clear();
        endRenderBUS.listeners.clear();
    }
}
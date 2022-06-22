package com.github.pupnewfster.minema_resurrection.event;

@FunctionalInterface
public interface IEventListener<X> {

    void onEvent(X event) throws Exception;
}
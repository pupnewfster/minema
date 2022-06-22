package com.github.pupnewfster.minema_resurrection.cam.path;

public abstract class ActivePath {

    public abstract void tick();

    protected final void stop() {
        PathHandler.stopTravelling();
    }
}
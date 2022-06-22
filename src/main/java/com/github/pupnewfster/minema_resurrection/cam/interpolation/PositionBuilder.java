package com.github.pupnewfster.minema_resurrection.cam.interpolation;

import com.github.pupnewfster.minema_resurrection.cam.path.PolarCoordinates;
import com.github.pupnewfster.minema_resurrection.cam.path.Position;
import net.minecraft.world.phys.Vec3;

public final class PositionBuilder {

    private Vec3 position;
    private PolarCoordinates polarCoordinates;
    private float roll;
    private float fov;

    public PositionBuilder setPosition(Vec3 position) {
        this.position = position;
        return this;
    }

    public PositionBuilder setPolarCoordinates(PolarCoordinates polarCoordinates) {
        this.polarCoordinates = polarCoordinates;
        return this;
    }

    public PositionBuilder setRoll(float value) {
        this.roll = value;
        return this;
    }

    public PositionBuilder setFov(float value) {
        this.fov = value;
        return this;
    }

    public Vec3 getPosition() {
        return this.position;
    }

    public PolarCoordinates getPolarCoordinates() {
        return this.polarCoordinates;
    }

    public Position build() {
        return new Position(this.position.x, this.position.y, this.position.z, this.polarCoordinates.pitch(),
              this.polarCoordinates.yaw(), this.roll, this.fov);
    }
}
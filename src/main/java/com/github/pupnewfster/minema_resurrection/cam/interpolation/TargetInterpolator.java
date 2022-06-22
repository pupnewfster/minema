package com.github.pupnewfster.minema_resurrection.cam.interpolation;

import com.github.pupnewfster.minema_resurrection.cam.path.PolarCoordinates;
import com.github.pupnewfster.minema_resurrection.cam.path.Position;
import net.minecraft.world.phys.Vec3;

public record TargetInterpolator(Vec3 target) implements IPolarCoordinatesInterpolator {

    @Override
    public void interpolatePolarCoordinates(PositionBuilder builder, Position y0, Position y1, Position y2, Position y3, double step) {
        // 1.62 is the default height for player eye positions
        builder.setPolarCoordinates(PolarCoordinates.lookAt(target, builder.getPosition().add(0, 1.62, 0)));
    }
}
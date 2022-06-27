package com.github.pupnewfster.minema_resurrection.cam.path;

import com.github.pupnewfster.minema_resurrection.cam.interpolation.Interpolator;
import com.github.pupnewfster.minema_resurrection.util.CamUtils;
import net.minecraft.world.entity.player.Player;

public final class ActiveInterpolatorPath extends ActivePath {

    private final Interpolator interpolator;
    private final long iterations;
    private final Player player;

    private long currentIteration;

    public ActiveInterpolatorPath(Player player, Interpolator interpolator, long iterations) {
        this.interpolator = interpolator;
        this.iterations = iterations;
        this.player = player;
        CamUtils.teleport(this.player, this.interpolator.getPoint(0), true);
    }

    @Override
    public void tick() {
        this.currentIteration++;
        Position pos = this.interpolator.getPoint(this.currentIteration / (double) this.iterations);
        CamUtils.teleport(this.player, pos, false);
        if (this.currentIteration >= this.iterations) {
            stop();
        }
    }
}
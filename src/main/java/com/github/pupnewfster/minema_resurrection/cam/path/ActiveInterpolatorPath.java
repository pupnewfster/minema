package com.github.pupnewfster.minema_resurrection.cam.path;

import com.github.pupnewfster.minema_resurrection.MinemaResurrection;
import com.github.pupnewfster.minema_resurrection.cam.interpolation.Interpolator;
import com.github.pupnewfster.minema_resurrection.util.CamUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
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
        Position startPoint = this.interpolator.getPoint(0, this.iterations);
        updateTime(startPoint);
        CamUtils.teleport(this.player, startPoint, true);
    }

    @Override
    public void tick() {
        this.currentIteration++;
        Position pos = this.interpolator.getPoint(this.currentIteration, this.iterations);
        updateTime(pos);
        CamUtils.teleport(this.player, pos, false);
        if (this.currentIteration >= this.iterations) {
            stop();
        }
    }

    private void updateTime(Position pos) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.hasSingleplayerServer() && MinemaResurrection.instance.getConfig().applyPathDayTime.get() && pos.time != -1) {
            ServerPlayer serverPlayer = minecraft.getSingleplayerServer().getPlayerList().getPlayer(player.getUUID());
            if (serverPlayer != null) {
                //Double check we can actually find the player, we should be able to
                serverPlayer.serverLevel().setDayTime(pos.time);
            }
            if (player instanceof LocalPlayer clientPlayer) {
                clientPlayer.clientLevel.setDayTime(pos.time);
            }
        }
    }
}
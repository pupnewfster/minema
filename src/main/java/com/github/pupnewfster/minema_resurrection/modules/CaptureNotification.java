package com.github.pupnewfster.minema_resurrection.modules;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class CaptureNotification extends CaptureModule {

    @Override
    protected void doEnable() {
        playChickenPlop(1);
    }

    @Override
    protected void doDisable() {
        playChickenPlop(0.75f);
    }

    @Override
    protected boolean checkEnable() {
        return true;
    }

    private void playChickenPlop(float pitch) {
        Player player = minecraft.player;
        if (player != null && minecraft.level != null) {
            minecraft.level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.CHICKEN_EGG, SoundSource.NEUTRAL, 1, pitch);
        }
    }
}
package info.ata4.minecraft.minema.client.modules;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

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
        if (MC.level != null && MC.player != null) {
            MC.level.playSound(MC.player, MC.player.getX(), MC.player.getY(), MC.player.getZ(), SoundEvents.CHICKEN_EGG, SoundSource.NEUTRAL, 1,
                  pitch);
        }
    }

}

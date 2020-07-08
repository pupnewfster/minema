package info.ata4.minecraft.minema.client.modules;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class CaptureNotification extends CaptureModule {

	@Override
	protected void doEnable() throws Exception {
		playChickenPlop(1);
	}

	@Override
	protected void doDisable() throws Exception {
		playChickenPlop(0.75f);
	}

	@Override
	protected boolean checkEnable() {
		return true;
	}

	private void playChickenPlop(float pitch) {
		MC.world.playSound(MC.player, MC.player.getPosX(), MC.player.getPosY(), MC.player.getPosZ(), SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.NEUTRAL, 1,
				pitch);
	}

}

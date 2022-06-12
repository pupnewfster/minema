package info.ata4.minecraft.minema.client.modules;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

public abstract class CaptureModule {

    protected static final Minecraft MC = Minecraft.getInstance();
    protected static final Logger L = LogUtils.getLogger();
    private boolean enabled;

    public String getName() {
        return getClass().getSimpleName();
    }

    public synchronized final boolean isEnabled() {
        return enabled;
    }

    /**
     * Enables this module if the current configuration says so
     */
    public final void enable() throws Exception {
        synchronized (this) {
            if (enabled) {
                return;
            }
            if (!checkEnable()) {
                return;
            }
            enabled = true;
        }

        L.info("Enabling " + getName());
        try {
            doEnable();
        } catch (Exception e) {
            throw new Exception("Cannot enable module", e);
        }
    }

    /**
     * Disables this module if it was active. Even though it might throw an exception this module must recover into a state that makes it reusable for enabling again as
     * if it was freshly instantiated.
     */
    public final void disable() throws Exception {
        synchronized (this) {
            if (!enabled) {
                return;
            }
            enabled = false;
        }

        L.info("Disabling " + getName());
        try {
            doDisable();
        } catch (Exception e) {
            throw new Exception("Cannot disable module", e);
        }
    }

    protected abstract void doEnable() throws Exception;

    /**
     * @return True if this module should be enabled given the current configuration
     */
    protected abstract boolean checkEnable();

    protected abstract void doDisable() throws Exception;
}

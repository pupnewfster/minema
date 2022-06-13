package info.ata4.minecraft.minema.client.config.value;

import info.ata4.minecraft.minema.client.config.MinemaConfig;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class CachedFloatValue extends CachedPrimitiveValue<Double> {

    private float cachedValue;

    private CachedFloatValue(MinemaConfig config, ConfigValue<Double> internal) {
        super(config, internal);
    }

    public static CachedFloatValue wrap(MinemaConfig config, ConfigValue<Double> internal) {
        return new CachedFloatValue(config, internal);
    }

    public float get() {
        if (!resolved) {
            //If we don't have a cached value or need to resolve it again, get it from the actual ConfigValue
            //Note: For now we have to get it out of a double as there is no FloatValue config type
            Double val = internal.get();
            if (val == null) {
                cachedValue = 0;
            } else if (val > Float.MAX_VALUE) {
                cachedValue = Float.MAX_VALUE;
            } else if (val < -Float.MAX_VALUE) {
                //Note: Float.MIN_VALUE is the smallest positive value a float can represent
                // the smallest value a float can represent overall is -Float.MAX_VALUE
                cachedValue = -Float.MAX_VALUE;
            } else {
                cachedValue = val.floatValue();
            }
            resolved = true;
        }
        return cachedValue;
    }

    public void set(float value) {
        internal.set((double) value);
        cachedValue = value;
    }
}
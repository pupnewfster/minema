package info.ata4.minecraft.minema.client.event;

@FunctionalInterface
public interface IEventListener<X> {

    void onEvent(X event) throws Exception;
}
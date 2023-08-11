package ink.tuanzi.shulkerviewer.screen;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;

public final class HandledScreenEvents {

	public HandledScreenEvents() {}
	
	public static Event<HandledScreenKeyPress> KEY_PRESS = EventFactory.createArrayBacked(HandledScreenKeyPress.class, callbacks -> (client, keyCode, scanCode, modifiers) -> {
		for (HandledScreenKeyPress callback : callbacks) {
			callback.handleKeyPress(client, keyCode, scanCode, modifiers);
		}
	});

	@FunctionalInterface
	public interface HandledScreenKeyPress {
		void handleKeyPress(MinecraftClient client, int keyCode, int scanCode, int modifiers);
	}
}

package ink.tuanzi.shulkerviewer;

import ink.tuanzi.shulkerviewer.screen.HandledScreenEvents;
import ink.tuanzi.shulkerviewer.screen.TargetedItemStackProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ShulkerViewerClient implements ClientModInitializer {

    private final static KeyBinding openShulkerBind;

    static {
        openShulkerBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Open Shulker Box",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "Shulker Viewer"
        ));
    }

    @Override
    public void onInitializeClient() {
        AtomicReference<Screen> atomicBeforeScreen = new AtomicReference<>();

        HandledScreenEvents.KEY_PRESS.register((client, keyCode, scanCode, modifiers) -> {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                // press Escape
                if (atomicBeforeScreen.get() != null) {
                    client.setScreen(atomicBeforeScreen.get());
                    atomicBeforeScreen.set(null);
                }
            }

            if (openShulkerBind.matchesKey(keyCode, scanCode)) {
                if (client.currentScreen instanceof HandledScreen) {
                    TargetedItemStackProvider screen = (TargetedItemStackProvider) client.currentScreen;
                    ItemStack stack = screen.getCursorStack(client.mouse.getX(), client.mouse.getY());

                    if (stack != null) {
                        String itemId = Registries.ITEM.getId(stack.getItem()).toString();

                        if (itemId.endsWith("shulker_box")) {
                            atomicBeforeScreen.set(client.currentScreen);
                            List<ItemStack> stacks = getInventory(stack);
                            ShulkerBoxScreenHandler screenHandler = new ShulkerBoxScreenHandler(0, client.player.getInventory(), new SimpleInventory(stacks.toArray(new ItemStack[]{})));
                            ShulkerBoxScreen shulkerBoxScreen = new ShulkerBoxScreen(screenHandler, client.player.getInventory(), stack.getName());
                            client.setScreen(shulkerBoxScreen);
                        }
                    }
                }
            }
        });
    }

    public List<ItemStack> getInventory(ItemStack itemStack) {
        int invMaxSize = 27;
        List<ItemStack> inv = DefaultedList.ofSize(invMaxSize, ItemStack.EMPTY);
        NbtCompound blockEntityTag = itemStack.getSubNbt("BlockEntityTag");

        if (blockEntityTag != null && blockEntityTag.contains("Items", NbtElement.LIST_TYPE)) {
            NbtList itemList = blockEntityTag.getList("Items", NbtElement.COMPOUND_TYPE);

            if (itemList != null) {
                for (int i = 0, len = itemList.size(); i < len; ++i) {
                    NbtCompound itemTag = itemList.getCompound(i);
                    ItemStack s = ItemStack.fromNbt(itemTag);

                    if (!itemTag.contains("Slot", NbtElement.NUMBER_TYPE))
                        continue;
                    int slot = itemTag.getInt("Slot");

                    if (slot >= 0 && slot < invMaxSize)
                        inv.set(slot, s);
                }
            }
        }
        return inv;
    }
}
package ink.tuanzi.shulkerviewer.screen;

import net.minecraft.item.ItemStack;

public interface TargetedItemStackProvider {
    int getSlotIndex(double mouseX, double mouseY);

    ItemStack getCursorStack(double mouseX, double mouseY);
}

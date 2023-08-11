package ink.tuanzi.shulkerviewer.mixin;

import ink.tuanzi.shulkerviewer.screen.HandledScreenEvents;
import ink.tuanzi.shulkerviewer.screen.TargetedItemStackProvider;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements TargetedItemStackProvider {

    public HandledScreenMixin(T handler, Text title) {
        super(title);
        System.out.println("Mixin --- HandledScreenMixin");
        this.handler = handler;
    }

    @Inject(at = @At("RETURN"), method = "keyPressed")
    private void onHandledScreenKeyPress(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        HandledScreenEvents.KEY_PRESS.invoker().handleKeyPress(this.client, keyCode, scanCode, modifiers);
    }

    @Shadow
    private boolean isPointOverSlot(Slot slot, double pointX, double pointY) {
        return true;
    }

    @Shadow
    protected final T handler;

    @Override
    public int getSlotIndex(double mouseX, double mouseY) {
        double x = mouseX * (double) this.client.getWindow().getScaledWidth() / (double) this.client.getWindow().getWidth();
        double y = mouseY * (double) this.client.getWindow().getScaledHeight() / (double) this.client.getWindow().getHeight();

        for (int i = 0; i < this.handler.slots.size(); ++i) {
            Slot slot = this.handler.slots.get(i);
            if (this.isPointOverSlot(slot, x, y)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public ItemStack getCursorStack(double mouseX, double mouseY) {
        double x = mouseX * (double) this.client.getWindow().getScaledWidth() / (double) this.client.getWindow().getWidth();
        double y = mouseY * (double) this.client.getWindow().getScaledHeight() / (double) this.client.getWindow().getHeight();

        for (int i = 0; i < this.handler.slots.size(); ++i) {
            Slot slot = this.handler.slots.get(i);
            if (this.isPointOverSlot(slot, x, y)) {
                return slot.getStack();
            }
        }

        return null;
    }
}

package sciwhiz12.basedefense.item.lock;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import sciwhiz12.basedefense.LockingUtil;
import sciwhiz12.basedefense.api.lock.ILock;
import sciwhiz12.basedefense.api.lock.LockContext;

public class LockItem extends Item implements ILock {
    public LockItem() {
        super(new Item.Properties().maxDamage(0));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn,
            List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!flagIn.isAdvanced()) return;
        long[] ids = LockingUtil.getUnlockIDs(stack);
        if (ids.length != 0) {
            tooltip.add(
                new TranslationTextComponent("tooltip.basedefense.unlockids").applyTextStyle(
                    TextFormatting.GRAY
                )
            );
            for (long id : ids) {
                tooltip.add(
                    new StringTextComponent("  " + Long.toHexString(id)).applyTextStyle(
                        TextFormatting.DARK_GRAY
                    )
                );
            }
        }
    }

    @Override
    public boolean onUnlock(LockContext ctx) {
        if (ctx.getPlayer().isSneaking()) {
            ItemStack lock = ctx.getLockItem();
            ServerPlayerEntity player = (ServerPlayerEntity) ctx.getPlayer();
            boolean flag = player.inventory.addItemStackToInventory(lock);
            if (flag && lock.isEmpty()) {
                lock.setCount(1);
                ItemEntity itementity1 = player.dropItem(lock, false);
                if (itementity1 != null) { itementity1.makeFakeItem(); }

                ctx.getWorld().playSound(
                    (PlayerEntity) null, player.getPosX(), player.getPosY(), player.getPosZ(),
                    SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG()
                        .nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F
                );
                player.container.detectAndSendChanges();
            } else {
                ItemEntity itementity = player.dropItem(lock, false);
                if (itementity != null) {
                    itementity.setNoPickupDelay();
                    itementity.setOwnerId(player.getUniqueID());
                }
            }
            ctx.getLockable().setLock(ItemStack.EMPTY);
            return false;
        }
        return true;
    }
}

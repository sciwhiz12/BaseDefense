package sciwhiz12.basedefense.item.lock;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.ItemHandlerHelper;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.capabilities.AdminKeyLock;
import sciwhiz12.basedefense.capabilities.GenericCapabilityProvider;
import sciwhiz12.basedefense.tileentity.LockableTile;

import javax.annotation.Nullable;

import static sciwhiz12.basedefense.Reference.Capabilities.LOCK;
import static sciwhiz12.basedefense.Reference.ITEM_GROUP;

public class AdminPadlockItem extends AbstractPadlockItem {
    public AdminPadlockItem() {
        super(new Item.Properties().maxDamage(0).rarity(Rarity.EPIC).group(ITEM_GROUP));
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new GenericCapabilityProvider<>(() -> new AdminKeyLock() {
            @Override
            public void onRemove(IKey key, IWorldPosCallable worldPos, @Nullable PlayerEntity player) {
                super.onRemove(key, worldPos, player);
                worldPos.consume((world, pos) -> {
                    TileEntity te = world.getTileEntity(pos);
                    if (te instanceof LockableTile) {
                        LockableTile lockTile = (LockableTile) te;
                        ItemHandlerHelper.giveItemToPlayer(player, lockTile.getLockStack());
                        lockTile.setLockStack(ItemStack.EMPTY);
                    }
                });
            }
        }, LOCK);
    }
}

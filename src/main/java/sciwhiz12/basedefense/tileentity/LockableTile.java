package sciwhiz12.basedefense.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import sciwhiz12.basedefense.Reference.TileEntities;
import sciwhiz12.basedefense.capabilities.CodedItemStackLock;

import static sciwhiz12.basedefense.Reference.Capabilities.*;

public class LockableTile extends TileEntity {
    public static final String TAG_LOCK_ITEM = "LockItem";

    protected LazyOptional<CodedItemStackLock> lockCap;
    protected final CodedItemStackLock lock = new CodedItemStackLock();

    public LockableTile() {
        super(TileEntities.LOCKABLE_TILE);
    }

    public LockableTile(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == CONTAINS_CODE || cap == CODE_HOLDER || cap == LOCK) {
            if (lockCap == null) {
                lockCap = LazyOptional.of(() -> this.lock);
            }
            return lockCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tag = new CompoundNBT();
        tag.put(TAG_LOCK_ITEM, lock.getStack().write(new CompoundNBT()));
        return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.lock.setStack(ItemStack.read(pkt.getNbtCompound().getCompound(TAG_LOCK_ITEM)));
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        readData(compound);
    }

    public void readData(CompoundNBT compound) {
        lock.setStack(ItemStack.read(compound.getCompound(TAG_LOCK_ITEM)));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound = super.write(compound);
        compound = writeData(compound);
        return compound;
    }

    public CompoundNBT writeData(CompoundNBT compound) {
        compound.put(TAG_LOCK_ITEM, lock.getStack().write(new CompoundNBT()));
        return compound;
    }

    @Override
    public void remove() {
        super.remove();
        if (lockCap != null) {
            lockCap.invalidate();
            lockCap = null;
        }
    }

    public void setLockStack(ItemStack stack) {
        lock.setStack(stack);
        if (world != null) {
            this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.DEFAULT);
        }
        this.markDirty();
        if (lockCap != null) {
            lockCap.invalidate();
            lockCap = null;
        }
    }

    public ItemStack getLockStack() {
        return lock.getStack();
    }
}

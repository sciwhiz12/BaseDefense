package sciwhiz12.basedefense.container;

import it.unimi.dsi.fastutil.longs.LongLists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.StringUtils;
import sciwhiz12.basedefense.ClientReference.Textures;
import sciwhiz12.basedefense.Reference.Items;
import sciwhiz12.basedefense.api.capablities.ICodeHolder;
import sciwhiz12.basedefense.item.IColorable;
import sciwhiz12.basedefense.util.ContainerHelper;

import java.util.Random;

import static sciwhiz12.basedefense.Reference.Blocks;
import static sciwhiz12.basedefense.Reference.Capabilities.CODE_HOLDER;
import static sciwhiz12.basedefense.Reference.Containers;

public class KeysmithContainer extends Container {
    private static final Random RANDOM = new Random();
    private final IInventory outputSlot = new CraftResultInventory() {
        @Override
        public void markDirty() {
            super.markDirty();
            KeysmithContainer.this.onContentsChange();
        }
    };
    private final IInventory inputSlots = new Inventory(7) {
        @Override
        public void markDirty() {
            super.markDirty();
            KeysmithContainer.this.onContentsChange();
        }
    };
    private final IWorldPosCallable worldPos;
    private String customName = null;

    public KeysmithContainer(int windowId, PlayerInventory playerInv) {
        this(windowId, playerInv, IWorldPosCallable.DUMMY);
    }

    public KeysmithContainer(int windowId, PlayerInventory playerInv, IWorldPosCallable worldPos) {
        super(Containers.KEYSMITH_TABLE, windowId);
        this.worldPos = worldPos;

        this.addSlot(new Slot(this.inputSlots, 0, 14, 24) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == Items.BLANK_KEY;
            }
        }.setBackground(Textures.ATLAS_BLOCKS_TEXTURE, Textures.SLOT_BLANK_KEY));
        this.addSlot(new Slot(this.inputSlots, 1, 31, 46) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == Items.KEY;
            }
        }.setBackground(Textures.ATLAS_BLOCKS_TEXTURE, Textures.SLOT_KEY));
        this.addSlot(new Slot(this.outputSlot, 0, 64, 24) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            @Override
            public ItemStack onTake(PlayerEntity player, ItemStack stack) {
                KeysmithContainer.this.inputSlots.decrStackSize(0, 1);
                if (KeysmithContainer.this.inputSlots.getStackInSlot(0).isEmpty()) {
                    KeysmithContainer.this.setOutputName(null);
                }
                return stack;
            }
        }.setBackground(Textures.ATLAS_BLOCKS_TEXTURE, Textures.SLOT_KEY));
        ContainerHelper.layoutPlayerInventorySlots(this::addSlot, playerInv, 8, 84);
    }

    public void onContentsChange() {
        ItemStack blank = this.inputSlots.getStackInSlot(0);
        ItemStack dupl = this.inputSlots.getStackInSlot(1);
        ItemStack out = blank.isEmpty() ? ItemStack.EMPTY : new ItemStack(Items.KEY, 1);
        if (blank.isEmpty()) {
            this.customName = null;
        } else {
            out.getCapability(CODE_HOLDER).ifPresent(outCode -> outCode.setCodes(
                    dupl.getCapability(CODE_HOLDER).filter(holder -> holder.getCodes().size() > 0).map(ICodeHolder::getCodes)
                            .orElseGet(() -> LongLists.singleton(RANDOM.nextLong()))));
            IColorable.copyColors(dupl, out);
            if (!StringUtils.isBlank(this.customName)) {
                out.setDisplayName(new StringTextComponent(this.customName));
            } else if (out.hasDisplayName()) { out.clearCustomName(); }
        }
        this.outputSlot.setInventorySlotContents(0, out);
        this.detectAndSendChanges();
    }

    public void setOutputName(String newName) {
        this.customName = newName;
        this.onContentsChange();
    }

    public String getOutputName() {
        return this.customName;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(worldPos, player, Blocks.KEYSMITH_TABLE);
    }

    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        this.worldPos.consume((world, pos) -> this.clearContainer(playerIn, world, this.inputSlots));
    }

    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            if (index == 2) {
                if (!this.mergeItemStack(slotStack, 3, 39, true)) { return ItemStack.EMPTY; }
            } else if (index != 0 && index != 1) {
                if (index < 39 && !this.mergeItemStack(slotStack, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(slotStack, 3, 39, false)) { return ItemStack.EMPTY; }

            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            slot.onTake(playerIn, slotStack);
        }
        return ItemStack.EMPTY;
    }
}

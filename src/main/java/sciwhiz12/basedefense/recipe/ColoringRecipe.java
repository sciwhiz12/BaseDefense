package sciwhiz12.basedefense.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import sciwhiz12.basedefense.init.ModRecipes;
import sciwhiz12.basedefense.item.IColorable;

public class ColoringRecipe extends SpecialRecipe {
    public ColoringRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        final int width = inv.getWidth();
        List<DyeColor> colors = new ArrayList<>(4);
        ItemStack colorItem = ItemStack.EMPTY;
        if (inv.isEmpty()) { return false; }
        for (int col = 0; col < inv.getHeight(); col++) {
            for (int row = 0; row < width; row++) {
                ItemStack stack = inv.getStackInSlot(row * width + col);
                if (stack.isEmpty()) { continue; }
                if (stack.getItem() instanceof IColorable) {
                    if (colorItem.isEmpty()) {
                        colorItem = stack;
                        continue;
                    } else {
                        return false;
                    }
                }
                DyeColor color = DyeColor.getColor(stack);
                if (color != null) { colors.add(color); }
                if (colors.size() > 3) { return false; }
            }
        }
        if (colors.size() <= 0) { return false; }
        if (colorItem.isEmpty()) { return false; }
        return true;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        final int width = inv.getWidth();
        List<DyeColor> colors = new ArrayList<>(4);
        ItemStack colorItem = ItemStack.EMPTY;
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < inv.getHeight(); row++) {
                ItemStack stack = inv.getStackInSlot(row * width + col);
                if (stack.isEmpty()) { continue; }
                if (stack.getItem() instanceof IColorable) {
                    if (colorItem.isEmpty()) {
                        colorItem = stack;
                        continue;
                    } else {
                        return ItemStack.EMPTY;
                    }
                }
                DyeColor color = DyeColor.getColor(stack);
                if (color != null) { colors.add(color); }
                if (colors.size() > 3) { return ItemStack.EMPTY; }
            }
        }
        if (colors.size() <= 0) { return ItemStack.EMPTY; }
        if (colorItem.isEmpty()) { return ItemStack.EMPTY; }
        ItemStack output = colorItem.copy();
        IColorable color = (IColorable) output.getItem();

        for (int idx = 0; idx < colors.size(); idx++) { color.setColor(output, idx, colors.get(idx)); }
        return output;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 4;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.COLORING;
    }
}
package net.beholderface.ephemera.items;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.common.items.ItemSlate;
import net.beholderface.ephemera.Ephemera;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ExtraConnectedSlateItem extends ItemSlate {
    public ExtraConnectedSlateItem(Block pBlock, Settings settings) {
        super(pBlock, settings);
    }

    @Override
    public Text getName(ItemStack pStack) {
        var key = "block." + Ephemera.MOD_ID + ".sneakyslate." + (hasPattern(pStack) ? "written" : "blank");
        return Text.translatable(key);
    }
}

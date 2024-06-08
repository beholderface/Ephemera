package net.beholderface.ephemera.items;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class ConjuredArmorMaterial implements ArmorMaterial {
    private static final int[] BASE_DURABILITY = new int[] {13, 15, 16, 11};
    //private static final int[] PROTECTION_VALUES = new int[] {A, B, C, D};
    @Override
    public int getDurability(EquipmentSlot slot) {
        return BASE_DURABILITY[slot.getEntitySlotId()] * 1000;
    }

    @Override
    public int getProtectionAmount(EquipmentSlot slot) {
        return 0;
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return null;
    }

    @Override
    public String getName() {
        return "media";
    }

    @Override
    public float getToughness() {
        return 0;
    }

    @Override
    public float getKnockbackResistance() {
        return 0;
    }
}

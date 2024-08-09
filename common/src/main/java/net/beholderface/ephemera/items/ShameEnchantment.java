package net.beholderface.ephemera.items;

import at.petrak.hexcasting.api.misc.HexDamageSources;
import at.petrak.hexcasting.ktxt.AccessorWrappers;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.beholderface.ephemera.registry.EphemeraItemRegistry;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ShameEnchantment extends Enchantment {
    private static final Map<LivingEntity, Long> cooldownMap = new HashMap<>();
    public ShameEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        Item[] accepted = {EphemeraItemRegistry.MEDIA_BOOTS.get(), EphemeraItemRegistry.MEDIA_LEGGINGS.get(),
                EphemeraItemRegistry.MEDIA_CHESTPLATE.get(), EphemeraItemRegistry.MEDIA_HELMET.get()};
        return Arrays.stream(accepted).toList().contains(stack.getItem());
    }

    @Override
    public boolean canAccept(Enchantment other){
        return false;
    }

    @Override
    public boolean isCursed() {
        return true;
    }
}

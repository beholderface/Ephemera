package net.beholderface.ephemera.items;

import at.petrak.hexcasting.common.lib.HexItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ConjuredArmorItem extends ArmorItem {
    public ConjuredArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
        super(material, slot, settings);
    }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected){
        //disappear if unequipped
        EquipmentSlot validSlot = null;
        boolean debugOverride = false;
        if (entity instanceof PlayerEntity player){
            //for test purposes only
            if (player.getOffHandStack().getItem().equals(HexItems.CREATIVE_UNLOCKER)){
                debugOverride = true;
            }
        }
        //yeah this really isn't intended for non-players
        int current = 0;
        EquipmentSlot[] slots = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};
        if (entity instanceof PlayerEntity player){
            for (ItemStack armorpiece : player.getArmorItems()){
                if (stack == armorpiece){
                    validSlot = slots[current];
                }
                current++;
            }
        }
        if (!debugOverride){
            if (validSlot == null || (stack.getDamage() < stack.getMaxDamage() - getAdjustedDurability(stack) && stack.isDamageable()) /*in case someone uses something that sets its damage to 0 or something*/
            ){
                //breakIgnoreCreative(stack, world, entity);
                if (entity instanceof PlayerEntity livingEntity){
                    if (!livingEntity.getAbilities().creativeMode){
                        EquipmentSlot finalValidSlot = validSlot;
                        stack.damage(Integer.MAX_VALUE, livingEntity, (theentity) -> {
                            theentity.sendEquipmentBreakStatus(finalValidSlot);
                        });
                    } else {
                        //Ephemera.LOGGER.info("calling break at first instance");
                        breakIgnoreCreative(stack, world, entity, false);
                    }
                } else {
                    //Ephemera.LOGGER.info("calling break at second instance");
                    breakIgnoreCreative(stack, world, entity, false);
                }
            } else if (world.getTime() % 20 == 0){
                PlayerEntity livingEntity = (PlayerEntity) entity;
                if (!livingEntity.getAbilities().creativeMode) {
                    EquipmentSlot finalValidSlot = validSlot;
                    stack.damage(1, livingEntity, (theentity) -> {
                        theentity.sendEquipmentBreakStatus(finalValidSlot);
                    });
                } else {
                    stack.setDamage(stack.getDamage() + 1);
                    if (stack.getDamage() >= stack.getMaxDamage() && !world.isClient){
                        //Ephemera.LOGGER.info("calling break at third instance " + world.isClient);
                        breakIgnoreCreative(stack, world, entity, true);
                    }
                }
            }
        }
    }

    //no shenanigans for you, whoever you are
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient){
        return false;
    }

    public static int getAdjustedDurability(@NotNull ItemStack stack){
        return stack.getOrCreateNbt().contains("DurabilityOverride") ? stack.getNbt().getInt("DurabilityOverride") : stack.getMaxDamage();
    }
    public static int getAdjustedDamage(@NotNull ItemStack stack){
        return stack.getOrCreateNbt().contains("DurabilityOverride") ? stack.getDamage() - Math.abs(stack.getNbt().getInt("DurabilityOverride") - stack.getMaxDamage()) : stack.getDamage();
    }
    public void breakIgnoreCreative(ItemStack stack, @NotNull World world, Entity entity, boolean sound){
        if (!world.isClient){
            stack.setCount(0);
            if (sound){
                world.playSoundFromEntity(null, entity, SoundEvents.ENTITY_ITEM_BREAK, entity.getSoundCategory(), 1f, 1f, 0);
            }
            /*ServerWorld serverWorld = (ServerWorld) world;
            MinecraftServer server = serverWorld.getServer();
            String command = "playsound entity.item.break player @a " + entity.getPos().x + " " + entity.getPos().y + " " + entity.getPos().z + " 1 1";
            server.getCommandManager().executeWithPrefix(server.getCommandSource().withSilent(), command);*/
        }
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        int adjustedDurability = getAdjustedDurability(stack);
        return Math.round(13.0F - getAdjustedDamage(stack) * 13.0F / (float)adjustedDurability);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        float stackMaxDamage = (float)getAdjustedDurability(stack);
        float f = Math.max(0.0F, (stackMaxDamage - (float)getAdjustedDamage(stack)) / stackMaxDamage);
        return MathHelper.hsvToRgb(0.75f, f, 1.0F);
    }
}

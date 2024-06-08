package net.beholderface.ephemera.items;

import at.petrak.hexcasting.common.lib.HexItems;
import net.beholderface.ephemera.Ephemera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.Arrays;

public class ConjuredArmorItem extends ArmorItem {
    public ConjuredArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
        super(material, slot, settings);
    }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected){
        //disappear if unequipped
        boolean validSlot = false;
        if (entity instanceof PlayerEntity player){
            //for test purposes only
            if (player.getOffHandStack().getItem().equals(HexItems.CREATIVE_UNLOCKER)){
                validSlot = true;
            }
        }
        if (entity instanceof PlayerEntity player){
            for (ItemStack armorpiece : player.getArmorItems()){
                if (stack == armorpiece){
                    validSlot = true;
                    break;
                }
            }
        }
        if (!validSlot){
            //Ephemera.LOGGER.info("Attempting to delete armor");
            stack.setCount(0);
            /*if (entity instanceof LivingEntity livingEntity){
                Ephemera.LOGGER.info("Attempting to delete armor");
                stack.setCount(0);
                //stack.damage(Integer.MAX_VALUE, livingEntity, (theEntity)-> {});//what does the lambda even do here
            } else {
                //idfk
                Ephemera.LOGGER.info("How did " + entity.getDisplayName() + ", a nonliving entity, manage to tick conjured armor?");
            }*/
        }/* else if (world.getTime() % 100 == 0){
            Ephemera.LOGGER.info("Armor passed break check. Armor slot IDs:" + Arrays.toString(armorSlotIDs) + ". Ticked slot ID: " + slot);
        }*/
    }

    //no shenanigans for you, whoever you are
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}

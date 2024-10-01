package net.beholderface.ephemera.items;

import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.lib.HexItems;
import kotlin.Pair;
import net.beholderface.ephemera.api.MiscAPIKt;
import net.beholderface.ephemera.registry.EphemeraMiscRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConjuredArmorItem extends ArmorItem {

    public static final String STORED_STATUS_TAG = "storedstatus";
    public static final String STORED_STATUS_LEVEL_TAG = "level";
    public static final String STORED_STATUS_TYPE_TAG = "type";
    public static final Map<Enchantment, Integer> SHAME_MAP = new HashMap<>();

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
        int damageIncrement = 1;
        //apply stored effect if present
        if (world.getTime() % 20 == 0 && validSlot != null && !world.isClient){
            Pair<StatusEffect, Integer> storedStatus = getStoredStatus(stack);
            if (storedStatus != null) {
                PlayerEntity player = (PlayerEntity) entity;
                damageIncrement += (int) /*Math.pow(*/storedStatus.component2() + 1/*, 2)*/;
                if (world.getTime() % 100 == 0 && damageIncrement <= stack.getMaxDamage() - stack.getDamage()){
                    StatusEffect stored = storedStatus.getFirst();
                    int level = storedStatus.getSecond();
                    boolean preexistingcondition = player.hasStatusEffect(stored);
                    int prelevel = preexistingcondition ? player.getStatusEffect(stored).getAmplifier() : -1;
                    if (preexistingcondition && prelevel < level){
                        player.removeStatusEffect(stored);
                    }
                    player.addStatusEffect(new StatusEffectInstance(stored, 200,
                            level, false, false, true));
                }
                /*if (!player.hasStatusEffect(storedStatus.getFirst())) {
                    //Ephemera.LOGGER.info("Attempting to add status effect " + storedStatus.getFirst());
                }*/
            }
        }
        if (!debugOverride){
            if (validSlot == null || (stack.getDamage() < stack.getMaxDamage() - getAdjustedDurability(stack) && stack.isDamageable()) /*in case someone uses something that sets its damage to 0 or something*/
            ){
                knockoffBreak(stack, world, entity, entity instanceof PlayerEntity);
            } else if (world.getTime() % 20 == 0){
                if (stack.hasEnchantments()){
                    Map<Enchantment, Integer> enchantMap = EnchantmentHelper.get(stack);
                    if (enchantMap.size() != 1 || !enchantMap.containsKey(EphemeraMiscRegistry.SHAME_CURSE.get())){
                        EnchantmentHelper.set(SHAME_MAP, stack);
                    }
                }
                PlayerEntity livingEntity = (PlayerEntity) entity;
                if (!livingEntity.getAbilities().creativeMode) {
                    EquipmentSlot finalValidSlot = validSlot;
                    stack.damage(damageIncrement, livingEntity, (theentity) -> {
                        theentity.sendEquipmentBreakStatus(finalValidSlot);
                    });
                } else {
                    stack.setDamage(stack.getDamage() + damageIncrement);
                    if (stack.getDamage() >= stack.getMaxDamage() && !world.isClient){
                        //Ephemera.LOGGER.info("calling break at third instance " + world.isClient);
                        knockoffBreak(stack, world, entity, true);
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
    public boolean hasGlint(ItemStack stack){
        return !EnchantmentHelper.fromNbt(stack.getEnchantments()).containsKey(EphemeraMiscRegistry.SHAME_CURSE.get());
    }

    @Override
    public Rarity getRarity(ItemStack stack){
        if (getStoredStatus(stack) != null){
            return Rarity.RARE;
        } else {
            return Rarity.UNCOMMON;
        }
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
    public void knockoffBreak(ItemStack stack, @NotNull World world, Entity entity, boolean sound){
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
    public static @Nullable Pair<StatusEffect, Integer> getStoredStatus(ItemStack stack){
        NbtCompound topLevelNbt = stack.getOrCreateNbt();
        if (topLevelNbt.contains(STORED_STATUS_TAG)){
            NbtCompound storedStatus = topLevelNbt.getCompound(STORED_STATUS_TAG);
            String statusString = storedStatus.getString(STORED_STATUS_TYPE_TAG);
            //Ephemera.LOGGER.info("found status string " + statusString);
            Identifier statusID = Identifier.tryParse(statusString);
            if (statusID != null){
                //Ephemera.LOGGER.info("searching registry for identifier " + statusID);
                StatusEffect effect = Registry.STATUS_EFFECT.get(statusID);
                //Ephemera.LOGGER.info("found effect: " + effect);
                if (effect != null){
                    return new Pair<>(effect, storedStatus.getInt(STORED_STATUS_LEVEL_TAG));
                }
            }
        }
        return null;
    }
    public static boolean setStoredStatus(ItemStack stack, StatusEffect effect, int strength){
        NbtCompound topLevelNbt = stack.getOrCreateNbt();
        NbtCompound statusData = new NbtCompound();
        if (effect != null){
            Identifier statusID = MiscAPIKt.effectToIdentifier(effect);
            if (statusID != null){
                statusData.putString(STORED_STATUS_TYPE_TAG, statusID.toString());
                statusData.putInt(STORED_STATUS_LEVEL_TAG, strength);
                NBTHelper.putCompound(topLevelNbt, STORED_STATUS_TAG, statusData);
                return true;
            }
        } else {
            if (topLevelNbt.contains(STORED_STATUS_TAG)){
                topLevelNbt.remove(STORED_STATUS_TAG);
                return true;
            }
        }
        return false;
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
    @Override
    public void appendTooltip(ItemStack stack, @javax.annotation.Nullable World world, List<Text> tooltip, TooltipContext context) {
        Pair<StatusEffect, Integer> storedStatus = getStoredStatus(stack);
        if (storedStatus != null){
            Text text = Text.translatable("item.ephemeral.media_armor.tooltip.1",
                    Text.translatable(storedStatus.getFirst().getTranslationKey()), storedStatus.component2() + 1);
            Text text2 = Text.of(text.getString());
            Style style = text2.getStyle().withColor(48831);
            tooltip.add(text2.getWithStyle(style).get(0));
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        this.knockoffBreak(itemStack, world, user, true);
        return TypedActionResult.success(itemStack, world.isClient());
    }
}

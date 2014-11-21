/*
 * Copyright (c) CovertJaguar, 2011 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.emblems;

import com.google.common.io.BaseEncoding;
import java.security.MessageDigest;
import java.util.*;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.plugins.forge.NBTPlugin.NBTList;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Level;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EmblemManager implements IEmblemManager {

    public static final EmblemManager instance;
    public static final String EMBLEM_NBT_DATA = "emblemData";
    public static final String EMBLEM_UNLOCK_NBT_DATA = "unlocks";
    private static final BaseEncoding encoder = BaseEncoding.base32().omitPadding().lowerCase();
    private static final Set<String> starterEmblems;

    static {
        instance = new EmblemManager();
        EmblemToolsServer.manager = instance;

        Set<String> starters = new LinkedHashSet<String>();
//        starters.add(getIdentifierFromCode("Crazy Star"));
        starters.add(getIdentifierFromCodeLegacy("Railcraft"));
        starters.add(getIdentifierFromCodeLegacy("Book"));
        starters.add(getIdentifierFromCodeLegacy("Stone Age Miner"));
        starters.add(getIdentifierFromCodeLegacy("Toy Sword"));
        starters.add(getIdentifierFromCodeLegacy("Bone Dead"));
        starters.add(getIdentifierFromCodeLegacy("All Aboard"));
        starters.add(getIdentifierFromCodeLegacy("Record Breaker"));
        starters.add(getIdentifierFromCodeLegacy("Sleeper"));
        starters.add(getIdentifierFromCodeLegacy("It's a lie!"));
        starters.add(getIdentifierFromCodeLegacy("Beauty"));
        starters.add(getIdentifierFromCodeLegacy("Power Up"));
        starters.add(getIdentifierFromCodeLegacy("ssssSS!"));
        starters.add(getIdentifierFromCodeLegacy("Sticky Situation"));
        starters.add(getIdentifierFromCode("my name is jack"));
        starters.add(getIdentifierFromCode("tells no tales"));
        starters.add(getIdentifierFromCode("are you ready for z-day?"));
        starters.add(getIdentifierFromCode("no acorns here"));
        starterEmblems = Collections.unmodifiableSet(starters);
    }

    public void init() {
    }

    public static boolean playerHasEmblem(EntityPlayer player, String emblem) {
        Set<String> emblems = getUnlockedEmblems(player);
        if (emblems.contains(emblem))
            return true;
        return false;
    }

    private static NBTTagCompound getEmblemData(EntityPlayer player) {
        NBTTagCompound forgeData = player.getEntityData();
        if (!forgeData.hasKey(EntityPlayer.PERSISTED_NBT_TAG))
            forgeData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        NBTTagCompound persistantData = forgeData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

        if (!persistantData.hasKey(RailcraftConstants.RAILCRAFT_PLAYER_NBT_TAG))
            persistantData.setTag(RailcraftConstants.RAILCRAFT_PLAYER_NBT_TAG, new NBTTagCompound());
        NBTTagCompound railcraftData = persistantData.getCompoundTag(RailcraftConstants.RAILCRAFT_PLAYER_NBT_TAG);

        if (!railcraftData.hasKey(EMBLEM_NBT_DATA))
            railcraftData.setTag(EMBLEM_NBT_DATA, new NBTTagCompound());
        return railcraftData.getCompoundTag(EMBLEM_NBT_DATA);
    }

    private static NBTList<NBTTagByteArray> getEmblemUnlockData(EntityPlayer player) {
        NBTTagCompound emblemData = getEmblemData(player);
        if (!emblemData.hasKey(EMBLEM_UNLOCK_NBT_DATA))
            emblemData.setTag(EMBLEM_UNLOCK_NBT_DATA, new NBTTagList());
        return NBTPlugin.getNBTList(emblemData, EMBLEM_UNLOCK_NBT_DATA, NBTPlugin.EnumNBTType.BYTE_ARRAY);
    }

    public static Set<String> getUnlockedEmblems(EntityPlayer player) {
        Set<String> set = new LinkedHashSet<String>();
        set.addAll(starterEmblems);
        NBTList<NBTTagByteArray> unlocks = getEmblemUnlockData(player);
        for (NBTTagByteArray tag : unlocks) {
            set.add(unscrambleIdentifier(tag.func_150292_c()));
        }
        return set;
    }

    static void unlockEmblem(EntityPlayerMP player, String emblemCode) {
        String identifier = getIdentifierFromCode(emblemCode);
        if (getUnlockedEmblems(player).contains(identifier)) {
            Game.log(Level.WARN, "Tried to unlock already unlocked Emblem, aborting - \"emblem-{0}\"", identifier);
            return;
        }
        NBTList<NBTTagByteArray> unlocks = getEmblemUnlockData(player);
        unlocks.add(new NBTTagByteArray(scrambleIdentifier(identifier)));
        Game.log(Level.WARN, "Emblem unlocked - \"emblem-{0}\"", identifier);
    }

    @Override
    public void unlockEmblem(EntityPlayerMP player, String emblemCode, int windowId) {
        String identifier = getIdentifierFromCode(emblemCode);
        if (playerHasEmblem(player, emblemCode)) {
            updateUnlockGUI(player, identifier, windowId, "railcraft.gui.engrave.unlock.exists");
            return;
        }
        EmblemUnlocker.spawnUnlocker(emblemCode, windowId, player);
    }

    public static void updateUnlockGUI(EntityPlayerMP player, String identifier, int windowId, String msg) {
        PacketBuilder.instance().sendGuiStringPacket(player, windowId, 0, identifier);
        PacketBuilder.instance().sendGuiStringPacket(player, windowId, 1, msg);
    }

    @Override
    public ItemStack getEmblemItemStack(String ident) {
        return ItemEmblem.getEmblem(ident);
    }

    public static String getIdentifierFromCode(String unlockCode) {
        try {
            MessageDigest msgDigest = MessageDigest.getInstance("SHA-1");
            msgDigest.update(unlockCode.toLowerCase(Locale.ENGLISH).getBytes("UTF-8"));
            byte rawByte[] = msgDigest.digest();
            return encoder.encode(rawByte);
        } catch (Exception ex) {
            return "";
        }
    }

    private static String getIdentifierFromCodeLegacy(String unlockCode) {
        try {
            MessageDigest msgDigest = MessageDigest.getInstance("SHA-1");
            msgDigest.update(unlockCode.getBytes("UTF-8"));
            byte rawByte[] = msgDigest.digest();
            return encoder.encode(rawByte);
        } catch (Exception ex) {
            return "";
        }
    }

    public static byte[] scrambleIdentifier(String identifier) {
        byte[] bytes = encoder.decode(identifier);
        bytes = Arrays.copyOf(bytes, bytes.length);
        ArrayUtils.reverse(bytes);
        return bytes;
    }

    public static String unscrambleIdentifier(byte[] bytes) {
        bytes = Arrays.copyOf(bytes, bytes.length);
        ArrayUtils.reverse(bytes);
        return encoder.encode(bytes);
    }

}

/*
 * Copyright (c) CovertJaguar, 2011 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.emblems;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.logging.log4j.Level;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class EmblemUnlocker {

    protected boolean isComplete = false;
    protected boolean emblemExists = false;
    protected final String unlockCode;
    protected final String identifier;
    protected final EntityPlayerMP player;

    private EmblemUnlocker(String unlockCode, EntityPlayerMP player) {
        this.unlockCode = unlockCode;
        this.player = player;
        this.identifier = EmblemManager.getIdentifierFromCode(unlockCode);
    }

    private static class EmblemUnlockerGUI extends EmblemUnlocker {

        private final int windowId;

        private EmblemUnlockerGUI(String unlockCode, EntityPlayerMP player, int windowId) {
            super(unlockCode, player);
            this.windowId = windowId;
        }

        @Override
        protected void onComplete() {
            String result = "";
            String msg = "railcraft.gui.engrave.unlock.fail";
            if (emblemExists) {
                EmblemManager.unlockEmblem(player, unlockCode);
                result = identifier;
                msg = "railcraft.gui.engrave.unlock.success";
            }
            EmblemManager.updateUnlockGUI(player, result, windowId, msg);
        }

    }

    public static void spawnUnlocker(String unlockCode, int windowId, EntityPlayerMP player) {
        EmblemUnlocker unlocker = new EmblemUnlockerGUI(unlockCode, player, windowId);
        unlocker.initiateCheck();
        FMLCommonHandler.instance().bus().register(unlocker);
    }

    private void initiateCheck() {
        Game.log(Level.INFO, "Attempting to unlock Emblem - \"emblem-{0}\"", identifier);
        EmblemChecker checker = new EmblemChecker();
        checker.setDaemon(true);
        checker.setName("Emblem checker (" + identifier + ")");
        checker.start();
    }

    protected void onComplete() {
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.side == Side.CLIENT)
            return;
        if (isComplete) {
            onComplete();
            FMLCommonHandler.instance().bus().unregister(this);
        }
    }

    private class EmblemChecker extends Thread {

        @Override
        public void run() {
            emblemExists = emblemExists();
            isComplete = true;
        }

        private boolean emblemExists() {
            HttpURLConnection con = null;
            try {
                HttpURLConnection.setFollowRedirects(false);
                URL url = new URL(RailcraftConstants.EMBLEM_URL + "emblem-" + identifier + ".jar");
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("HEAD");
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Game.log(Level.INFO, "Found Emblem: \"emblem-{0}\"", identifier);
                    return true;
                }
                return false;
            } catch (Exception ex) {
                Game.log(Level.WARN, "Failed to find Emblem: \"emblem-{0}\". Reason: {1}", identifier, ex);
                return false;
            } finally {
                if (con != null) con.disconnect();
            }
        }

    }
}

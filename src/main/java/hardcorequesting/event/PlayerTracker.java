package hardcorequesting.event;

import hardcorequesting.config.ModConfig;
import hardcorequesting.death.DeathStats;
import hardcorequesting.quests.QuestLine;
import hardcorequesting.quests.QuestingData;
import hardcorequesting.util.Translator;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class PlayerTracker {

    public PlayerTracker() {
        MinecraftForge.EVENT_BUS.register(this);
    }


    public int getRemainingLives(ICommandSender sender) {
        return QuestingData.getQuestingData((EntityPlayer) sender).getLives();
    }

    public static final String HQ_TAG = "HardcoreQuesting";
    public static final String RECEIVED_BOOK = "questBook";

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        if (!QuestingData.hasData(player)) {
            DeathStats.resync();
        }

        QuestLine.sendServerSync(player);

        if (QuestingData.isHardcoreActive())
            sendLoginMessage(player);
        else if (ModConfig.NO_HARDCORE_MESSAGE)
            player.addChatMessage(new TextComponentTranslation("hqm.message.noHardcore"));

        NBTTagCompound tags = player.getEntityData();
        if (tags.hasKey(HQ_TAG)) {
            if (tags.getCompoundTag(HQ_TAG).getBoolean(RECEIVED_BOOK)) {
                QuestingData.getQuestingData(player).receivedBook = true;
            }
            if (!QuestingData.isQuestActive()) {
                tags.removeTag(HQ_TAG);
            }
        }
        QuestingData.spawnBook(player);
    }


    private void sendLoginMessage(EntityPlayer player) {
        player.addChatMessage(new TextComponentString(
                Translator.translate("hqm.message.hardcore") + " "
                        + Translator.translate(getRemainingLives(player) != 1, "hqm.message.livesLeft", getRemainingLives(player))
        ));

    }
}

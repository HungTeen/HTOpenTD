package hungteen.opentd.common.capability.player;

import hungteen.htlib.api.interfaces.IPlayerDataManager;
import hungteen.opentd.common.network.CDPacket;
import hungteen.opentd.common.network.NetworkHandler;
import hungteen.opentd.common.impl.OTDSummonEntries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-08 12:46
 **/
public class PlayerDataManager implements IPlayerDataManager {

    private final Player player;
    private final Map<ResourceLocation, Integer> summonItemCD = new HashMap<>(); // Length.
    private final Map<ResourceLocation, Long> summonItemPT = new HashMap<>(); // End Point.

    public PlayerDataManager(Player player){
        this.player = player;
    }

    @Override
    public void tick(){
    }

    @Override
    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        {
            final CompoundTag nbt = new CompoundTag();
            OTDSummonEntries.registry().getKeys(getLevel()).forEach(key -> {
                final CompoundTag tmp = new CompoundTag();
                final ResourceLocation id = key.location();
                tmp.putInt(id + "_CD", this.getSummonItemCD(id));
                tmp.putLong(id + "PT", this.getSummonItemPT(id));
                nbt.put(id.toString(), tmp);
            });
            tag.put("CoolDowns", nbt);
        }

        return tag;
    }

    @Override
    public void loadFromNBT(CompoundTag tag) {
        if(tag.contains("CoolDowns")){
            final CompoundTag nbt = tag.getCompound("CoolDowns");
            OTDSummonEntries.registry().getKeys(getLevel()).forEach(key -> {
                final ResourceLocation id = key.location();
                if(nbt.contains(id.toString())){
                    final CompoundTag tmp = nbt.getCompound(id.toString());
                    this.summonItemCD.put(id, tmp.getInt(id + "_CD"));
                    this.summonItemPT.put(id, tmp.getLong(id + "_PT"));
                }
            });
        }
    }

    /**
     * copy player data when clone event happen.
     */
    @Override
    public void cloneFromExistingPlayerData(IPlayerDataManager data, boolean died) {
        this.loadFromNBT(data.saveToNBT());
    }

    @Override
    public void syncToClient() {
        OTDSummonEntries.registry().getKeys(getLevel()).stream().map(ResourceKey::location).forEach(this::sendCDPacket);
    }

    public void setSummonItemCD(ResourceLocation id, int tick) {
        this.summonItemCD.put(id, tick);
        this.sendCDPacket(id);
    }

    public int getSummonItemCD(ResourceLocation id) {
        return this.summonItemCD.getOrDefault(id, 0);
    }

    public void setSummonItemPT(ResourceLocation id, long point) {
        this.summonItemPT.put(id, point);
        this.sendCDPacket(id);
    }

    public long getSummonItemPT(ResourceLocation id) {
        return this.summonItemPT.getOrDefault(id, 0L);
    }

    public boolean isOnCooldown(ResourceLocation id){
        return this.getSummonItemCD(id) > 0 && this.getSummonItemPT(id) > getCurrentTick();
    }

    public double getCDPercent(ResourceLocation id){
        return this.isOnCooldown(id) ? (this.getSummonItemPT(id) - getCurrentTick()) * 1D / this.getSummonItemCD(id) : 0;
    }

    public void saveCurrentCD(ResourceLocation id, int cd) {
        this.setSummonItemCD(id, cd);
        this.setSummonItemPT(id, this.getCurrentTick() + cd);
    }

    public void sendCDPacket(ResourceLocation id) {
        if (getPlayer() instanceof ServerPlayer) {
            NetworkHandler.sendToClient((ServerPlayer) getPlayer(), new CDPacket(id, this.getSummonItemCD(id), this.getSummonItemPT(id)));
        }
    }

    public long getCurrentTick(){
        return this.getLevel().getGameTime();
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public Level getLevel(){
        return getPlayer().level();
    }

}

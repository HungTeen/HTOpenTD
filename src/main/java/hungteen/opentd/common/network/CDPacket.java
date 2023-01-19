package hungteen.opentd.common.network;

import hungteen.htlib.HTLib;
import hungteen.opentd.util.PlayerUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @program: Immortal
 * @author: HungTeen
 * @create: 2022-10-13 21:34
 **/
public class CDPacket {

    private final String id;
    private final int cd;
    private final long pt;

    public CDPacket(ResourceLocation id, int cd, long pt) {
        this.id = id.toString();
        this.cd = cd;
        this.pt = pt;
    }

    public CDPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readUtf();
        this.cd = buffer.readInt();
        this.pt = buffer.readLong();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.id);
        buffer.writeInt(this.cd);
        buffer.writeLong(this.pt);
    }

    public static class Handler {

        /**
         * Only Server sync to Client.
         */
        public static void onMessage(CDPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(()->{
                final ResourceLocation location = new ResourceLocation(message.id);
                PlayerUtil.getOptManager(HTLib.PROXY.getPlayer()).ifPresent(l -> {
                    l.setSummonItemCD(location, message.cd);
                    l.setSummonItemPT(location, message.pt);
                });
            });
            ctx.get().setPacketHandled(true);
        }
    }

}

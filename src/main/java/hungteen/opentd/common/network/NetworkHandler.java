package hungteen.opentd.common.network;

import hungteen.opentd.util.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-08 15:25
 **/
public class NetworkHandler {

    private static int id = 0;

    private static SimpleChannel CHANNEL;

    public static void init() {
        CHANNEL = NetworkRegistry.ChannelBuilder
                .named(Util.prefix("networking"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        CHANNEL.registerMessage(getId(), CDPacket.class, CDPacket::encode, CDPacket::new, CDPacket.Handler::onMessage);
    }

    public static <MSG> void sendToServer(MSG msg){
        CHANNEL.sendToServer(msg);
    }

    public static <MSG> void sendToClient(ServerPlayer serverPlayer, MSG msg){
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), msg);
    }

    public static <MSG> void sendToNearByClient(Level world, Vec3 vec, double dis, MSG msg){
        CHANNEL.send(PacketDistributor.NEAR.with(() -> {
            return new PacketDistributor.TargetPoint(vec.x, vec.y, vec.z, dis, world.dimension());
        }), msg);
    }

    private static int getId(){
        return id ++;
    }
}

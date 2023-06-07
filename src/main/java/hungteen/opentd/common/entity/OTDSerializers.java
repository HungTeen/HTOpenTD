package hungteen.opentd.common.entity;

import hungteen.opentd.OpenTD;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/19 15:32
 */
public class OTDSerializers {

    private static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, OpenTD.MOD_ID);

    public static final RegistryObject<EntityDataSerializer<IEntityForKJS.ClientEntityResource>> CLIENT_ENTITY_RES = DATA_SERIALIZERS.register("client_entity_res", () -> new EntityDataSerializer.ForValueType<>() {
        @Override
        public void write(FriendlyByteBuf byteBuf, IEntityForKJS.ClientEntityResource resource) {
            byteBuf.writeNbt(resource.saveTo(new CompoundTag()));
        }

        @Override
        public IEntityForKJS.ClientEntityResource read(FriendlyByteBuf byteBuf) {
            final IEntityForKJS.ClientEntityResource resource = new IEntityForKJS.ClientEntityResource();
            resource.readFrom(Objects.requireNonNull(byteBuf.readNbt()));
            return resource;
        }
    });

    public static void register(IEventBus event){
        DATA_SERIALIZERS.register(event);
    }

}

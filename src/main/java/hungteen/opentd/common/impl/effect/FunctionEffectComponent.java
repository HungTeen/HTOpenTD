package hungteen.opentd.common.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-31 16:06
 **/
public record FunctionEffectComponent(boolean self, ResourceLocation function) implements IEffectComponent {

    public static final Codec<FunctionEffectComponent> CODEC = RecordCodecBuilder.<FunctionEffectComponent>mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("self", false).forGetter(FunctionEffectComponent::self),
            ResourceLocation.CODEC.fieldOf("function").forGetter(FunctionEffectComponent::function)
    ).apply(instance, FunctionEffectComponent::new)).codec();

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, Entity entity) {
        effect(serverLevel, self() ? owner : entity);
    }

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, BlockPos pos) {
        if(self()){
            effect(serverLevel, owner);
        }
    }

    private void effect(ServerLevel serverLevel, Entity entity){
        final ServerFunctionManager manager = serverLevel.getServer().getFunctions();
        manager.get(this.function()).ifPresent((func) -> {
            manager.execute(func, createCommandSourceStack(entity).withSuppressedOutput().withPermission(2));
        });
    }

    public CommandSourceStack createCommandSourceStack(Entity entity) {
        return new CommandSourceStack(entity, entity.position(), entity.getRotationVector(), (ServerLevel)entity.level(), 2, entity.getName().getString(), entity.getDisplayName(), Objects.requireNonNull(entity.level().getServer()), entity);
    }

    @Override
    public IEffectComponentType<?> getType() {
        return OTDEffectComponentTypes.FUNCTION_EFFECT;
    }
}

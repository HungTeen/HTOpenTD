package hungteen.opentd.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.UUID;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-18 12:00
 **/
public record NBTEffectComponent(CompoundTag nbt, boolean self) implements IEffectComponent {

    public static final Codec<NBTEffectComponent> CODEC = RecordCodecBuilder.<NBTEffectComponent>mapCodec(instance -> instance.group(
            CompoundTag.CODEC.fieldOf("nbt").forGetter(NBTEffectComponent::nbt),
            Codec.BOOL.optionalFieldOf("self", false).forGetter(NBTEffectComponent::self)
    ).apply(instance, NBTEffectComponent::new)).codec();

    @Override
    public void effectTo(Entity owner, Entity entity) {
        effect(self() ? owner : entity);
    }

    @Override
    public void effectTo(Entity owner, BlockPos pos) {
        if (self()){
            effect(owner);
        }
    }

    private void effect(Entity entity){
        final CompoundTag compoundTag = entity.saveWithoutId(new CompoundTag());
        compoundTag.merge(nbt());
        entity.load(compoundTag);
    }

    @Override
    public IEffectComponentType<?> getType() {
        return HTEffectComponents.NBT_EFFECT;
    }
}

package hungteen.opentd.impl.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ISummonRequirementType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-07 20:14
 **/
public record BlockRequirement(Optional<BlockState> blockState, Optional<TagKey<Block>> blockTag, Optional<List<Block>> blocks) implements ISummonRequirement {

    public static final Codec<BlockRequirement> CODEC = RecordCodecBuilder.<BlockRequirement>mapCodec(instance -> instance.group(
            Codec.optionalField("state", BlockState.CODEC).forGetter(BlockRequirement::blockState),
            Codec.optionalField("tag", TagKey.codec(Registry.BLOCK_REGISTRY)).forGetter(BlockRequirement::blockTag),
            Codec.optionalField("blocks", ForgeRegistries.BLOCKS.getCodec().listOf()).forGetter(BlockRequirement::blocks)
    ).apply(instance, BlockRequirement::new)).codec();

    @Override
    public boolean allowOn(Level level, Player player, Entity entity) {
        return false;
    }

    @Override
    public boolean allowOn(Level level, Player player, BlockState state, BlockPos pos) {
        if(blockState().isPresent() && blockState().get() != state){
            return false;
        }
        if(blockTag().isPresent() && ! state.is(blockTag().get())){
            return false;
        }
        if(blocks().isPresent() && ! blocks().get().contains(state.getBlock())){
            return false;
        }
        return true;
    }

    @Override
    public void consume(Level level, Player player) {

    }

    @Override
    public ISummonRequirementType<?> getType() {
        return HTSummonRequirements.BLOCK_REQUIREMENT;
    }
}

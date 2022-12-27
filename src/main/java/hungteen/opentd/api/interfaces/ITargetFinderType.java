package hungteen.opentd.api.interfaces;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.ISimpleEntry;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:26
 **/
public interface ITargetFinderType<P extends ITargetFinder> extends ISimpleEntry {

    /**
     * Get the method to codec target finder.
     * @return Codec method.
     */
    Codec<P> codec();
}

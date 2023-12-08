package hungteen.opentd.common.entity;

import software.bernie.geckolib.core.animation.RawAnimation;

/**
 * @program: HTOpenTD
 * @author: PangTeen
 * @create: 2023/12/8 23:08
 **/
public class OTDAnimations {

    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    public static final RawAnimation REST = RawAnimation.begin().thenLoop("rest");
    public static final RawAnimation MOVE = RawAnimation.begin().thenLoop("move");

    public static final RawAnimation SHOOT = RawAnimation.begin().thenPlay("shoot");
    public static final RawAnimation GEN = RawAnimation.begin().thenPlay("gen");
    public static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    public static final RawAnimation INSTANT = RawAnimation.begin().thenPlay("instant");

    public static final RawAnimation DEAD = RawAnimation.begin().thenPlayAndHold("dead");

}

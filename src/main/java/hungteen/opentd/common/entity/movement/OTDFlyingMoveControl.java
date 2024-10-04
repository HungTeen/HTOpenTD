package hungteen.opentd.common.entity.movement;

import hungteen.htlib.util.helper.RandomHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

/**
 * @program: HTOpenTD
 * @author: PangTeen
 * @create: 2024/10/4 15:06
 * Modify {@link net.minecraft.world.entity.ai.control.FlyingMoveControl} for OTD flying entities.
 **/
public class OTDFlyingMoveControl extends MoveControl {

    private final int maxTurn;
    private final boolean hoversInPlace;
    private long nextChangeTick = 0;
    private float variantSpeed = 0;

    public OTDFlyingMoveControl(Mob mob, int maxTurn, boolean hoversInPlace) {
        super(mob);
        this.maxTurn = maxTurn;
        this.hoversInPlace = hoversInPlace;
    }

    @Override
    public void tick() {
        if (this.operation == MoveControl.Operation.MOVE_TO) {
            this.operation = MoveControl.Operation.WAIT;
            this.mob.setNoGravity(true);
            double d0 = this.wantedX - this.mob.getX();
            double d1 = this.wantedY - this.mob.getY();
            double d2 = this.wantedZ - this.mob.getZ();
            double dis = d0 * d0 + d1 * d1 + d2 * d2;
            if (dis < (double) 2.5000003E-7F) {
                this.mob.setYya(0.0F);
                this.mob.setZza(0.0F);
                return;
            }

            float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, 90.0F));
            float speed = this.getSpeed();
            this.mob.setSpeed(speed);
            double d4 = Math.sqrt(d0 * d0 + d2 * d2);
            if (Math.abs(d1) > (double) 1.0E-5F || Math.abs(d4) > (double) 1.0E-5F) {
                float f2 = (float) (-(Mth.atan2(d1, d4) * (double) (180F / (float) Math.PI)));
                this.mob.setXRot(this.rotlerp(this.mob.getXRot(), f2, (float) this.maxTurn));
                this.mob.setYya(d1 > 0.0D ? speed : -speed);
            }
        }  else if (this.operation == Operation.STRAFE && this.mob.getTarget() != null) {
            this.mob.setNoGravity(true);
            final float speed = this.getSpeed();
            if(this.mob.level.getGameTime() >= this.nextChangeTick){
                this.nextChangeTick = this.mob.level.getGameTime() + this.mob.getRandom().nextIntBetweenInclusive(5, 20);
                this.variantSpeed = (float) RandomHelper.getMinMax(this.mob.getRandom(), 2F, 3F) * RandomHelper.getSide(this.mob.getRandom());
            }
            final float dz = this.strafeForwards;
            final float dx = this.strafeRight + this.variantSpeed;
            final float dy = (this.mob.getTarget().getEyeY() + 5 - this.mob.getY() > 0) ? 1F : -1F;

            this.mob.setSpeed(speed);
            this.mob.setZza(this.strafeForwards);
            this.mob.setXxa(dx);
            this.mob.setYya(dy);

            this.operation = MoveControl.Operation.WAIT;
        } else {
            if (!this.hoversInPlace) {
                this.mob.setNoGravity(false);
            }

            this.mob.setYya(0.0F);
            this.mob.setZza(0.0F);
        }
    }

    private float getSpeed(){
        return (float) (this.speedModifier * (this.mob.isOnGround() ? this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED) : this.mob.getAttributeValue(Attributes.FLYING_SPEED)));
    }

}


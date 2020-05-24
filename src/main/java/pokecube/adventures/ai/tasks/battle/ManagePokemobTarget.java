package pokecube.adventures.ai.tasks.battle;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import pokecube.core.ai.brain.BrainUtils;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.interfaces.pokemob.ai.CombatStates;
import pokecube.core.utils.PokemobTracker;

public class ManagePokemobTarget extends BaseBattleTask
{

    public ManagePokemobTarget(final LivingEntity trainer)
    {
        super(trainer);
    }

    @Override
    protected void updateTask(final ServerWorld worldIn, final LivingEntity owner, final long gameTime)
    {
        final IPokemob mob = this.trainer.getOutMob();
        if (mob == null || this.target == null) return;
        final LivingEntity mobTarget = BrainUtils.getAttackTarget(mob.getEntity());
        LivingEntity newTarget = this.target;
        final IPokemob target = CapabilityPokemob.getPokemobFor(mobTarget);
        // Try to send our mob after the target's nearest mob instead.
        if (target == null)
        {
            final List<Entity> alternates = PokemobTracker.getMobs(this.target, e -> e.getDistanceSq(this.entity) < 64
                    && CapabilityPokemob.getPokemobFor(e) != null);
            if (!alternates.isEmpty()) newTarget = (LivingEntity) alternates.get(0);
        }
        if (!mob.getCombatState(CombatStates.ANGRY)) mob.setCombatState(CombatStates.ANGRY, true);
        // check if pokemob's target is same as trainers.
        if (mobTarget != newTarget && newTarget != null) BrainUtils.initiateCombat(mob.getEntity(), newTarget);
    }

    @Override
    protected boolean shouldContinueExecuting(final ServerWorld worldIn, final LivingEntity entityIn,
            final long gameTimeIn)
    {
        return super.shouldExecute(worldIn, entityIn);
    }

    @Override
    protected boolean shouldExecute(final ServerWorld worldIn, final LivingEntity owner)
    {
        if (!super.shouldExecute(worldIn, owner)) return false;
        return this.trainer.getOutMob() != null;
    }
}
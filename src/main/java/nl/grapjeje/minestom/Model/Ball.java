package nl.grapjeje.minestom.Model;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.entity.Player;
import nl.grapjeje.minestom.Server;

public class Ball {
    public static Ball instance;

    private Entity blockDisplayEntity;

    public void spawn(Pos pos) {
        Instance instance = Server.container;

        blockDisplayEntity = new Entity(EntityType.FALLING_BLOCK);
        blockDisplayEntity.setInstance(instance, pos);

        BlockDisplayMeta meta = (BlockDisplayMeta) blockDisplayEntity.getEntityMeta();
        meta.setBlockState(Block.STONE);
        blockDisplayEntity.setBoundingBox(new BoundingBox(100, 100, 100));
        blockDisplayEntity.setCustomNameVisible(true);
    }

    public void kick(Player player, float power) {
        if (power > 10) power = 10;
        if (power < 0) power = 0;

        Vec direction = player.getPosition().direction();
        direction = new Vec(direction.x(), 0, direction.z()).normalize();

        float horizontalForce = power * 1.2f;
        float verticalForce = power * 0.8f;

        Vec force = new Vec(direction.x() * horizontalForce, verticalForce, direction.z() * horizontalForce);

        blockDisplayEntity.setVelocity(force);
    }
}
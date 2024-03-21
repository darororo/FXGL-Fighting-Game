package com.example.fp;

import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.dsl.views.ScrollingBackgroundView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.PhysicsUnitConverter;

import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


import static com.almasb.fxgl.dsl.FXGL.*;
import static com.example.fp.EntityType.*;

public class GameFactory implements EntityFactory {

    @Spawns("ground")
    public Entity newGround(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.STATIC);
        physics.setFixtureDef(new FixtureDef().friction(2.5f));

        return entityBuilder(data).type(GROUND)
                .viewWithBBox(new Rectangle(1200, 300, Color.GRAY)).opacity(0.5)
                .with(physics)
                .build();
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        String name = data.get("name");
        int number = data.get("number");

//        Texture t = texture(name.concat("/idle.gif"));

        return entityBuilder(data).type(PLAYER)
                .collidable()
                //.viewWithBBox(new Rectangle(60,100, new Color(1,0,0,0.5)))
                .bbox(new HitBox(BoundingShape.box(60, 100)))
                //.viewWithBBox(t)
                .with(physics)
                .with(new PlayerComponent(name, number))
                .build();
    }

    @Spawns("enemy")
    public Entity newEnemy(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        String name = data.get("name");
        int number = data.get("number");

        return entityBuilder(data).type(PLAYER)
                .collidable()
                //.viewWithBBox(new Rectangle(60,100, new Color(1,0,0,0.5)))
                .bbox(new HitBox(BoundingShape.box(60, 100)))
                //.viewWithBBox(t)
                .with(physics)
                .with(new AIComponent(name, number))
                .with(new PlayerComponent(name, number))
                .build();
    }

    @Spawns("boss")
    public Entity newBoss(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        var texture = texture("boss/idle1.png",150,60);
//        String name = data.get("name");
//        int number = data.get("number");

        return entityBuilder(data)
                .type(PLAYER)
                .collidable()
                //.viewWithBBox(new Rectangle(60,100, new Color(1,0,0,0.5)))
                .bbox(new HitBox(BoundingShape.box(80, 100)))
                //.viewWithBBox(texture)
                .with(physics)
                .with(new PlayerComponent("boss", 2))
                .with(new BossComponent())
                .build();
    }

    @Spawns("map")
    public Entity newMap(SpawnData data) {
        String name = data.get("mapName");
        return entityBuilder(data).type(MAP)
                .view(
                        texture(name, 800, 600)
                )
                .zIndex(-1)
                .build();
    }

    @Spawns("attack")
    public Entity newAttack(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        int playerNumber = data.get("number");

        return entityBuilder(data).type(ATTACK)
                .viewWithBBox(new Rectangle(40, 30, Color.RED)).opacity(0.5)
                //.bbox(BoundingShape.box(40, 30))
                .collidable()
                .build();
    }

    @Spawns("projectile")
    public Entity newProjectile(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        int playerNumber = data.get("number");
        Point2D dir = data.get("dir");
        return entityBuilder(data).type(ATTACK)
//                .viewWithBBox(new Rectangle(40, 30, Color.RED)).opacity(0.5)
                //.bbox(BoundingShape.box(40, 30))
                .viewWithBBox(texture("boss/hadouken.png", 60, 60))
                .collidable()
                .with(new ProjectileComponent(dir, 200))
                .with(new OffscreenCleanComponent())
                .build();
    }

    @Spawns("flame")
    public Entity newFlame(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        var t = texture("boss/flame.gif");
        int playerNumber = data.get("number");

        return entityBuilder(data).type(ATTACK)
                //.viewWithBBox(new Rectangle(40, 30, Color.RED)).opacity(0.5)
                //.bbox(BoundingShape.box(40, 30))
                .viewWithBBox(t)
                .collidable()
                .build();
    }
}

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.bosses.bossrush;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.BGMPlayer;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Boss;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.lb.RivalSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor.Glyph;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTenacity;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFirebolt;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.hightwand.WandOfVenom;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon.Enchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.nosync.DeepShadowLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Rival extends Boss implements Callback {

    private static final float TIME_TO_ZAP	= 1f;

    @Override
    public String name() {
        return Messages.get(this,"name",hero.name());
    }


    {
        spriteClass = RivalSprite.class;
        properties.add(Property.BOSS);
    }

    public MeleeWeapon weapon;
    public Armor armor;
    public KindofMisc misc1;
    public KindofMisc misc2;
    public Wand wand;
    public MissileWeapon missile;

    public Rival() {
        super();

        int lvl = hero.lvl;

        //melee
        do {
            weapon = (MeleeWeapon)Generator.random(Generator.Category.WEAPON);
        } while (weapon.cursed);
        weapon.enchant(Enchantment.random());
        weapon.identify();

        flying = true;

        //armor
        do {
            armor = (Armor)Generator.random(Generator.Category.ARMOR);
        } while (armor.cursed);
        armor.inscribe(Glyph.random());
        armor.identify();

        //misc1
        do {
            misc1 = (KindofMisc)Generator.random(Generator.Category.RING);
        } while (misc1.cursed);
        misc1.identify();

        //misc2
        do {
            misc2 = (KindofMisc)Generator.random(Generator.Category.RING);
        } while (misc2.cursed);
        misc2.identify();

        //wand
        do {
            wand = RandomWand();
        } while (wand.cursed);
        wand.updateLevel();
        wand.curCharges = 20;
        wand.identify();

        //missile
        do {
            missile = (MissileWeapon)Generator.random(Generator.Category.MISSILE);
        } while (missile.cursed);
        HP = HT = 50 + lvl * 5;
        defenseSkill = (int)(armor.evasionFactor( this, 7 + lvl ));

        EXP = lvl * 17;

    }

    private static final String WEAPON	= "weapon";
    private static final String ARMOR	= "armor";
    private static final String MISC1	= "misc1";
    private static final String MISC2	= "misc2";
    private static final String WAND	= "wand";
    private static final String MISSILE	= "missile";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( WEAPON, weapon );
        bundle.put( ARMOR, armor );
        bundle.put( MISC1, misc1 );
        bundle.put( MISC2, misc2 );
        bundle.put( WAND, wand );
        bundle.put( MISSILE, missile );
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        weapon		= (MeleeWeapon)		bundle.get( WEAPON );
        armor		= (Armor)			bundle.get( ARMOR );
        misc1		= (KindofMisc)		bundle.get( MISC1 );
        misc2		= (KindofMisc)		bundle.get( MISC2 );
        wand		= (Wand)			bundle.get( WAND );
        missile		= (MissileWeapon)	bundle.get( MISSILE );
        if (state != SLEEPING) BossHealthBar.assignBoss(this);
        if ((HP*2 <= HT)) BossHealthBar.bleed(true);
    }

    @Override
    public int damageRoll() {
        int dmg = 0;
        dmg += weapon.damageRoll( this );
        if (dmg < 0) dmg = 0;
        return dmg;
    }

    @Override
    public int drRoll() {
        int dr = 0;
        dr += Random.NormalIntRange( armor.DRMin(), armor.DRMax() );
        dr += Random.NormalIntRange( 0, weapon.defenseFactor( this ) );
        if (dr < 0) dr = 0;
        return dr;
    }

    @Override
    public int attackSkill( Char target ) {
        return (int)((12 + Dungeon.depth) * weapon.accuracyFactor( this,target ));
    }

    @Override
    public float attackDelay() {
        return super.attackDelay() * weapon.speedFactor( this );
    }

    @Override
    public float speed() {
        float speed = 0;
        if(misc1 instanceof RingOfHaste || misc2 instanceof RingOfHaste){
            speed += RingOfHaste.speedMultiplier(this);
        }
        if(armor != null){
            speed += armor.speedFactor( this, super.speed() );
        }
        return speed;
    }


    @Override
    protected boolean canAttack( Char enemy ) {
        return super.canAttack(enemy)
                || weapon.canReach(this, enemy.pos)
                || (new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos);
    }

    protected boolean doAttack( Char enemy ) {

        if (Dungeon.level.adjacent( pos, enemy.pos ) || weapon.canReach(this, enemy.pos)) {

            return super.doAttack( enemy );

        } else {

            boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
            if (wand.curCharges > 0) {
                if (visible) {
                    sprite.zap( enemy.pos );
                } else {
                    zap();
                }
                wand.curCharges--;
            } else if (missile.quantity() > 0) {
                if (visible) {
                    sprite.toss( enemy.pos );
                } else {
                    toss();
                }
            }

            return !visible;
        }
    }

    private void zap() {
        spend( TIME_TO_ZAP );

        final Ballistica shot = new Ballistica( pos, enemy.pos, wand.collisionProperties);

        wand.rivalOnZap( shot, this );
    }

    private void toss() {
        spend( TIME_TO_ZAP );

        if (hit( this, enemy, true )) {
            enemy.damage( this.missile.damageRoll(this), this.missile.getClass() );
        } else {
            enemy.sprite.showStatus( CharSprite.NEUTRAL, enemy.defenseVerb() );
        }
    }

    public void onZapComplete() {
        zap();
        next();
    }

    public void onTossComplete() {
        toss();
        next();
    }

    @Override
    public void call() {
        next();
    }

    @Override
    public int attackProc( Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );
        return weapon.proc( this, enemy, damage );
    }

    @Override
    public int defenseProc( Char enemy, int damage ) {
        damage = super.defenseProc( enemy, damage );
        return armor.proc( enemy, this, damage );
    }

    @Override
    public void damage( int dmg, Object src ) {
        super.damage( dmg, src );
        if (HP <= 0) {
            spend( TICK );
        }
    }

    @Override
    public void die( Object cause ) {
        Dungeon.level.unseal();
        DeepShadowLevel.State state = ((DeepShadowLevel)Dungeon.level).state();
        if (Statistics.happyMode && state != DeepShadowLevel.State.WON) {

            //cures doom and drops missile weapons
            for (Buff buff : buffs()) {
                if (buff instanceof Doom || buff instanceof PinCushion) {
                    buff.detach();
                }
            }


            switch(state) {
                case BRIDGE:
                    HP = 1;

                    PotionOfHealing.cure(this);
                    Buff.detach(this, Paralysis.class);

                    ((DeepShadowLevel)Dungeon.level).progress();

                    yell( Messages.get(this, "interrobang") );
                    return;
                case PHASE_1:
                case PHASE_2:
                case PHASE_3:
                case PHASE_4:
                    HP = HT;
                    for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])){
                        if(mob instanceof Rival){
                            Buff.affect(mob, Dread.class);
                        }
                    }
                    PotionOfHealing.cure(this);
                    Buff.detach(this, Paralysis.class);

                    if (Dungeon.level.heroFOV[pos] && hero.isAlive()) {
                        new Flare(8, 32).color(0xFFFF66, true).show(sprite, 2f);
                        CellEmitter.get(this.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
                        Sample.INSTANCE.play( Assets.Sounds.TELEPORT );
                        GLog.w( Messages.get(this, "revive") );
                    }

                    ((DeepShadowLevel)Dungeon.level).progress();

                    yell( Messages.get(this, "exclamation") );

                    switch (state) {
                        case PHASE_1:
                            wand = new WandOfFrost();
                            wand.curCharges = 20;
                            wand.level(3);
                            wand.updateLevel();
                            break;
                        case PHASE_2:
                            wand = new WandOfBlastWave();
                            wand.curCharges = 40;
                            misc1 = new RingOfHaste();
                            wand.level(2);
                            wand.updateLevel();
                            break;
                        case PHASE_3:
                            wand = new WandOfFirebolt();
                            misc1 = new RingOfHaste();
                            wand.level(1);
                            wand.updateLevel();
                            wand.curCharges = 80;
                            break;
                        case PHASE_4:
                            wand = new WandOfVenom();
                            wand.level(8);
                            wand.updateLevel();
                            misc1 = new RingOfTenacity();
                            wand.curCharges = 100;
                            break;
                    }
                    HP = HT;
                    missile = (MissileWeapon)Generator.random(Generator.Category.MISSILE);
                    return;
                case PHASE_5:
                    ((DeepShadowLevel)Dungeon.level).progress();
                    super.die( cause );
                    wand = new WandOfMagicMissile();
                    misc1 = new RingOfTenacity();
                    Statistics.doNotLookLing = true;
                    GameScene.bossSlain();

                    yell( Messages.get(this, "ellipsis") );
                    return;
                case WON:
                default:
            }
        } else {
            super.die( cause );

            GameScene.bossSlain();

            yell( Messages.get(this, "ellipsis") );
        }
    }

    @Override
    public void notice() {
        super.notice();
        Dungeon.level.seal();
        BGMPlayer.playBoss();
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            yell(Messages.get(this, "question"));

        }
    }

    @Override
    public String description() {
        String desc = super.description();

        desc += Messages.get(this, "weapon", weapon.toString() );
        desc += Messages.get(this, "armor", armor.toString() );
        desc += Messages.get(this, "ring", misc1.toString() );
        desc += Messages.get(this, "ring", misc2.toString() );
        desc += Messages.get(this, "wand", wand.toString() );
        desc += Messages.get(this, "missile", missile.toString() );
        desc += Messages.get(this, "ankhs");

        return desc;
    }

    {
        resistances.add( Grim.class );
        resistances.add( GrimTrap.class );
        resistances.add( ScrollOfRetribution.class );
        resistances.add( ScrollOfPsionicBlast.class );
        immunities.add( Amok.class );
        immunities.add( Corruption.class );
        immunities.add( Terror.class );
    }

    private Wand RandomWand() {
        wand = new WandOfCorrosion();
        wand.curCharges = 20;
        return wand;
    }


}

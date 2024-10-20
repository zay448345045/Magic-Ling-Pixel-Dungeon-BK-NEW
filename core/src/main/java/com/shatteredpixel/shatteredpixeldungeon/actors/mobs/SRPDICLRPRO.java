/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2018 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SRPDICLRTT;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class SRPDICLRPRO extends Mob {
    private static final String COMBO = "combo";
    private int combo = 0;

    public SRPDICLRPRO() {
        this.spriteClass = SRPDICLRTT.class;
        this.HT = 30;
        this.HP = 30;
        this.defenseSkill = 7;
        this.EXP = -1215;
        this.maxLvl = 15;
        this.state = this.SLEEPING;
    }

    public void spawnAround( int pos ) {
        for (int n : PathFinder.NEIGHBOURS4) {
            int cell = pos + n;
            if (Dungeon.level.passable[pos+1] && Actor.findChar( cell ) == null) {
                spawnAt( cell );
            }
        }
    }
    private static final float SPAWN_DELAY	= 2f;
    private int level;
    public void adjustStats( int level ) {
        this.level = level;
        defenseSkill = attackSkill( null ) * 5;
        enemySeen = true;
    }

    public static SRPDICLRPRO spawnAt( int pos ) {
        if (!Dungeon.level.solid[pos] && Actor.findChar( pos ) == null) {

            SRPDICLRPRO w = new SRPDICLRPRO();
            w.adjustStats( Dungeon.depth );
            w.pos = pos;
            w.state = w.HUNTING;
            GameScene.add( w, SPAWN_DELAY );

            w.sprite.alpha( 0 );
            w.sprite.parent.add( new AlphaTweener( w.sprite, 1, 0.5f ) );

            w.sprite.emitter().burst( ShadowParticle.CURSE, 5 );

            return w;
        } else {
            return null;
        }
    }

    public int attackSkill(Char target) {
        return 16;
    }

    public int damageRoll() {
        return Char.combatRoll(11, 12);
    }

    public int attackProc(Char enemy, int damage) {
        if (Random.Int(0, 10) > 7) {
            this.sprite.showStatus(0x009999, Messages.get(this,"attack_msg_"+Random.IntRange(1, 3)));
        }
        int damage2 = SRPDICLRPRO.super.attackProc(enemy, this.combo + damage);
        this.combo++;
        if (enemy.buff(Burning.class) == null) {
            Buff.affect(enemy, Burning.class).reignite(enemy);
            Buff.prolong(enemy, Chill.class, Chill.DURATION * 3);
        }
        if (this.combo > 5) {
            this.combo = 1;
        }
        return damage2;
    }

    public void die(Object cause) {
        SRPDICLRPRO.super.die(cause);
        if (cause != Chasm.class) {
            //如果新增临死前的话，请将1改为Random.IntRange
            this.sprite.showStatus(0x009999, Messages.get(this,"death_msg_"+1), new Object[0]);
        }
    }}

/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
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

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.AlarmTrap;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BatSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class BrownBat extends Mob {

    {
        spriteClass = BatSprite.BatEDSprite.class;

        HP = HT = 15;
        defenseSkill = 24;
        baseSpeed = 1.3f;

        EXP = 7;
        maxLvl = 15;

        flying = true;

        loot = new PotionOfHealing();
        lootChance = 0.1667f; //by default, see rollToDropLoot()
    }

    @Override
    public void die( Object cause ) {

        super.die(cause);

        if (Random.Int(3) == 0) {
            AlarmTrap var4 = new AlarmTrap();
            var4.pos = super.pos;
            Sample.INSTANCE.play( Assets.Sounds.CHALLENGE );
            var4.activate();

        }
            for (Buff buff : hero.buffs()) {
                if (buff instanceof Blindness) {
                    buff.detach();
                }
            }
        }


    @Override
    public int damageRoll() {
        return Char.combatRoll( 5, 9 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 15;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 4);
    }

    @Override
    public int attackProc( Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );


        if (damage > 4) {
            Buff.prolong( enemy, Blindness.class, Blindness.DURATION*1.5f );
        }

        return damage;
    }

    @Override
    public void rollToDropLoot() {
        lootChance *= ((7f - Dungeon.LimitedDrops.BAT_HP.count) / 7f);
        super.rollToDropLoot();
    }

    @Override
    public Item createLoot(){
        Dungeon.LimitedDrops.BAT_HP.count++;
        return super.createLoot();
    }

}

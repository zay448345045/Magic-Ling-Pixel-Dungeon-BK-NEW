package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.zero;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.PaswordBadges;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.GodNPC;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.plot.YetYogPlot;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfDivination;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YetYogSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndDialog;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.List;

public class YetYog extends GodNPC {

    {
        spriteClass = YetYogSprite.class;
        properties.add(Property.IMMOVABLE);
        properties.add(Property.UNKNOWN);
        maxLvl = -1;
    }

    @Override
    public String defenseVerb() {
        return def_verb();
    }
    private String def_verb(){
        if(Random.Int(100)>=50){
            return Messages.get(this, "def_verb_1");
        } else if(Random.Int(100)>=20) {
            return Messages.get(this, "def_verb_2");
        } else {
            return Messages.get(this, "def_verb_3");
        }

    }

    private boolean first=true;
    private boolean secnod=true;
    private boolean rd=true;

    private static final String FIRST = "first";
    private static final String SECNOD = "secnod";
    private static final String RD = "rd";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(FIRST, first);
        bundle.put(SECNOD, secnod);
        bundle.put(RD, rd);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        first = bundle.getBoolean(FIRST);
        secnod = bundle.getBoolean(SECNOD);
        rd = bundle.getBoolean(RD);
    }

    @Override
    public boolean interact(Char c) {
        PaswordBadges.loadGlobal();
        List<PaswordBadges.Badge> passwordbadges = PaswordBadges.filtered(true);

        sprite.turnTo(pos, hero.pos);
        YetYogPlot plot = new YetYogPlot();
        YetYogPlot.EndPlot plot2= new YetYogPlot.EndPlot();
        if (first) {
            Game.runOnRenderThread(new Callback() {
                @Override
                public void call() {
                    GameScene.show(new WndDialog(plot, false));
                }
            });
            first = false;
        } else if ( passwordbadges.contains(PaswordBadges.Badge.KILL_YOGSTS) && secnod ){
            yell(Messages.get(this,"scroll"));
            if(Statistics.zeroItemLevel >=4 && Dungeon.depth == 0) {
                Dungeon.level.drop(new Gold(100), hero.pos);
            } else {
                Item item = new ScrollOfDivination();
                item.identify();
                Dungeon.level.drop( item , hero.pos );
            }
            secnod = false;
        } else {
            Game.runOnRenderThread(new Callback() {
                @Override
                public void call() {
                    GameScene.show(new WndDialog(plot2,false));
                }
            });
        }
        return true;
    }

}

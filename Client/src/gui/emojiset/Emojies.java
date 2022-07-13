package gui.emojiset;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Emojies {
    private static String getEmoji(byte[] data){
        return new String(data, Charset.forName("UTF-8"));
    }
    public static String getEmoji(String mm){
        switch (mm){
            case "laugh":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x98,(byte)0x82});
            case "smile":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x98,(byte)0x83});
            case "cry":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x98,(byte)0xAD});
            case "heart":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x92,(byte)0x9C});
            case "ok":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x91,(byte)0x8C});
            case "thumb":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x91,(byte)0x8D});
            case "fist":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x91,(byte)0x8A});
            case "angry":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x98,(byte)0xA1});
            case "tick":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9C,(byte)0x85});
            case "cross":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9D,(byte)0x8C});
            case "moon":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x8C,(byte)0x99});
            case "twoheart":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x92,(byte)0x95});
            case "circle":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x94,(byte)0xB4});
            case "mouthless":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x98,(byte)0xB6});
            case "tree":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x8C,(byte)0xB2});
            case "square":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x94,(byte)0xB7});
            case "fire":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x94,(byte)0xA5});
            case "hundred":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x92,(byte)0xAF});
            case "star":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x92,(byte)0xAB});
            case "poop":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x92,(byte)0xA9});
            case "pointright":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x91,(byte)0x89});
            case "pointleft":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x91,(byte)0x88});
            case "pointdown":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x91,(byte)0x87});
            case "pointup":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x91,(byte)0x86});
            case "eyes":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x91,(byte)0x80});
            case "rose":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x8C,(byte)0xB9});
            case "palm":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9C,(byte)0x8B});
            case "hearteyes":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x98,(byte)0x8D});
            case "sparkles":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9C,(byte)0xA8});
            case "plus":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9E,(byte)0x95});
            case "minus":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9E,(byte)0x96});
            case "divide":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9E,(byte)0x97});
            case "snowflake":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9D,(byte)0x84});
            case "pencil":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9C,(byte)0x8F});
            case "victoryhand":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9C,(byte)0x8C});
            case "airplane":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9C,(byte)0x88});
            case "mail":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9C,(byte)0x89});
            case "questionmark":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9D,(byte)0x93});
            case "rocket":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x9A,(byte)0x80});
            case "ambulance":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x9A,(byte)0x91});
            case "nosmoking":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x9A,(byte)0xAD});
            case "bike":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x9A,(byte)0xB2});
            case "walking":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x9A,(byte)0xB6});
            case "houglass":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x8C,(byte)0x9B});
            case "tickbox":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x98,(byte)0x91});
            case "recycle":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x99,(byte)0xBB});
            case "anchor":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9A,(byte)0x93});
            case "soccerball":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9A,(byte)0xBD});
            case "baseball":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9A,(byte)0xBE});
            case "sunbehindcloud":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9B,(byte)0x85});
            case "noentry":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9B,(byte)0x94});
            case "church":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9B,(byte)0xAA});
            case "tent":
                return getEmoji(new byte[]{(byte)0xE2,(byte)0x9B,(byte)0xBA});
            case "wave":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x8C,(byte)0x8A});
            case "volcano":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x8C,(byte)0x8B});
            case "earth":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x8C,(byte)0x8F});
            case "glowingstar":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x8C,(byte)0x9F});
            case "apple":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x8D,(byte)0x8E});
            case "hamburger":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x8D,(byte)0x94});
            case "birthdaycake":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x8E,(byte)0x82});
            case "dart":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x8E,(byte)0xAF});
            case "dice":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x8E,(byte)0xB2});
            case "chick":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x90,(byte)0xA5});
            case "penguin":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x90,(byte)0xA7});
            case "thumbsdown":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x91,(byte)0x8E});
            case "clappinghands":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x91,(byte)0x8F});
            case "gem":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x92,(byte)0x8E});
            case "sparklingheart":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x92,(byte)0x96});
            case "growingheart":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x92,(byte)0x97});
            case "pushpin":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x93,(byte)0x8C});
            case "telephone":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x93,(byte)0x9E});
            case "lock":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x94,(byte)0x92});
            case "linkchain":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x94,(byte)0x97});
            case "underage":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x94,(byte)0x9E});
            case "pokerface":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x98,(byte)0x90});
            case "suprisedface":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x98,(byte)0xAE});
            case "shower":
                return getEmoji(new byte[]{(byte)0xF0,(byte)0x9F,(byte)0x9A,(byte)0xBF});



            //todo : 77
        }
        return ":"+mm+":";
    }

    public static ArrayList<Emoji> getAllEmojies(){
        ArrayList<String> names = new ArrayList<>(Arrays.asList("laugh","smile","cry","heart","ok","thumb","fist","angry","tick","cross","moon","twoheart","circle","mouthless","tree","square","fire","hundred","star","poop","pointright","pointleft","pointdown","pointup","eyes","rose","palm","hearteyes","sparkles","plus","minus","divide","snowflake","pencil","victoryhand","airplane","mail","questionmark","rocket","ambulance","nosmoking","bike","walking","houglass","tickbox","recycle","anchor","soccerball","baseball","sunbehindcloud","noentry","church","tent","wave","volcano","earth","glowingstar","apple","hamburger","birthdaycake","dart","dice","chick","penguin","thumbsdown","clappinghands","gem","sparklingheart","growingheart","pushpin","telephone","lock","linkchain","underage","pokerface","suprisedface","shower"));
        ArrayList<Emoji> result = new ArrayList<>();
        for(String name : names){
            Emoji results = new Emoji(name,getEmoji(name));
            result.add(results);
        }
        return result;
    }
}

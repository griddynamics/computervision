package com.griddynamics.pojo.starsDomain;

import java.util.Set;

/**
 * Created by npakhomova on 3/12/16.
 */
public enum CategoryEnum implements ICategory {
//    RugsCommon("11006,11180,11628,15316,13636,14786,14787,14799,13916,13073,14861,14867,14235,12543,12559,12618,14370,16231,17247,17465,16592,16593,16651,16739,17584,16905,17808,17018,18592,17862,17874,16050,16101,20653,20800,22349,20828,20834,20841,20856,22476,20877,20078,20135,20218,21067,20291,20292,20329,20522,25043,23606,24478,23849,24502,24592,23283,22713,23486,26275,26281,26920,26922,27872,27880,27783,27184,27773,27221,27900,27818,26643,26647,26648,27350,27351,26128,27527,27528,28201,28223,28240,29515,30187,29643,29650,29677,29729,28554,28557,28560,29746,29271,29310,29368,28157,28810,31400,32597,31576,31644,31645,32292,31197,31198,33838,33905,33908,33909,35374,33998,34004,34021,34023,34030,34031,34032,34033,33539,34907,32958,35608,35610,35611,35612,35613,36962,35651,36971,35723,35726,35735,35736,35737,35738,35739,35740,35741,35742,35743,35744,36396,35774,37791,37133,37316,37323,37346,36726,36749,36866,37520,38165,38180,39483,39491,38249,40223,40224,38289,40266,40267,40271,40272,39634,39646,39647,39665,38493,39179,39356,38740,40765,40813,41509,40893,42910,42916,40999,41734,41735,41736,42376,42399,41119,45227,44631,44633,44653,43395,45310,45416,43534,43546,43564,44853,44854,44867,45430,45449,44282,45079,46986,46987,46988,46992,46998,47143,46580,47801,47216,47239,46076,46630,46633,46650,46656,46147,47366,46197,46198,45613,46293,47557,49682,49684,48411,49083,58466,50742,50232,52946,52953,51017,51602,52303,52310,52411,52412,52566,52591,52899,54009,55967,54764,55377,54770,5548,58623,8211,8240,9532,9535,9639,7361,57322,7399,7513,57566,58822,60133,46579,58949,59664,61818,61048,60951,60913,60882,44635,40729,63371,63378,63908,63377,63379,63372,63376,63373,63374,64078,63380,62687,62521,65025,66340,66513,66746,65907,65408,66553,66798,65791,65910,66471,66593,65996,66191,65909,65908,66639,68089,68806,68090,68591,68092,69773,65009,68786,68103,68093,69527,68091,71614,70016,71050,70018,73242,72643,74464,75051,76111,72147,74151,74217,74309,63375,76108,76682,72755,72854,66279,35745,72756,41737,75067,73016,31453,72944,74653,74654,74655,76224,72420,76186,72912,73581,75295,75296,76488,76155,72301,73989,73965,73545,74461,74463,74893,72070",
//            ShapeDetectionStrategy.RUGS),
//    Sports_Fan_Shop_By_Lids("65701"),
//        Kids__Baby("5991"),
    Dresses_11("5449", true),
//    Dress_Shirts("20635"),
//    Shoes_1("65"),
//    Tops_12("255"),
//    Sandals("17570"),
//    Boots("25122"),
//    Finish_Line_Athletic_Shoes_1(""63266"),
//    Jeans_11("11221"),
//    T_Shirts("30423"),
//    Pants("89"),
//    Casual_Button_Down_Shirts("20627"),
//    Finish_Line_Athletic_Shoes_2("63268"),
//    Bras_Panties__Shapewear("225"),
//    Pumps("26481"),
//    Tops_11("34048"),
//    Maternity("66718"),
//    Pants__Capris_11("157"),
//    Handbags__Accessories("26846"),
//    Flats("50295"),
//    Sweaters_1("260"),
//    Coats__Jackets("3763"),
//    Jackets__Blazers_11("120"),
//    Finish_Line_Athletic_Shoes("63270"),
//    Jeans_1("3111"),
//    Fashion_Jewelry("55285"),
//    Tops_1("55613"),
//    Blazers__Sport_Coats("16499"),
//    Suits__Suit_Separates("17788"),
//    Shorts_1("3310"),
//    Rings("21176"),
//    Coats_1("269"),
    Dresses_2("55596",true),
//    Sweaters_2("4286"),
    Polos("20640",true),
    Dresses_3("18109",true),
//    Skirts_2("131"),
//    Tops("17043"),
//    Sneakers("26499"),
    Dresses_4("37038",true),
//    Hoodies__Sweatshirts("25995"),
//    Underwear_("57"),
//    Pants__Capris_1("55607"),
//    Pants__Capris("34053"),
//    Wear_to_Work_1("39096"),
//    Swimwear_1("8699"),
//    Shoes_2("48561"),
//    Jeans_2("28754"),
//    Accessories__Wallets("47665"),
//    Swimwear_2("3291"),
//    Shorts_4("5344"),
//    Sweaters_4("40227"),
//    Sweaters_5("55612"),
//    Shoes("13247"),
//    Ties__Pocket_Squares("53239"),
//    Jeans_5("40438"),
//    Wear_to_Work("55610"),
//    Sunglasses_by_Sunglass_Hut_1("28295"),
//    Jeans("55603"),
//    Leggings__Pants("21561"),
//    Jackets__Blazers_1("46203"),
//    Sweaters("20853"),
//    Furniture("29391"),
//    Earrings("10835"),
//    Watches("23930"),
//    Necklaces_("9569"),
//    Bedding_Collections("7502"),
//    Hats_Gloves__Scarves("47657"),
//    Jackets__Blazers("55600"),
//    Pajamas_Robes__Slippers("16295"),
//    Holiday_Lane("30599"),
//    Shorts_2("28589"),
//    Makeup("30077"),
//    Sunglasses_by_Sunglass_Hut("58262"),
//    Skirts_3("34057"),
//    Skirts_4("55609"),
//    Jackets__Vests("35786"),
//    Skirts("28379"),
//    COACH("25300"),
//    Serveware("7923"),
//    Dinnerware("53629"),
////    Coats_4("57535"),
////    Lighting__Lamps("39267"),
////    Shorts_6("37000"),
//    Shop_All_Brands("30076"),
//    Activewear("3296"),
//    Bracelets("10834"),
//    Swimwear("34050"),
//    Coats_7("34049"),
//    Belts__Suspenders("47673"),
//    The_Fur_Vault("69211"),
////    Cookware("7552"),
////    Shorts("55608"),
//    Bathroom_Accessories("22094"),
//    Bath_Towels("16853"),
//    Kitchen_Gadgets("31839"),
//    Shop_All_Glassware__Stemware("65938"),
//    Curtains__Drapes_1("34769"),
////    Jewelry__Watches("544"),
//    Table_Linens("17199"),
//    Bed_in_a_Bag("26795"),
//    Slippers("16108"),
//    Dress_Shirt__Tie_Combos("26013"),
//    Collections("56127"),
    Dresses("88076", true),
//    Blankets__Throws("29405"),
//    Check_In_Luggage("71198"),
//    Women_s_Brands("9572"),
//    Socks("18245"),
////    Skin_Care("30078"),
////    Gifts_Gadgets__Audio("49167"),
//    Window_Treatments("26435"),
//    Gifts__Value_Sets("55537"),
//    Slipcovers("24672"),
////    Fine_China("53630"),
////    Storage__Organization("51662"),
//    Backpacks("29722"),
//    Sheets("9915"),
////    Electrics("7554"),
////    Sunglasses_4("58170"),
//    Carry_On_Luggage("25691"),
////    Bowls__Vases("55973"),
//    Bath_Rugs__Bath_Mats("8240"),
//    Curtain_Rods__Hardware("57517"),
//    Mattress_Pads__Toppers("40384"),
////    Duffels__Totes("20688"),
//    Pillows("28901"),
//    Bags__Backpacks("45016"),
////    Gourmet_Food__Gifts("45859"),
////    Quilts__Bedspreads("22748"),
//    Bakeware("31795"),
////    Coffee_Tea__Espresso("24732"),
////    Gifts_with_Purchase("58476"),
////    Candles__Home_Fragrance("55974"),
////    Cutlery__Knives("31760"),
//    Picture_Frames("55977"),
//    Coats("21115"),
//    Shower_Curtains__Accessories("58936"),
//    Collectible_Figurines("55976"),
//    Travel_Accessories("29563"),
//    Bar_Accessories("28973"),
//    All_Suits__Suit_Separates("67592"),
//    Flatware__Silverware("7919"),
//    Plus_Sizes("32147"),
//    Decorative_Pillows("37945"),
//    Hair_Care("40554"),
//    Comforters_Down__Alternative("28898"),
//    Sheer_Curtains("57514"),
//    Sneakers__Athletic_("55642"),
////    Garment_Bags("25692"),
////    Accessories_5("27688"),
//    Men_s_Brands("9557"),
//    Wall_Art("55979"),
////    Shop_All_Suits__Suit_Separates("67545"),
////    Sport_Sunglasses_for_Women("58296"),
////    Mirrors("55978"),
////    Curtains__Drapes("57513"),
////    Petite("18579"),
////    Mascara("53452"),
////    Shirts("20626"),
////    Tees__Tanks("61124"),
////    Personal_Care("23487"),
//    Cookware_Sets("7631"),
////    Accessories("63009"),
////    Beauty("669"),
////    Shop_Super_Bowl_50_Gear("70022"),
//    Kids__Luggage("70805"),
//    Duvet_Covers("25045"),
////    Luggage_Sets("17799"),
////    mattresses("25931"),
////    Coach_Accessories("25313"),
////    Vacuums__Steam_Cleaners("23481"),
////    Window_Tiers("32381"),
////    Juniors__Brands("17133"),
////    Upright_Luggage("26195"),
////    essie_Nail_Color("57685"),
//    Sunglasses("27808"),
////    Blinds__Shades("47846"),
////    Lace_Ups__Oxfords("55639"),
//    Kids__Bedding("13661"),
    Laptop__Messenger_Bags("25547");

    private String categoriesList;

    private ShapeDetectionStrategy shapeDetectionStrategy;
    private boolean isBodyContains;

    CategoryEnum(String categoryId) {
        this.categoriesList = categoryId;
    }

    CategoryEnum(String categoryId, ShapeDetectionStrategy shapeDetectionStrategy) {
        this.categoriesList = categoryId;
        this.shapeDetectionStrategy = shapeDetectionStrategy;
    }
    CategoryEnum(String categoryId, boolean isBodyContains) {
        this.categoriesList = categoryId;
        this.isBodyContains = isBodyContains;
    }

    public String getCategoriesJoinedString() {
        return categoriesList;
    }

    public ShapeDetectionStrategy getShapeDetectionStrategy() {
        return shapeDetectionStrategy;
    }

    public String getCategoryName(){
        return name();
    }

    @Override
    public Set<Integer> getCategoryIds() {
        return null;
    }

    @Override
    public boolean isBodyContains() {
        return isBodyContains;
    }
}

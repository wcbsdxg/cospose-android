package com.cospose.gallery.ai

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutoTagConfig @Inject constructor() {

    data class Label(val name: String, val prompt: String)

    data class CategoryConfig(
        val category: String,
        val categoryName: String,
        val template: String,
        val threshold: Float,
        val maxTags: Int,
        val labels: List<Label>
    )

    val categories: List<CategoryConfig> = listOf(
        // ACTION - 34 labels
        CategoryConfig(
            category = "ACTION",
            categoryName = "动作",
            template = "a photo of a person doing {}",
            threshold = 0.15f,
            maxTags = 3,
            labels = listOf(
                Label("战斗姿势", "a fighting pose, combat stance"),
                Label("挥剑", "swinging a sword, sword attack"),
                Label("施法", "casting a spell, magic gesture"),
                Label("结印", "hand seal, ninjutsu hand sign"),
                Label("比心", "heart gesture, finger heart"),
                Label("变身", "transformation pose, magical girl transformation"),
                Label("跳跃", "jumping in the air, dynamic jump"),
                Label("蹲姿", "crouching pose, squatting"),
                Label("坐姿", "sitting pose"),
                Label("站姿", "standing pose"),
                Label("侧身站立", "standing sideways, side pose"),
                Label("单手叉腰", "hand on hip, akimbo"),
                Label("双手叉腰", "both hands on hip"),
                Label("回头", "looking back over shoulder"),
                Label("跪姿", "kneeling pose"),
                Label("躺姿", "lying down pose"),
                Label("弯腰", "bending forward"),
                Label("伸手", "reaching out, extending hand"),
                Label("握拳", "clenching fist"),
                Label("持扇", "holding a fan"),
                Label("持伞", "holding an umbrella"),
                Label("持杖", "holding a staff"),
                Label("持枪", "holding a gun"),
                Label("持弓", "holding a bow"),
                Label("持盾", "holding a shield"),
                Label("持武器", "holding a weapon"),
                Label("张开双臂", "arms spread wide, open arms"),
                Label("双手合十", "hands pressed together, prayer pose"),
                Label("托腮", "chin resting on hand"),
                Label("遮阳", "shading eyes from sun"),
                Label("敬礼", "salute gesture"),
                Label("剪刀手", "peace sign, V sign"),
                Label("指点", "pointing gesture"),
                Label("托举", "lifting up, holding overhead")
            )
        ),

        // CHARACTER - 48 labels
        CategoryConfig(
            category = "CHARACTER",
            categoryName = "角色",
            template = "a photo of {} from anime",
            threshold = 0.20f,
            maxTags = 2,
            labels = listOf(
                Label("雷电将军", "Raiden Shogun from Genshin Impact"),
                Label("刻晴", "Keqing from Genshin Impact"),
                Label("甘雨", "Ganyu from Genshin Impact"),
                Label("胡桃", "Hu Tao from Genshin Impact"),
                Label("纳西妲", "Nahida from Genshin Impact"),
                Label("神里绫华", "Ayaka Kamisato from Genshin Impact"),
                Label("荧", "Lumine from Genshin Impact"),
                Label("空", "Aether from Genshin Impact"),
                Label("钟离", "Zhongli from Genshin Impact"),
                Label("魈", "Xiao from Genshin Impact"),
                Label("温迪", "Venti from Genshin Impact"),
                Label("行秋", "Xingqiu from Genshin Impact"),
                Label("香菱", "Xiangling from Genshin Impact"),
                Label("班尼特", "Bennett from Genshin Impact"),
                Label("迪卢克", "Diluc from Genshin Impact"),
                Label("莫娜", "Mona from Genshin Impact"),
                Label("优菈", "Eula from Genshin Impact"),
                Label("八重神子", "Yae Miko from Genshin Impact"),
                Label("星穹铁道·银狼", "Silver Wolf from Honkai Star Rail"),
                Label("星穹铁道·布洛妮娅", "Bronya from Honkai Star Rail"),
                Label("星穹铁道·希儿", "Seele from Honkai Star Rail"),
                Label("星穹铁道·克拉拉", "Clara from Honkai Star Rail"),
                Label("星穹铁道·三月七", "March 7th from Honkai Star Rail"),
                Label("星穹铁道·黑塔", "Herta from Honkai Star Rail"),
                Label("阿米娅", "Amiya from Arknights"),
                Label("德克萨斯", "Texas from Arknights"),
                Label("能天使", "Exusiai from Arknights"),
                Label("陈", "Chen from Arknights"),
                Label("W", "W from Arknights"),
                Label("Saber", "Saber from Fate"),
                Label("远坂凛", "Rin Tohsaka from Fate"),
                Label("间桐樱", "Sakura Matou from Fate"),
                Label("玛修", "Mash Kyrielight from Fate"),
                Label("灶门炭治郎", "Tanjiro Kamado from Demon Slayer"),
                Label("灶门祢豆子", "Nezuko Kamado from Demon Slayer"),
                Label("我妻善逸", "Zenitsu Agatsuma from Demon Slayer"),
                Label("胡蝶忍", "Shinobu Kocho from Demon Slayer"),
                Label("虎杖悠仁", "Yuji Itadori from Jujutsu Kaisen"),
                Label("五条悟", "Gojo Satoru from Jujutsu Kaisen"),
                Label("初音未来", "Hatsune Miku from Vocaloid"),
                Label("镜音铃", "Kagamine Rin from Vocaloid"),
                Label("博丽灵梦", "Reimu Hakurei from Touhou"),
                Label("雾雨魔理沙", "Marisa Kirisame from Touhou"),
                Label("后藤一里", "Hitori Gotoh from Bocchi the Rock"),
                Label("安妮亚", "Anya from Spy x Family"),
                Label("约尔", "Yor from Spy x Family"),
                Label("三笠", "Mikasa from Attack on Titan"),
                Label("砂狼白子", "Shiroko from Blue Archive")
            )
        ),

        // ANIME - 27 labels
        CategoryConfig(
            category = "ANIME",
            categoryName = "作品",
            template = "a character from {}, anime style",
            threshold = 0.25f,
            maxTags = 1,
            labels = listOf(
                Label("原神", "Genshin Impact"),
                Label("崩坏：星穹铁道", "Honkai Star Rail"),
                Label("崩坏3", "Honkai Impact 3rd"),
                Label("明日方舟", "Arknights"),
                Label("蔚蓝档案", "Blue Archive"),
                Label("Fate/stay night", "Fate stay night"),
                Label("Fate/Grand Order", "Fate Grand Order"),
                Label("鬼灭之刃", "Demon Slayer"),
                Label("咒术回战", "Jujutsu Kaisen"),
                Label("进击的巨人", "Attack on Titan"),
                Label("火影忍者", "Naruto"),
                Label("海贼王", "One Piece"),
                Label("死神", "Bleach"),
                Label("刀剑神域", "Sword Art Online"),
                Label("间谍过家家", "Spy x Family"),
                Label("孤独摇滚", "Bocchi the Rock"),
                Label("东方Project", "Touhou Project"),
                Label("Vocaloid", "Vocaloid"),
                Label("偶像大师", "Idolmaster"),
                Label("Love Live", "Love Live"),
                Label("约会大作战", "Date A Live"),
                Label("我的英雄学院", "My Hero Academia"),
                Label("电锯人", "Chainsaw Man"),
                Label("莉可丽丝", "Lycoris Recoil"),
                Label("更衣人偶", "My Dress Up Darling"),
                Label("无职转生", "Mushoku Tensei"),
                Label("葬送的芙莉莲", "Frieren")
            )
        ),

        // OTHER - 14 labels
        CategoryConfig(
            category = "OTHER",
            categoryName = "场景",
            template = "a photo with {} atmosphere",
            threshold = 0.20f,
            maxTags = 1,
            labels = listOf(
                Label("室内", "indoor"),
                Label("室外", "outdoor"),
                Label("夜景", "night scene"),
                Label("樱花", "cherry blossom"),
                Label("海滩", "beach"),
                Label("废墟", "ruins"),
                Label("森林", "forest"),
                Label("城市", "city"),
                Label("城堡", "castle"),
                Label("教室", "classroom"),
                Label("神社", "shrine"),
                Label("赛博朋克", "cyberpunk"),
                Label("奇幻", "fantasy"),
                Label("蒸汽朋克", "steampunk")
            )
        )
    )
}

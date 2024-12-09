package mod.pilot.entomophobia.util;

import mod.pilot.entomophobia.Entomophobia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class EntomoTags {
    public static class Blocks{
        private static TagKey<Block> tag(String name){
            return BlockTags.create(new ResourceLocation(Entomophobia.MOD_ID, name));
        }

        public static final TagKey<Block> MYIATIC_FLESH_BLOCKTAG = tag("myiatic_flesh");
    }

    public static class Items{
        private static TagKey<Item> tag(String name){
            return ItemTags.create(new ResourceLocation(Entomophobia.MOD_ID, name));
        }

        //public static final TagKey<Item> TEST_ITEM_TAG = tag("test_item_tag");
    }
}

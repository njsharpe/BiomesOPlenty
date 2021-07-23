/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package biomesoplenty.common.world.layer;

import biomesoplenty.api.biome.BOPBiomes;
import biomesoplenty.common.util.biome.BiomeUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum BOPBiomeEdgeLayer implements CastleTransformer
{
    INSTANCE;

    private static final int DESERT = BiomeUtil.getBiomeId(Biomes.DESERT);
    private static final int MOUNTAINS = BiomeUtil.getBiomeId(Biomes.MOUNTAINS);
    private static final int WOODED_MOUNTAINS = BiomeUtil.getBiomeId(Biomes.WOODED_MOUNTAINS);
    private static final int SNOWY_TUNDRA = BiomeUtil.getBiomeId(Biomes.SNOWY_TUNDRA);
    private static final int JUNGLE = BiomeUtil.getBiomeId(Biomes.JUNGLE);
    private static final int JUNGLE_HILLS = BiomeUtil.getBiomeId(Biomes.JUNGLE_HILLS);
    private static final int JUNGLE_EDGE = BiomeUtil.getBiomeId(Biomes.JUNGLE_EDGE);
    private static final int BADLANDS = BiomeUtil.getBiomeId(Biomes.BADLANDS);
    private static final int BADLANDS_PLATEAU = BiomeUtil.getBiomeId(Biomes.BADLANDS_PLATEAU);
    private static final int WOODED_BADLANDS_PLATEAU = BiomeUtil.getBiomeId(Biomes.WOODED_BADLANDS_PLATEAU);
    private static final int PLAINS = BiomeUtil.getBiomeId(Biomes.PLAINS);
    private static final int GIANT_TREE_TAIGA = BiomeUtil.getBiomeId(Biomes.GIANT_TREE_TAIGA);
    private static final int MOUNTAIN_EDGE = BiomeUtil.getBiomeId(Biomes.MOUNTAIN_EDGE);
    private static final int SWAMP = BiomeUtil.getBiomeId(Biomes.SWAMP);
    private static final int TAIGA = BiomeUtil.getBiomeId(Biomes.TAIGA);
    private static final int SNOWY_TAIGA = BiomeUtil.getBiomeId(Biomes.SNOWY_TAIGA);

    @Override
    public int apply(Context context, int northBiomeId, int eastBiomeId, int southBiomeId, int westBiomeId, int biomeId)
    {
        int[] outBiomeId = new int[1];

        // line BOP alps peaks with BOP alps foothills
        if (this.replaceBiomeEdge(outBiomeId, northBiomeId, eastBiomeId, southBiomeId, westBiomeId, biomeId,  BOPBiomes.alps, BOPBiomes.alps_foothills)) { return outBiomeId[0]; }

        // line BOP redwood forest with BOP redwood forest edge
        if (this.replaceBiomeEdge(outBiomeId, northBiomeId, eastBiomeId, southBiomeId, westBiomeId, biomeId, BOPBiomes.redwood_forest, BOPBiomes.redwood_forest_edge)) { return outBiomeId[0]; }
        if (this.replaceBiomeEdge(outBiomeId, northBiomeId, eastBiomeId, southBiomeId, westBiomeId, biomeId, BOPBiomes.redwood_hills, BOPBiomes.redwood_forest_edge)) { return outBiomeId[0]; }

        // line BOP volcano with BOP volcanic plains
        if (this.replaceBiomeEdge(outBiomeId, northBiomeId, eastBiomeId, southBiomeId, westBiomeId, biomeId, BOPBiomes.volcano, BOPBiomes.volcanic_plains)) { return outBiomeId[0]; }

        // line mountains with mountain edges
        //if (this.replaceBiomeEdgeIfNecessary(outBiomeId, northBiomeId, eastBiomeId, southBiomeId, westBiomeId, biomeId, MOUNTAINS, MOUNTAIN_EDGE)) { return outBiomeId[0]; }

        // line special badlands with badlands
        if (this.replaceBiomeEdge(outBiomeId, northBiomeId, eastBiomeId, southBiomeId, westBiomeId, biomeId, WOODED_BADLANDS_PLATEAU, BADLANDS)) { return outBiomeId[0]; }
        if (this.replaceBiomeEdge(outBiomeId, northBiomeId, eastBiomeId, southBiomeId, westBiomeId, biomeId, BADLANDS_PLATEAU, BADLANDS)) { return outBiomeId[0]; }

        // line the giant tree taiga with taiga
        if (this.replaceBiomeEdge(outBiomeId, northBiomeId, eastBiomeId, southBiomeId, westBiomeId, biomeId, GIANT_TREE_TAIGA, TAIGA)) { return outBiomeId[0]; }

        if (biomeId == DESERT && (northBiomeId == SNOWY_TUNDRA || eastBiomeId == SNOWY_TUNDRA || westBiomeId == SNOWY_TUNDRA || southBiomeId == SNOWY_TUNDRA))
        {
            return WOODED_MOUNTAINS;
        }
        else
        {
            if (biomeId == SWAMP)
            {
                if (northBiomeId == DESERT || eastBiomeId == DESERT || westBiomeId == DESERT || southBiomeId == DESERT || northBiomeId == SNOWY_TAIGA || eastBiomeId == SNOWY_TAIGA || westBiomeId == SNOWY_TAIGA || southBiomeId == SNOWY_TAIGA || northBiomeId == SNOWY_TUNDRA || eastBiomeId == SNOWY_TUNDRA || westBiomeId == SNOWY_TUNDRA || southBiomeId == SNOWY_TUNDRA)
                {
                    return PLAINS;
                }

                if (northBiomeId == JUNGLE || southBiomeId == JUNGLE || eastBiomeId == JUNGLE || westBiomeId == JUNGLE || northBiomeId == JUNGLE_HILLS || southBiomeId == JUNGLE_HILLS || eastBiomeId == JUNGLE_HILLS || westBiomeId == JUNGLE_HILLS)
                {
                    return JUNGLE_EDGE;
                }
            }
        }

        return biomeId;
    }

    private boolean replaceBiomeEdge(int[] outId, int northBiomeId, int eastBiomeId, int southBiomeId, int westBiomeId, int biomeId, ResourceKey<Biome> fromBiome, ResourceKey<Biome> toBiome)
    {
        return BiomeUtil.exists(fromBiome) && BiomeUtil.exists(toBiome) && this.replaceBiomeEdge(outId, northBiomeId, eastBiomeId, southBiomeId, westBiomeId, biomeId, BiomeUtil.getBiomeId(fromBiome), BiomeUtil.getBiomeId(toBiome));
    }

    private boolean replaceBiomeEdge(int[] outId, int northBiomeId, int eastBiomeId, int southBiomeId, int westBiomeId, int biomeId, int fromBiome, int toBiome)
    {
        if (biomeId != fromBiome)
        {
            return false;
        }
        else
        {
            if (Layers.isSame(northBiomeId, fromBiome) && Layers.isSame(eastBiomeId, fromBiome) && Layers.isSame(westBiomeId, fromBiome) && Layers.isSame(southBiomeId, fromBiome))
            {
                outId[0] = biomeId;
            }
            else
            {
                outId[0] = toBiome;
            }

            return true;
        }
    }
}

/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package biomesoplenty.common.world.layer;

import biomesoplenty.common.biome.BiomeMetadata;
import biomesoplenty.common.util.biome.BiomeUtil;
import biomesoplenty.common.world.BOPLayerUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer2;
import net.minecraft.world.level.newbiome.layer.traits.DimensionOffset0Transformer;

public enum BOPRiverMixLayer implements AreaTransformer2, DimensionOffset0Transformer
{
    INSTANCE;

    private static final int FROZEN_RIVER = BiomeUtil.getBiomeId(Biomes.FROZEN_RIVER);
    private static final int SNOWY_TUNDRA = BiomeUtil.getBiomeId(Biomes.SNOWY_TUNDRA);
    private static final int MUSHROOM_FIELDS = BiomeUtil.getBiomeId(Biomes.MUSHROOM_FIELDS);
    private static final int MUSHROOM_FIELD_SHORE = BiomeUtil.getBiomeId(Biomes.MUSHROOM_FIELD_SHORE);
    private static final int RIVER = BiomeUtil.getBiomeId(Biomes.RIVER);

    @Override
    public int applyPixel(Context context, Area biomeArea, Area riverArea, int x, int z)
    {
        int biomeId = biomeArea.get(x, z);
        int riverId = riverArea.get(x, z);
        ResourceKey<Biome> biome = BiomeUtil.createKey(biomeId);

        if (BOPLayerUtil.isOcean(biomeId))
        {
            return biomeId;
        }
        else if (riverId == RIVER)
        {
            if (biomeId == SNOWY_TUNDRA)
            {
                return FROZEN_RIVER;
            }
            else if (BiomeUtil.hasMetadata(biome))
            {
                BiomeMetadata meta = BiomeUtil.getMetadata(biome);

                if (meta.getRiverBiome() != null)
                    return BiomeUtil.getBiomeId(meta.getRiverBiome());
                else
                    return biomeId;
            }
            else
            {
                return biomeId != MUSHROOM_FIELDS && biomeId != MUSHROOM_FIELD_SHORE ? riverId & 255 : MUSHROOM_FIELD_SHORE;
            }
        }
        else
        {
            return biomeId;
        }
    }
}

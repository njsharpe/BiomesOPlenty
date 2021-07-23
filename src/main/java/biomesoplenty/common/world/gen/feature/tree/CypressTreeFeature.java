/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package biomesoplenty.common.world.gen.feature.tree;

import biomesoplenty.api.block.BOPBlocks;
import biomesoplenty.common.util.biome.GeneratorUtil;
import biomesoplenty.common.util.block.IBlockPosQuery;
import biomesoplenty.common.world.gen.BOPFeatureUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Material;

import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;

public class CypressTreeFeature extends TreeFeatureBase
{
    public static class Builder extends BuilderBase<Builder, CypressTreeFeature>
    {
        protected int trunkWidth;

        public Builder trunkWidth(int a) {this.trunkWidth = a; return this;}

        public Builder()
        {
            this.minHeight = 6;
            this.maxHeight = 15;
            this.placeOn = BOPFeatureUtil::isSoil;
            this.replace = (world, pos) -> TreeFeature.isAirOrLeaves(world, pos) || world.getBlockState(pos).getMaterial() == Material.WATER || world.getBlockState(pos).is(BlockTags.SAPLINGS) || world.getBlockState(pos).getBlock() == Blocks.VINE || world.getBlockState(pos).getBlock() == BOPBlocks.WILLOW_VINE || world.getBlockState(pos).getBlock() == BOPBlocks.SPANISH_MOSS_PLANT || world.getBlockState(pos).getBlock() == BOPBlocks.SPANISH_MOSS || world.getBlockState(pos).getBlock() instanceof BushBlock;
            this.log = BOPBlocks.WILLOW_LOG.defaultBlockState();
            this.leaves = BOPBlocks.WILLOW_LEAVES.defaultBlockState();
            this.vine = BOPBlocks.WILLOW_VINE.defaultBlockState();
            this.trunkWidth = 1;
        }

        @Override
        public CypressTreeFeature create()
        {
            return new CypressTreeFeature(this.placeOn, this.replace, this.log, this.leaves, this.altLeaves, this.vine, this.hanging, this.trunkFruit, this.minHeight, this.maxHeight, this.trunkWidth);
        }

    }

    private int trunkWidth = 1;

    protected CypressTreeFeature(IBlockPosQuery placeOn, IBlockPosQuery replace, BlockState log, BlockState leaves, BlockState altLeaves, BlockState vine, BlockState hanging, BlockState trunkFruit, int minHeight, int maxHeight, int trunkWidth)
    {
        super(placeOn, replace, log, leaves, altLeaves, vine, hanging, trunkFruit, minHeight, maxHeight);
        this.trunkWidth = trunkWidth;
    }

    public boolean checkSpace(LevelAccessor world, BlockPos pos, int baseHeight, int height)
    {
        for (int y = 0; y <= height; y++)
        {
            int radius = this.trunkWidth - 1;

            for (int x = -radius; x <= radius; x++)
            {
                for (int z = -radius; z <= radius; z++)
                {
                    BlockPos pos1 = pos.offset(x, y, z);
                    // note, there may be a sapling on the first layer - make sure this.replace matches it!
                    if (pos1.getY() >= 255 || !this.replace.matches(world, pos1))
                    {
                        return false;
                    }
                }
            }
        }

        BlockPos pos2 = pos.offset(0, height - 2,0);
        if (!TreeFeature.isAirOrLeaves(world, pos2))
        {
            return false;
        }

        return true;
    }

    // generates a layer of leaves
    public void generateLeafLayer(LevelAccessor world, Random rand, BlockPos pos, int leavesRadius, BiConsumer<BlockPos, BlockState> leaves)
    {
        int start = -leavesRadius;
        int end = leavesRadius;

        for (int x = start; x <= end; x++)
        {
            for (int z = start; z <= end; z++)
            {
                // skip corners
                if ((leavesRadius > 0) && (x == start || x == end) && (z == start || z == end))
                {
                    continue;
                }

                // Make ends more scraggly
                if ((leavesRadius > 0) && ((x == start || x == end) || (z == start || z == end)) && rand.nextDouble() < 0.2)
                {
                    continue;
                }

                this.placeLeaves(world, pos.offset(x, 0, z), leaves);
            }
        }
    }

    public void generateBranch(LevelAccessor world, Random rand, BlockPos pos, Direction direction, int length, BiConsumer<BlockPos, BlockState> logs, BiConsumer<BlockPos, BlockState> leaves)
    {
        Direction.Axis axis = direction.getAxis();
        Direction sideways = direction.getClockWise();
        for (int i = 1; i <= length; i++)
        {
            BlockPos pos1 = pos.relative(direction, i);
            int r = (i == 1 || i == length) ? 1 : 2;
            for (int j = -r; j <= r; j++)
            {
                if (i < length || rand.nextInt(2) == 0)
                {
                    this.placeLeaves(world, pos1.relative(sideways, j), leaves);
                }
            }
            if (length - i > 2)
            {
                this.placeLeaves(world, pos1.above(), leaves);
                this.placeLeaves(world, pos1.above().relative(sideways, -1), leaves);
                this.placeLeaves(world, pos1.above().relative(sideways, 1), leaves);
                this.placeLog(world, pos1, axis, logs);
            }
        }
    }


    @Override
    protected boolean place(LevelAccessor world, Random random, BlockPos startPos, BiConsumer<BlockPos, BlockState> logs, BiConsumer<BlockPos, BlockState> leaves)
    {
        // Move down until we reach the ground
        while (startPos.getY() > 1 && this.replace.matches(world, startPos) || world.getBlockState(startPos).getMaterial() == Material.LEAVES) {startPos = startPos.below();}

        for (int x = 0; x <= this.trunkWidth - 1; x++)
        {
            for (int z = 0; z <= this.trunkWidth - 1; z++)
            {
                if (!this.placeOn.matches(world, startPos.offset(x, 0, z)))
                {
                    // Abandon if we can't place the tree on this block
                    return false;
                }
            }
        }

        // Choose heights
        int height = GeneratorUtil.nextIntBetween(random, this.minHeight, this.maxHeight);
        int baseHeight = GeneratorUtil.nextIntBetween(random, (int)(height * 0.6F), (int)(height * 0.4F));
        int leavesHeight = height - baseHeight;
        int baseLeavesHeight = leavesHeight;
        if (leavesHeight < 3) {return false;}

        leavesHeight = Mth.clamp(leavesHeight, 3, 5);
        leavesHeight = Mth.clamp(leavesHeight + random.nextInt(3), 0, baseLeavesHeight);

        if (!this.checkSpace(world, startPos.above(), baseHeight, height))
        {
            // Abandon if there isn't enough room
            return false;
        }

        // Start at the top of the tree
        BlockPos pos = startPos.above(height);

        // Leaves at the top
        this.placeLeaves(world, pos, leaves);
        pos.below();

        // Add layers of leaves
        for (int i = 0; i < leavesHeight; i++)
        {
            int radius = 3;
            if (i == 0)
            {
                radius = 1;
            }
            else if (i <= 2)
            {
                radius = 2;
            }

            this.generateLeafLayer(world, random, pos, radius, leaves);

            pos = pos.below();
        }

        this.placeSpanishMoss(world, random, pos);

        // We make the radius to check 1 less than the width
        int trunkRadius = this.trunkWidth - 1;

        // Generate the trunk
        for (int x = -trunkRadius; x <= trunkRadius; x++)
        {
            for (int z = -trunkRadius; z <= trunkRadius; z++)
            {
                int dist = Math.abs(x) + Math.abs(z);

                if (dist > trunkRadius)
                {
                    continue;
                }

                int heightHere = height - 1;
                if (dist == 1)
                {
                    heightHere = (int) (height * (0.2 + random.nextDouble() * 0.15));
                }

                heightHere += random.nextInt(2);

                for (int y = 0; y < heightHere; y++)
                {
                    BlockPos local = startPos.offset(x, y, z);
                    boolean air = world.getBlockState(local).getFluidState().isEmpty();

                    this.placeLog(world, local, logs);

                    if (x == 0 && z == 0 && air && y < heightHere - leavesHeight + 1)
                    {
                        if (y >= baseHeight && random.nextInt(3) == 0)
                        {
                            // Big branch
                            double theta = Math.PI * random.nextDouble() * 2;

                            int length = 2 + random.nextInt(3);

                            BlockPos branchPos = null;
                            for (int i = 0; i < length; i++)
                            {
                                branchPos = local.offset(Math.cos(theta) * i, i / 2, Math.sin(theta) * i);

                                this.placeLog(world, branchPos, logs);
                            }

                            generateLeafLayer(world, random, branchPos, 2, leaves);
                            generateLeafLayer(world, random, branchPos.above(), 1, leaves);
                            if (random.nextBoolean())
                            {
                                generateLeafLayer(world, random, branchPos.above(2), 0, leaves);
                            }

                            this.placeSpanishMoss(world, random, branchPos);

                        }
                        else if (y >= baseHeight && random.nextInt(3) == 0)
                        {
                            // Small branch
                            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                            BlockPos offset = local.relative(direction);

                            this.placeLog(world, offset, logs);

                            for (Direction dir : Direction.values())
                            {
                                if (random.nextDouble() > 0.2)
                                {
                                    this.placeLeaves(world, offset.relative(dir), leaves);
                                }
                            }

                            this.placeSpanishMoss(world, random, offset);
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean placeLeaves(LevelAccessor world, BlockPos pos, BiConsumer<BlockPos, BlockState> leaves)
    {
        if (TreeFeature.isAirOrLeaves(world, pos))
        {
            leaves.accept(pos, this.leaves);
            return true;
        }
        return false;
    }

    private void placeSpanishMoss(LevelAccessor p_236429_1_, Random p_236429_2_, BlockPos p_236429_3_)
    {
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        for(int i = 0; i < 50; ++i)
        {
            blockpos$mutable.setWithOffset(p_236429_3_, p_236429_2_.nextInt(5) - p_236429_2_.nextInt(5), p_236429_2_.nextInt(3) - p_236429_2_.nextInt(3), p_236429_2_.nextInt(5) - p_236429_2_.nextInt(5));
            if (p_236429_1_.isEmptyBlock(blockpos$mutable))
            {
                BlockState blockstate = p_236429_1_.getBlockState(blockpos$mutable.above());
                if (blockstate.getBlock() == BOPBlocks.WILLOW_LEAVES)
                {
                    int j = Mth.nextInt(p_236429_2_, 1, 3);

                    if (p_236429_2_.nextInt(5) == 0)
                    {
                        j = 1;
                    }

                    placeSpanishMossColumn(p_236429_1_, p_236429_2_, blockpos$mutable, j, 17, 25);
                }
            }
        }
    }

    public static void placeSpanishMossColumn(LevelAccessor p_236427_0_, Random p_236427_1_, BlockPos.MutableBlockPos p_236427_2_, int p_236427_3_, int p_236427_4_, int p_236427_5_)
    {
        for(int i = 0; i <= p_236427_3_; ++i)
        {
            if (p_236427_0_.isEmptyBlock(p_236427_2_))
            {
                if (i == p_236427_3_ || !p_236427_0_.isEmptyBlock(p_236427_2_.below()))
                {
                    p_236427_0_.setBlock(p_236427_2_, BOPBlocks.SPANISH_MOSS.defaultBlockState().setValue(GrowingPlantHeadBlock.AGE, Integer.valueOf(Mth.nextInt(p_236427_1_, p_236427_4_, p_236427_5_))), 2);
                    break;
                }

                p_236427_0_.setBlock(p_236427_2_, BOPBlocks.SPANISH_MOSS_PLANT.defaultBlockState(), 2);
            }

            p_236427_2_.move(Direction.DOWN);
        }

    }
}

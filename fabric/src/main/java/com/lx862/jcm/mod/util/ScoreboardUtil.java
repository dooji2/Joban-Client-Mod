package com.lx862.jcm.mod.util;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.ScoreboardHelper;
import org.mtr.mapping.mapper.TextHelper;

public class ScoreboardUtil {
    public static ScoreboardObjective getOrCreateObjective(World world, String name, ScoreboardCriterion criterion, Text displayName, ScoreboardCriterionRenderType renderType) {
        if(!world.getScoreboard().getObjectiveNames().contains(name)) {
            world.getScoreboard().addObjective(name, criterion, displayName, renderType);
        }
        return ScoreboardHelper.getScoreboardObjective(world.getScoreboard(), name);
    }

    public static boolean incrementNonOverflow(ScoreboardPlayerScore score, int amount) {
        if((long)score.getScore() + amount > Integer.MAX_VALUE) {
            return false;
        } else {
            score.incrementScore(amount);
            return true;
        }
    }

    public static ScoreboardPlayerScore getPlayerMTRBalanceScore(World world, PlayerEntity player) {
        String playerName = player.getGameProfile().getName();
        ScoreboardObjective mtrScoreboard = getOrCreateObjective(world, "mtr_balance", ScoreboardCriterion.getDummyMapped(), Text.cast(TextHelper.literal("Balance")), ScoreboardCriterionRenderType.INTEGER);
        return world.getScoreboard().getPlayerScore(playerName, mtrScoreboard);
    }
}

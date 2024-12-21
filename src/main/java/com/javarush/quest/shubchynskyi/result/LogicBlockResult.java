package com.javarush.quest.shubchynskyi.result;

public record LogicBlockResult(
        int blockNumber,
        String blockData,
        String blockType
) {}
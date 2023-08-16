package com.javarush.quest.shubchynskyi.quest_util;

import org.springframework.stereotype.Component;

@Component
public class BlockTypeResolver {

    public enum BlockType {
        PLAY, WIN, LOST, ANSWER, UNKNOWN
    }

    public BlockType defineBlockType(String blockType) {
        for (QuestMarksEnum questMark : QuestMarksEnum.values()) {
            if (questMark.getMark().equals(blockType)) {
                return BlockType.valueOf(questMark.name());
            }
        }
        return BlockType.UNKNOWN;
    }
}

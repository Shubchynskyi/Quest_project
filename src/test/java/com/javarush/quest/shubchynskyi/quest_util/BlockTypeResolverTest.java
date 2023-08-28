package com.javarush.quest.shubchynskyi.quest_util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class BlockTypeResolverTest {

    private final BlockTypeResolver blockTypeResolver = new BlockTypeResolver();

    @ParameterizedTest
    @CsvSource({
            ":, PLAY",
            "<, ANSWER",
            "+, WIN",
            "-, LOST",
            "*, UNKNOWN"
    })
    public void should_ReturnCorrectBlockType_When_InputIsProvided(String input, BlockTypeResolver.BlockType expected) {
        assertEquals(expected, blockTypeResolver.defineBlockType(input));
    }
}
package com.example.nosql;

import com.example.nosql.community.document.PersonalBoardConfig;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

public class JavaTest {

    @Test
    void testSwitch(){
        List<String> list = List.of("a","b","c","d");

        int e = list.indexOf("e");

        Collections.swap(list, e, 0);

        System.out.println("e = " + e);
    }

    @Test
    void equalTest(){
        PersonalBoardConfig p1 = new PersonalBoardConfig(1L, List.of("a", "b"));
        PersonalBoardConfig p2 = new PersonalBoardConfig(1L, List.of("b", "a"));

        boolean equals = p1.equals(p2);

        System.out.println("equals = " + equals);
    }
}

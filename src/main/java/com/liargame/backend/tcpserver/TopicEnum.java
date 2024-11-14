package com.liargame.backend.tcpserver;

import java.util.Arrays;
import java.util.List;

public enum Topic {
    BUILDING("건국대학교 건물", Arrays.asList("공학관", "경영관", "상허기념도서관", "법학관", "새천년관", "신공학관", "건축관", "예디대", "박물관", "동물생명과학관")),
    LANDMARK("건국대학교 명소", Arrays.asList("청심대", "황소상", "일감호", "와우도", "박물관")),
    ANIMAL("건국대학교 동물", Arrays.asList("건구스", "건덕이", "쿠", "까마귀", "비둘기", "청설모"));

    private final String topic;
    private final List<String> words;

    Topic(String topic, List<String> words) {
        this.topic = topic;
        this.words = words;
    }
}

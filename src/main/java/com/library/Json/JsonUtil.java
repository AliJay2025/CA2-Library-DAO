package com.library.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.library.domain.Member;

import java.util.List;

public class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // Member to JSON
    public static String memberToJson(Member member) throws Exception {
        if (member == null) return "{}";
        return MAPPER.writeValueAsString(member);
    }

    // JSON to Member
    public static Member memberFromJson(String json) throws Exception {
        if (json == null || json.trim().isEmpty()) return null;
        return MAPPER.readValue(json, Member.class);
    }

    // List<Member> to JSON
    public static String memberListToJson(List<Member> members) throws Exception {
        if (members == null) return "[]";
        return MAPPER.writeValueAsString(members);
    }

    // JSON to List<Member>
    public static List<Member> memberListFromJson(String json) throws Exception {
        if (json == null || json.trim().isEmpty()) return null;
        return MAPPER.readValue(json, new TypeReference<List<Member>>() {});
    }
}
package com.mediinbusan.backend.diagnosischat.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Gemini structured output(responseSchema)용 최소 JSON Schema 노드. type/properties/items/enum/required만
 * 지원하면 되므로 record 대신 필드가 대부분 비어있는 클래스로 두고 static factory로 조립한다.
 * 여기서 enum 배열을 넣어도(1차 방어) 서버는 DiagnosisChatDtoMapper에서 다시 화이트리스트 검증한다(2차 방어) —
 * Gemini가 스키마를 어겨도 서버가 최종 방어선이다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class GeminiSchema {

    public String type;
    public Map<String, GeminiSchema> properties;
    public List<String> required;
    public GeminiSchema items;
    @JsonProperty("enum")
    public List<String> enumValues;
    public Boolean nullable;

    public static GeminiSchema object(Map<String, GeminiSchema> properties, List<String> required) {
        GeminiSchema schema = new GeminiSchema();
        schema.type = "OBJECT";
        schema.properties = properties;
        schema.required = required;
        return schema;
    }

    public static GeminiSchema string() {
        GeminiSchema schema = new GeminiSchema();
        schema.type = "STRING";
        return schema;
    }

    public static GeminiSchema nullableEnumString(List<String> enumValues) {
        GeminiSchema schema = new GeminiSchema();
        schema.type = "STRING";
        schema.enumValues = enumValues;
        schema.nullable = true;
        return schema;
    }

    public static GeminiSchema arrayOfEnumStrings(List<String> enumValues) {
        GeminiSchema items = new GeminiSchema();
        items.type = "STRING";
        items.enumValues = enumValues;

        GeminiSchema schema = new GeminiSchema();
        schema.type = "ARRAY";
        schema.items = items;
        return schema;
    }
}

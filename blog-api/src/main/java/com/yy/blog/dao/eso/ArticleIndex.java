package com.yy.blog.dao.eso;

public class ArticleIndex {
    public static final String MAPPING_TEMPLATE="\n" +
            "{\n" +
            "\t\"mappings\": {\n" +
            "\t  \"properties\": {\n" +
            "\t    \"id\":{\n" +
            "\t      \"type\":\"keyword\",\n" +
            "\t      \"index\": false\n" +
            "\t    },\n" +
            "\t    \"title\":{\n" +
            "\t      \"type\": \"text\",\n" +
            "\t      \"analyzer\": \"ik_max_word\"\n" +
            "\t    },\n" +
            "\t    \"summary\":{\n" +
            "\t      \"type\":\"text\",\n" +
            "\t      \"analyzer\": \"ik_max_word\"\n" +
            "\t    },\n" +
            "\t    \"create_date\":{\n" +
            "\t      \"type\":\"keyword\",\n" +
            "\t    },\n" +
            "\t    \"category_vo\":{\n" +
            "\t      \"type\": \"text\",\n" +
            "\t      \"analyzer\": \"ik_max_word\"\n" +
            "\t    },\n" +
            "\t    \"tag_vo\":{\n" +
            "\t      \"type\": \"text\",\n" +
            "\t      \"analyzer\": \"ik_max_word\"\n" +
            "\t    },\n" +
            "\t    \"user_vo\":{\n" +
            "\t      \"type\": \"text\",\n" +
            "\t      \"analyzer\": \"ik_smart\"\n" +
            "\t    },\n" +
            "\t    \"content\":{\n" +
            "\t      \"type\": \"text\",\n" +
            "\t      \"analyzer\": \"ik_smart\"\n" +
            "\t    }\n" +
            "\t  }\n" +
            "\t}\n" +
            "}";

    public static final String TAG_MAPPING_TEMPLATE="\n" +
            "{\n" +
            "  \"settings\": {\n" +
            "    \"analysis\": {\n" +
            "      \"analyzer\": { \n" +
            "        \n" +
            "        \"my_analyzer\": { \n" +
            "          \"tokenizer\": \"keyword\",\n" +
            "          \"filter\": \"py\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"filter\": {\n" +
            "        \"py\": { \n" +
            "          \"type\": \"pinyin\",\n" +
            "          \"keep_full_pinyin\": false,\n" +
            "          \"keep_joined_full_pinyin\": true,\n" +
            "          \"keep_original\": true,\n" +
            "          \"limit_first_letter_length\": 16,\n" +
            "          \"remove_duplicated_term\": true,\n" +
            "          \"none_chinese_pinyin_tokenize\": false\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"mappings\": {\n" +
            "    \"properties\":{\n" +
            "      \"id\":{\n" +
            "\t      \"type\":\"keyword\",\n" +
            "\t      \"index\": false\n" +
            "\t    },\n" +
            "      \"name\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"suggestion\": {\n" +
            "        \"type\":\"completion\",\n" +
            "        \"analyzer\":\"my_analyzer\"\n" +
            "      }  \n" +
            "    }\n" +
            "  }\n" +
            "}";
}

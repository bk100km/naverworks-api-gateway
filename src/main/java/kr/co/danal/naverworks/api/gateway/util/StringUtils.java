package kr.co.danal.naverworks.api.gateway.util;

public class StringUtils {

    /**
     * 주어진 문자열 배열을 하나의 문자열로 연결하는 메서드
     *
     * @param strings 연결할 문자열 배열
     * @return 연결된 문자열
     */
    public static String concat(String... strings) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : strings) {
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }
}
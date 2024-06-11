package kr.co.danal.naverworks.api.gateway.util;

public class MessageUtils {

    /**
     * 주어진 문자열을 '/'를 기준으로 스플릿.
     *
     * @param input 스플릿할 문자열
     * @return 첫번째 문자열 부분
     */
    public static String[] splitBySlash(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        return input.split("/");
    }

    /**
     * 주어진 문자열 배열에서 특정 위치 부분을 가져옴.
     *
     * @param input 문자열 배열
     * @return 특정 위치 부분 문자열
     */
    public static String getPartByLocation(String[] input, int location) {
        if (input == null) {
            return "";
        }
        return input.length > location ? input[location] : "";
    }
}
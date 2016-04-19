package com.mengcraft.account.lib;

public class StringUtil {

    public static final StringUtil DEF = new StringUtil();
    private static final char CHAR_SPLIT = 32;

    public String format(String in, Object... res) {
        StringBuilder builder = new StringBuilder();
        ArrayVector<?> it = new ArrayVector<>(res);
        int j = 0, i = in.indexOf('{');
        for (; i != -1; i = in.indexOf('{', j)) {
            if (in.length() > i + 2 &&
                    in.charAt(i + 1) == '}') {
                builder.append(in.substring(j, i));
                if (it.hasNext()) {
                    builder.append(it.next());
                }
                j = i + 2;
            } else {
                builder.append(in.substring(j, i + 1));
                j = i + 1;
            }
        }
        builder.append(in.substring(j, in.length()));
        return builder.toString();
    }

    private String[] split(String in, char c) {
        ArrayBuilder<String> ab = new ArrayBuilder<>();
        int x = 0, y;
        for (; (y = in.indexOf(c, x)) != -1;x = y + 1) {
            ab.append(in.substring(x, y));
        }
        ab.append(in.substring(x));
        return ab.build(String.class);
    }
    
    public String[] split(String in) {
    	return split(in, CHAR_SPLIT);
    }

}

package com.fangxiong.constants;

public class SystemConstants {
    public static final String TOKEN_KEY = "exchange:login:user:tokens:id:";
    public static final String EMAIL_KEY = "exchange:login:user:email:";

    public static final String DEFAULT_NAME_PREFIX = "exu_";
    public static final String SMS_CODE_TIMES_KEY = "exchange:sms:email:";

    public static final Integer SMS_CODE_TIMES_LIMIT = 3;

    public static final Long TOKEN_EXPIRE_TIME = (long) (1000 * 60 * 30);
    public static final Long CODE_EXPIRE_TIME = (long) (1000 * 60 * 5);
}

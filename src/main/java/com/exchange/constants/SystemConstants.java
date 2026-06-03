package com.exchange.constants;

public class SystemConstants {
    public static final String TOKEN_KEY = "exchange:login:user:tokens:id:";
    public static final String EMAIL_KEY = "exchange:login:user:email:";
    public static final String ACCOUNT_BANDED_KEY = "exchange:login:user:banded:id:";

    public static final String DEFAULT_NAME_PREFIX = "exu_";
    public static final String SMS_CODE_TIMES_KEY = "exchange:sms:email:";

    public static final String SCHOOL_CHANGE_KEY = "exchange:change:school:id:";
    public static final String REQUEST_INFO_CHANGE_KEY = "exchange:change:info:id:";
    public static final String REQUEST_INFO_CHANGE_IGNORED_KEY = "exchange:change:info:ignored:id:";
    public static final String REQUEST_INFO_CHANGE_SUCCESS_KEY = "exchange:change:info:success:id:";
    public static final String REQUEST_INFO_CHANGE_REJECTED_KEY = "exchange:change:info:rejected:id:";


    public static final String USER_POST_PREFERENCE_TYPES_KEY = "exchange:user:preference:post:types:id:";

    public static final Integer SMS_CODE_TIMES_LIMIT = 3;

    public static final Long TOKEN_EXPIRE_TIME = 1000L * 60 * 30;
    public static final Long USER_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7;
    public static final Long CODE_EXPIRE_TIME = 1000L * 60 * 5;
    public static final Long CHANGE_SCHOOL_LIMIT_TIME = 1000L * 60 * 60 * 24 * 30;
    public static final Long CHANGE_INFO_ADMIN_AUDIT_LIMIT_TIME = 1000L * 60 * 60 * 24;
}

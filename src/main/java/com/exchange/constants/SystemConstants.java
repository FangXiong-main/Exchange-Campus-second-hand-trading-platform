package com.exchange.constants;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SystemConstants {
    public static final String TOKEN_KEY = "exchange:login:user:tokens:id:";
    public static final String EMAIL_KEY = "exchange:login:user:email:";
    public static final String ACCOUNT_BANDED_KEY = "exchange:login:user:banded:id:";

    public static final String DEFAULT_NAME_PREFIX = "exc_";
    public static final String SMS_CODE_TIMES_KEY = "exchange:sms:email:";

    public static final String SCHOOL_CHANGE_KEY = "exchange:change:school:id:";
    public static final String REQUEST_INFO_CHANGE_KEY = "exchange:change:info:id:";
    public static final String REQUEST_INFO_CHANGE_IGNORED_KEY = "exchange:change:info:ignored:id:";
    public static final String REQUEST_INFO_CHANGE_SUCCESS_KEY = "exchange:change:info:success:id:";
    public static final String REQUEST_INFO_CHANGE_REJECTED_KEY = "exchange:change:info:rejected:id:";


    public static final String USER_POST_PREFERENCE_TYPES_KEY = "exchange:user:preference:post:types:id:";

    public static final String USER_PURCHASE_GOODS_LOCK_KEY = "exchange:user:purchase:goods:lock:id:";
    public static final String USER_FIANCE_OPERATION_LOCK_KEY = "exchange:user:finance:operation:lock:id:";
    public static final String USER_CONFIRM_ORDER_LOCK_KEY = "exchange:user:confirm:order:lock:id:";
    public static final String USER_CANCEL_ORDER_LOCK_KEY = "exchange:user:confirm:order:cancel:lock:id:";
    public static final String USER_DELETE_ORDER_OR_GOODS_LOCK_KEY = "exchange:user:delete:orderOrGoods:lock:id:";
    public static final String ADMIN_ORDER_DRAWBACK_LOCK_KEY = "exchange:admin:order:drawback:lock:id:";

    public static final String EXCHANGE_ORDER_INCR_ID_KEY_PREFIX = "exchange:order:icr:id:";
    public static final String EXCHANGE_FILE_INCR_ID_KEY_PREFIX = "exchange:file:icr:id:";

    public static final String EXCHANGE_UUID_TIME_KEY_FORMAT = "yyyy:MM";

    public static final String EXCHANGE_DEFAULT_AVATAR_URL = "/ExchangeUploads/default-images/default-avatar.jpg";

    public static final Long EXCHANGE_OFFICIAL_ID = 1L;

    public static final BigDecimal EXCHANGE_DEDUCTION_RATE = BigDecimal.valueOf(0.01);
    public static final Integer SMS_CODE_TIMES_LIMIT = 3;
    public static final LocalDateTime EXCHANGE_ORDER_START_TIME = LocalDateTime.now();
    public static final Integer EXCHANGE_ORDER_ID_TIMESTAMP_LENGTH = 23;
    public static final Integer EXCHANGE_ORDER_ID_MACHINE_CODE_LENGTH = 8;
    public static final Integer EXCHANGE_ORDER_ID_SEQUENCE_LENGTH = 32;
    public static final Integer EXCHANGE_FILE_ID_TIMESTAMP_LENGTH = 20;
    public static final Integer EXCHANGE_FILE_ID_MACHINE_CODE_LENGTH = 8;
    public static final Integer EXCHANGE_FILE_ID_SEQUENCE_LENGTH = 35;
    public static final Long EXCHANGE_MACHINE_CODE = 20000828L;

    public static final Long TOKEN_EXPIRE_TIME = 1000L * 60 * 30;
    public static final Long USER_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 15;
    public static final Long CODE_EXPIRE_TIME = 1000L * 60 * 5;
    public static final Long CHANGE_SCHOOL_LIMIT_TIME = 1000L * 60 * 60 * 24 * 30;
    public static final Long CHANGE_INFO_ADMIN_AUDIT_LIMIT_TIME = 1000L * 60 * 60 * 24;
}

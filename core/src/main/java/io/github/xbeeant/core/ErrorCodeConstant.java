package io.github.xbeeant.core;

/**
 * error code constant
 *
 * @author xiaobiao
 * @version 2020/2/2
 */
public class ErrorCodeConstant {
    /**
     * 服务异常
     */
    public static final int SERVICE_INVALID = 9999;
    /**
     * 服务异常
     */
    public static final String SERVICE_INVALID_MSG = "服务异常";
    /**
     * API调用异常
     */
    public static final int API_INVALID = 1000;
    /**
     * API调用异常
     */
    public static final String API_INVALID_MSG = "API调用异常";
    /**
     * API请求受限
     */
    public static final int API_LIMIT = -100;
    /**
     * API请求受限
     */
    public static final String API_LIMIT_MSG = "API请求次数受限";
    /**
     * 满足条件的数据条数大于1条
     */
    public static final int CONFLICT = 105;
    /**
     * 满足条件的数据条数大于1条
     */
    public static final String CONFLICT_MSG = "满足条件的数据条数大于1条";
    /**
     * 删除失败
     */
    public static final int DELETE_NONE = 103;
    /**
     * 删除失败
     */
    public static final String DELETE_NONE_MSG = "删除失败";
    /**
     * 写入失败
     */
    public static final int INSERT_NONE = 101;
    /**
     * 写入失败
     */
    public static final String INSERT_NONE_MSG = "写入失败";
    /**
     * 含有不能为空的参数
     */
    public static final int NONEMPTY_PARAM = 100;
    /**
     * 含有不能为空的参数
     */
    public static final String NONEMPTY_PARAM_MSG = "含有不能为空的参数";
    /**
     * 没有匹配项
     */
    public static final int NO_MATCH = 104;
    /**
     * 没有匹配项
     */
    public static final String NO_MATCH_MSG = "没有匹配项";
    /**
     * 更新失败
     */
    public static final int UPDATE_NONE = 102;
    /**
     *
     */
    public static final String UPDATE_NONE_MSG = "更新失败";

    private ErrorCodeConstant() {
    }
}

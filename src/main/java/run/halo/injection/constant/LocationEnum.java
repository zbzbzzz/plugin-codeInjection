package run.halo.injection.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Zbzbzzz
 * @description 位置枚举
 */
@Getter
@AllArgsConstructor
public enum LocationEnum {
    HEAD("head", 0),
    FOOTER("footer", 1);
    private final String name;
    private final Integer index;
}

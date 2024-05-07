package run.halo.injection.bean;

import lombok.Data;

/**
 * @author Zbzbzzz
 * @description
 */
@Data
public class InjectionQuery {
    private Integer page;
    private Integer size;
    private String keyword;
    private Integer location;
    private String rule;
    private Boolean enable;
}

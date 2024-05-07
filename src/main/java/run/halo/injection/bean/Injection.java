package run.halo.injection.bean;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * @author Zbzbzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
@GVK(kind = "Injection", group = "injection.halo.run",
    version = "v1alpha1", singular = "injection", plural = "injections")
public class Injection extends AbstractExtension {
    @Schema(requiredMode = REQUIRED)
    private InjectionSpec spec;

    @Data
    public static class InjectionSpec {

        @Schema(requiredMode = REQUIRED, minLength = 1)
        private String name;

        @Schema(requiredMode = REQUIRED)
        private String desc;

        @Schema(requiredMode = REQUIRED)
        private String content;

        @Schema(requiredMode = REQUIRED)
        private Integer location;

        @Schema(requiredMode = REQUIRED)
        private String rule;

        @Schema(requiredMode = REQUIRED)
        private Integer order;

        @Schema(requiredMode = REQUIRED, defaultValue = "false")
        private Boolean enable;

    }
}

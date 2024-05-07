package run.halo.injection.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;
import run.halo.app.plugin.ApiVersion;
import run.halo.injection.bean.Injection;
import run.halo.injection.bean.InjectionQuery;
import run.halo.injection.service.InjectionService;

/**
 * @author Zbzbzzz
 * @description 代码插入接口类
 */

@ApiVersion("injection.halo.run/v1alpha1")
@RestController
@RequestMapping("/injection")
@RequiredArgsConstructor
public class InjectionController {
    private final InjectionService injectionService;
    @GetMapping("/list")
    public Mono<ListResult<Injection>> list(@ModelAttribute InjectionQuery injectionQuery) {
        return injectionService.list(injectionQuery);
    }
}

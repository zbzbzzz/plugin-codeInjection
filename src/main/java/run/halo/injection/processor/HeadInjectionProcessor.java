package run.halo.injection.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import run.halo.app.theme.dialect.TemplateHeadProcessor;
import run.halo.injection.constant.LocationEnum;
import run.halo.injection.service.InjectionService;

/**
 * @author Zbzbzzz
 * @description 页面头部代码插入
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 3)
@Component
@RequiredArgsConstructor
@Slf4j
public class HeadInjectionProcessor implements TemplateHeadProcessor {
    private final InjectionService injectionService;

    @Override
    public Mono<Void> process(ITemplateContext context, IModel model,
        IElementModelStructureHandler structureHandler) {
        final IModelFactory modelFactory = context.getModelFactory();
        WebEngineContext webEngineContext;
        if (context instanceof WebEngineContext) {
            webEngineContext = (WebEngineContext) context;
            String requestURL = webEngineContext.getExchange().getRequest().getRequestURL();
            injectionService.listAll(LocationEnum.HEAD)
                .filter(item -> requestURL.matches(item.getSpec().getRule()))
                .subscribeOn(Schedulers.immediate())
                .subscribe(item -> {
                    model.add(modelFactory.createText(item.getSpec().getContent() + "\n"));
                });
        }
        return Mono.empty();
    }
}

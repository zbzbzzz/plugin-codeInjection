package run.halo.injection.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.injection.bean.Injection;
import run.halo.injection.bean.InjectionQuery;
import run.halo.injection.constant.LocationEnum;
import run.halo.injection.service.InjectionService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Zbzbzzz
 * @description 代码插入业务类
 */
@Service
@RequiredArgsConstructor
public class InjectionServiceImpl implements InjectionService {
    private final ReactiveExtensionClient client;

    @Override
    public Mono<ListResult<Injection>> list(InjectionQuery injectionQuery) {
        List<Comparator<Injection>> comparators = new ArrayList<>();
        comparators.add(Comparator.comparing(e -> e.getSpec().getOrder()));
        comparators.add(Comparator.comparing(e -> e.getMetadata().getCreationTimestamp()));
        Comparator<Injection> injectionComparator = comparators.stream()
            .reduce(Comparator::thenComparing)
            .orElse(null);

        Predicate<Injection> keywordPredicate = injection -> {
            var keyword = injectionQuery.getKeyword();
            if (StringUtils.isBlank(keyword)) {
                return true;
            }
            String keywordToSearch = keyword.trim().toLowerCase();
            return StringUtils.containsAnyIgnoreCase(injection.getSpec().getName(),
                keywordToSearch)
                || StringUtils.containsAnyIgnoreCase(injection.getSpec().getDesc(),
                keywordToSearch);
        };

        Predicate<Injection> locationPredicate = injection -> {
            var location = injectionQuery.getLocation();
            if (Objects.isNull(location)) {
                return true;
            }
            return injection.getSpec().getLocation().equals(location);
        };

        Predicate<Injection> rulePredicate = injection -> {
            var rule = injectionQuery.getRule();
            if (StringUtils.isBlank(rule)) {
                return true;
            }
            return rule.matches(injection.getSpec().getRule());
        };

        Predicate<Injection> enablePredicate = injection -> {
            var enable = injectionQuery.getEnable();
            if (Objects.isNull(enable)) {
                return true;
            }
            return injection.getSpec().getEnable() == enable;
        };

        return client.list(Injection.class,
            keywordPredicate
                .and(locationPredicate)
                .and(rulePredicate)
                .and(enablePredicate),
            injectionComparator,
            injectionQuery.getPage(),
            injectionQuery.getSize());
    }

    @Override
    public Flux<Injection> listAll(LocationEnum locationEnum) {
        return client.list(Injection.class,
            injection -> injection.getSpec().getEnable()&&injection.getSpec().getLocation().equals(locationEnum.getIndex()),
            (a, b) -> b.getSpec().getOrder()-a.getSpec().getOrder()
        );
    }
}

package run.halo.injection.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;
import run.halo.injection.bean.Injection;
import run.halo.injection.bean.InjectionQuery;
import run.halo.injection.constant.LocationEnum;

/**
 * @author Zbzbzzz
 * @description 代码插入业务类
 */
public interface InjectionService {
    Mono<ListResult<Injection>> list(InjectionQuery injectionQuery);
    Flux<Injection> listAll(LocationEnum locationEnum);
}

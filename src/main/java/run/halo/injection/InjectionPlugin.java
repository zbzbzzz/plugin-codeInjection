package run.halo.injection;

import org.springframework.stereotype.Component;
import run.halo.app.extension.Scheme;
import run.halo.app.extension.SchemeManager;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;
import run.halo.injection.bean.Injection;

/**
 * @author Zbzbzzz
 * @description 插件生命周期
 */
@Component
public class InjectionPlugin extends BasePlugin {
    private final SchemeManager schemeManager;

    public InjectionPlugin(PluginContext pluginContext, SchemeManager schemeManager) {
        super(pluginContext);
        this.schemeManager = schemeManager;
    }

    @Override
    public void start() {
        schemeManager.register(Injection.class);
    }

    @Override
    public void stop() {
        Scheme codeInjectionScheme = schemeManager.get(Injection.class);
        schemeManager.unregister(codeInjectionScheme);
    }
}

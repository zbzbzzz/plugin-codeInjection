import { definePlugin } from "@halo-dev/console-shared";
import InjectionList from "./views/InjectionList.vue";
import { IconCodeBoxLine } from "@halo-dev/components";
import { markRaw } from "vue";

export default definePlugin({
  components: {},
  routes: [
    {
      parentName: "Root",
      route: {
        path: "/injection",
        name: "Injection",
        component: InjectionList,
        meta: {
          title: "代码插入",
          searchable: true,
          menu: {
            name: "代码插入",
            group: "外观",
            icon: markRaw(IconCodeBoxLine),
            priority: 0,
          },
        },
      },
    },
  ],
  extensionPoints: {},
});

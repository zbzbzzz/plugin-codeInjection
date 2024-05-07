<script setup lang="ts">
import {computed} from "vue";
import {
  IconAddCircle,
  IconCodeBoxLine,
  IconRefreshLine,
  VButton,
  VPageHeader,
  VSpace,
  VCard,
  VEntity,
  VEntityField,
  VPagination,
  VLoading,
  VEmpty,
  VDropdownItem,
  Toast,
  Dialog,
  VSwitch
} from "@halo-dev/components";
import {useInjectionFetch} from "@/composables/injection";
import InjectionEditModal from "@/components/InjectionEditModal.vue";
import {ref} from "vue";
import {formatDatetime} from "@/utils/date";
import type {Injection} from "@/types";
import {useQueryClient} from "@tanstack/vue-query";
import apiClient from "@/utils/api-client";
import cloneDeep from "lodash.clonedeep";

const queryClient = useQueryClient();


const page = ref<number>(1);
const size = ref<number>(20);
const keyword = ref<string | undefined>("");
const enable = ref<boolean | undefined>(undefined);
const location = ref<number | undefined>(undefined);
const rule = ref<string | undefined>(undefined);
const editModal = ref<boolean>(false);


const selectedInjectionNames = ref<string[]>([]);
const checkedAll = ref<boolean>(false);

const {injections, isLoading, refetch, total, isFetching} = useInjectionFetch(
  page,
  size,
  keyword,
  location,
  rule,
  enable
);

const hasFilters = computed(() => {
  return keyword.value || enable.value;
});

const handleCheckAllChange = (e: Event) => {
  const {checked} = e.target as HTMLInputElement;

  if (checked) {
    selectedInjectionNames.value = injections.value?.map((injection) => {
      return injection.metadata.name;
    }) || [];
  } else {
    selectedInjectionNames.value.length = 0;
  }
};


const handleDeleteInBatch = () => {
  Dialog.warning({
    title: "是否确认删除所选的代码片段？",
    description: "删除之后将无法恢复。",
    confirmType: "danger",
    onConfirm: async () => {
      try {
        const promises = selectedInjectionNames.value.map((name) => {
          return apiClient.delete(`/apis/injection.halo.run/v1alpha1/injections/${name}`);
        });
        if (promises) {
          await Promise.all(promises);
        }

        selectedInjectionNames.value.length = 0;
        checkedAll.value = false;

        Toast.success("删除成功");
      } catch (e) {
        console.error(e);
      } finally {
        await queryClient.invalidateQueries({queryKey: ["injections"]});
      }
    },
  });
};

const handleClearFilters = () => {
  keyword.value = "";
  enable.value = undefined;
}

const selectedInjection = ref<Injection | undefined>();
const handleOpenEditModal = (injection: Injection) => {
  selectedInjection.value = injection;
  editModal.value = true;
};

const handleDelete = (injection: Injection) => {
  Dialog.warning({
    title: "是否确认删除当前的代码片段？",
    description: "删除之后将无法恢复。",
    confirmType: "danger",
    onConfirm: async () => {
      try {
        await apiClient.delete(
          `/apis/injection.halo.run/v1alpha1/injections/${injection.metadata.name}`
        );

        Toast.success("删除成功");
      } catch (e) {
        console.error(e);
      } finally {
        await queryClient.invalidateQueries({queryKey: ["injections"]});
      }
    },
  });
};

const onEditModalClose = () => {
  selectedInjection.value = undefined;
  refetch();
}

const changingStatus = ref<boolean>(false);
const handleChangeStatus = async (injection: Injection) => {
  try {
    let clone = cloneDeep(injection);
    clone.spec.enable = !clone.spec.enable;
    await apiClient.put<Injection>(
      `/apis/injection.halo.run/v1alpha1/injections/${clone.metadata.name}`, clone
    );
    Toast.success("状态改变成功");
  } catch (e) {
    console.error(e);
  } finally {
    await queryClient.invalidateQueries({queryKey: ["injections"]});
  }
};

const transformLocation=(index:number)=>{
  const map:string[] = ['head','footer'];
  return map[index];
}
</script>

<template>
  <InjectionEditModal
    v-model:visible="editModal"
    :injection="selectedInjection"
    @close="onEditModalClose"
  ></InjectionEditModal>
  <VPageHeader title="代码插入">
    <template #icon>
      <IconCodeBoxLine class="mr-2 self-center"/>
    </template>
    <template #actions>
      <VSpace>
        <VButton
          type="secondary"
          @click="editModal=true"
        >
          <template #icon>
            <IconAddCircle class="h-full w-full"/>
          </template>
          新建代码片段
        </VButton>
      </VSpace>
    </template>
  </VPageHeader>

  <div class="m-0 md:m-4">
    <VCard :body-class="['!p-0']">
      <template #header>
        <div class="block w-full bg-gray-50 px-4 py-3">
          <div
            class="relative flex flex-col flex-wrap items-start gap-4 sm:flex-row sm:items-center"
          >
            <div
              class="hidden items-center sm:flex"
            >
              <input
                v-model="checkedAll"
                type="checkbox"
                @change="handleCheckAllChange"
              />
            </div>
            <div class="flex w-full flex-1 items-center sm:w-auto">
              <VSpace v-if="!selectedInjectionNames.length" spacing="lg" class="flex-wrap">
                <SearchInput v-model="keyword"/>
                <SearchInput placeholder="输入需要匹配的链接" v-model="rule"/>
              </VSpace>
              <VSpace v-else>
                <VButton type="danger" @click="handleDeleteInBatch">
                  删除
                </VButton>
              </VSpace>
            </div>
            <VSpace spacing="lg" class="flex-wrap">
              <FilterCleanButton
                v-if="hasFilters"
                @click="handleClearFilters"
              />
              <FilterDropdown
                v-model="location"
                label="位置"
                :items="[
                  {
                    label: '全部'
                  },
                  {
                    label: 'head',
                    value: 0
                  },
                  {
                    label: 'footer',
                    value: 1
                  }
                  ]"
              />
              <FilterDropdown
                v-model="enable"
                label="状态"
                :items="[
                  {
                    label: '全部'
                  },
                  {
                    label: '启用',
                    value: true
                  },
                  {
                    label: '未启用',
                    value: false
                  }
                  ]"
              />

              <div class="flex flex-row gap-2">
                <div
                  class="group cursor-pointer rounded p-1 hover:bg-gray-200"
                  @click="refetch()"
                >
                  <IconRefreshLine
                    tooltip="刷新"
                    :class="{
                      'animate-spin text-gray-900': isFetching,
                    }"
                    class="h-4 w-4 text-gray-600 group-hover:text-gray-900"
                  />
                </div>
              </div>
            </VSpace>
          </div>
        </div>
      </template>

      <VLoading v-if="isLoading"/>

      <Transition v-else-if="!injections?.length" appear name="fade">
        <VEmpty
          message="你可以尝试刷新或者修改筛选条件"
          title="当前没有代码片段"
        >
          <template #actions>
            <VSpace>
              <VButton @click="refetch()">
                刷新
              </VButton>
            </VSpace>
          </template>
        </VEmpty>
      </Transition>

      <Transition v-else appear name="fade">
        <ul
          class="box-border h-full w-full divide-y divide-gray-100"
          role="list"
        >
          <li v-for="(injection, index) in injections" :key="index">
            <VEntity :is-selected="selectedInjectionNames.includes(injection.metadata.name)">
              <template
                #checkbox
              >
                <input
                  v-model="selectedInjectionNames"
                  :value="injection.metadata.name"
                  name="post-checkbox"
                  type="checkbox"
                />
              </template>
              <template #start>
                <VEntityField
                  :title="injection.spec.name"
                  :description="injection.spec.desc"
                />
                <VEntityField
                  :description="injection.spec.content"
                />

              </template>
              <template #end>
                <VEntityField
                  title="位置"
                  :description="transformLocation(injection.spec.location)"
                />
                <VEntityField
                  title="匹配规则"
                  :description="injection.spec.rule"
                />
                <VEntityField
                  title="优先级"
                  :description="injection.spec.order.toString()"
                />
                <VEntityField
                  :description="
                          formatDatetime(injection.metadata.creationTimestamp)
                        "
                />
                <VEntityField>
                  <template #description>
                    <div class="flex items-center">
                      <VSwitch
                        :model-value="injection.spec.enable"
                        @click="handleChangeStatus(injection)"
                      />
                    </div>
                  </template>
                </VEntityField>
              </template>
              <template #dropdownItems>
                <VDropdownItem @click="handleOpenEditModal(injection)">
                  编辑
                </VDropdownItem>
                <VDropdownItem type="danger" @click="handleDelete(injection)">
                  删除
                </VDropdownItem>
              </template>
            </VEntity>
          </li>
        </ul>
      </Transition>

      <template #footer>
        <VPagination
          v-model:page="page"
          v-model:size="size"
          :total="total"
          page-label="页"
          size-label="条"
          :total-label="`共${total}项数据`"
          :size-options="[20, 30, 50, 100]"
        />
      </template>
    </VCard>
  </div>
</template>

<style lang="scss" scoped>

</style>

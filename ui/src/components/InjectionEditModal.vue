<script lang="ts" setup>
import {Toast, VButton, VModal, VSpace} from "@halo-dev/components";
import {ref, computed, watch} from "vue";
import cloneDeep from "lodash.clonedeep";
import apiClient from "@/utils/api-client";
import type {Injection} from "@/types"


const saving = ref<boolean>(false);
const isUpdateMode = computed(() => {
  return !!formState.value.metadata.creationTimestamp;
});

const modalTitle = computed(() => {
  return isUpdateMode.value ? "编辑代码片段" : "新建代码片段";
});


const initialFormState: Injection = {
  metadata: {
    name: "",
    generateName: "injection-",
  },
  spec: {
    name: "",
    desc: "",
    content: "",
    location: null,
    rule: "",
    order: 0,
    enable: false,
  },
  kind: 'Injection',
  apiVersion: 'injection.halo.run/v1alpha1'
};

const formState = ref<Injection>(cloneDeep(initialFormState));

const formVisible = ref(false);
const handleResetForm = () => {
  formState.value = cloneDeep(initialFormState);
};


const props = withDefaults(
  defineProps<{
    visible: boolean;
    injection?: Injection;
  }>(),
  {
    visible: false,
  }
);

watch(
  () => props.visible,
  (visible) => {
    if (visible) {
      formVisible.value = true;
    } else {
      setTimeout(() => {
        formVisible.value = false;
        handleResetForm();
      }, 200);
    }
  }
);

watch(
  () => props.injection,
  (injection) => {
    if (injection) {
      formState.value = cloneDeep(injection);
    }
  }
);

const isFooter = computed(() => formState.value.spec.location === 1);
watch(
  () => isFooter.value,
  (value, oldValue) => {
    if (value === oldValue) {
      return;
    }
    if (value) {
      formState.value.spec.rule = ".*";
    } else {
      formState.value.spec.rule = "";
    }
  }, {
    deep: true
  }
);

const emit = defineEmits<{
  (event: "update:visible", visible: boolean): void;
  (event: "close"): void;
}>();

const onVisibleChange = (visible: boolean) => {
  emit("update:visible", visible);
  if (!visible) {
    emit("close");
  }
};

const handleSaveInjection = async () => {
  try {
    saving.value = true;
    if (isUpdateMode.value) {
      await apiClient.put<Injection>(
        `/apis/injection.halo.run/v1alpha1/injections/${formState.value.metadata.name}`,
        formState.value
      );
    } else {

      await apiClient.post<Injection>(
        `/apis/injection.halo.run/v1alpha1/injections`,
        formState.value
      );
    }

    Toast.success("保存成功");

    onVisibleChange(false);
  } catch (e) {
    console.error(e);
  } finally {
    saving.value = false;
  }
};

const is_valid_regex = ({value}) => {
  try {
    new RegExp(value);
    return true;
  } catch (e) {
    return false;
  }
};

</script>
<template>
  <VModal
    :title="modalTitle"
    :visible="visible"
    :width="650"
    @update:visible="onVisibleChange"
  >
    <FormKit
      v-if="formVisible"
      id="injection-form"
      name="injection-form"
      :config="{ validationVisibility: 'submit' }"
      type="form"
      v-model="formState.spec"
      @submit="handleSaveInjection"
    >
      <FormKit
        validation="required"
        label="名称"
        type="text"
        name="name"
      ></FormKit>
      <FormKit
        validation="required"
        label="描述"
        type="textarea"
        name="desc"
      ></FormKit>
      <FormKit
        validation="required"
        label="片段内容"
        type="textarea"
        name="content"
      ></FormKit>
      <FormKit
        validation="required"
        label="页面位置"
        placeholder="选择页面位置"
        type="select"
        name="location"
        help="footer位置默认匹配所有页面"
        :options="[
            { label: 'head', value: 0 },
            { label: 'footer', value: 1 }
      ]"
      >
      </FormKit>
      <FormKit
        :disabled="isFooter"
        validation="required|is_valid_regex"
        :validation-rules="{ is_valid_regex }"
        :validation-messages="{
      is_valid_regex: '不符合正则表达式',
    }"
        label="正则匹配规则"
        type="text"
        name="rule"
      ></FormKit>
      <FormKit
        validation="required"
        label="优先级"
        type="number"
        number="integer"
        name="order"
      ></FormKit>
    </FormKit>
    <template #footer>
      <VSpace>
        <VButton
          :loading="saving"
          type="secondary"
          @click="$formkit.submit('injection-form')"
        >
          保存
        </VButton>
        <VButton @click="onVisibleChange(false)">
          取消
        </VButton>
      </VSpace>
    </template>
  </VModal>
</template>

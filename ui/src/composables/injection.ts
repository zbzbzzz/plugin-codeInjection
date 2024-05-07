import apiClient from "@/utils/api-client";
import {useQuery} from "@tanstack/vue-query";
import {ref, type Ref} from "vue";
import type {InjectionList} from "@/types";

export function useInjectionFetch(
  page: Ref<number>,
  size: Ref<number>,
  keyword?: Ref<string>,
  location?: Ref<number | undefined>,
  rule?: Ref<string | undefined>,
  enable?: Ref<boolean | undefined>
) {
  const total = ref(0);

  const {
    data: injections,
    isLoading,
    isFetching,
    refetch,
  } = useQuery({
    queryKey: ["injections", page, size, keyword, location, rule, enable],
    queryFn: async () => {
      const {data} = await apiClient.get<InjectionList>(
        "/apis/injection.halo.run/v1alpha1/injection/list",
        {
          params: {
            page: page.value,
            size: size.value,
            keyword: keyword?.value,
            location: location?.value,
            rule: rule?.value,
            enable: enable?.value
          },
        }
      );
      total.value = data.total;
      return data.items;
    },
    refetchOnWindowFocus: false,
    refetchInterval(data) {
      const deletingInjection = data?.filter(
        (injection) => !!injection.metadata.deletionTimestamp
      );
      return deletingInjection?.length ? 1000 : false;
    },
  });

  return {
    injections,
    isLoading,
    refetch,
    total,
    isFetching,
  };
}

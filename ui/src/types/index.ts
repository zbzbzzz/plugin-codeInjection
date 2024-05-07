

export interface Metadata {
    name: string;
    labels?: {
        [key: string]: string;
    } | null;
    annotations?: {
        [key: string]: string;
    } | null;
    version?: number | null;
    generateName?:string;
    creationTimestamp?: string | null;
    deletionTimestamp?: string | null;
}

export interface InjectionSpec {
    name: string;
    desc: string;
    content: string;
    location: number|null;
    rule: string;
    order: number;
    enable:boolean;
}

export interface Injection {
    spec: InjectionSpec;
    apiVersion: "injection.halo.run/v1alpha1"; 
    kind: "Injection";
    metadata: Metadata;
}

export interface InjectionList {
    page: number;
    size: number;
    total: number;
    items: Array<Injection>;
    first: boolean;
    last: boolean;
    hasNext: boolean;
    hasPrevious: boolean;
    totalPages: number;
}

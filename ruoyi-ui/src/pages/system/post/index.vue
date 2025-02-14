<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(4em + 12px)">
        <t-form-item label="岗位编码" name="postCode">
          <t-input
            v-model="queryParams.postCode"
            placeholder="请输入岗位编码"
            clearable
            style="width: 200px"
            @enter="handleQuery"
          />
        </t-form-item>
        <t-form-item label="岗位名称" name="postName">
          <t-input
            v-model="queryParams.postName"
            placeholder="请输入岗位名称"
            clearable
            style="width: 200px"
            @enter="handleQuery"
          />
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-select v-model="queryParams.status" placeholder="岗位状态" clearable style="width: 200px">
            <t-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
          </t-select>
        </t-form-item>
        <t-form-item label-width="0px">
          <t-button theme="primary" @click="handleQuery">
            <template #icon> <search-icon /></template>
            搜索
          </t-button>
          <t-button theme="default" @click="resetQuery">
            <template #icon> <refresh-icon /></template>
            重置
          </t-button>
        </t-form-item>
      </t-form>

      <t-table
        v-model:column-controller-visible="columnControllerVisible"
        :loading="loading"
        hover
        row-key="postId"
        :data="postList"
        :columns="columns"
        :selected-row-keys="ids"
        select-on-row-click
        :pagination="pagination"
        :column-controller="{
          hideTriggerButton: true,
        }"
        :sort="sort"
        show-sort-column-bg-color
        @sort-change="handleSortChange"
        @select-change="handleSelectionChange"
      >
        <template #topContent>
          <t-row>
            <t-col flex="auto">
              <t-button v-hasPermi="['system:post:add']" theme="primary" @click="handleAdd">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button
                v-hasPermi="['system:post:edit']"
                theme="default"
                variant="outline"
                :disabled="single"
                @click="handleUpdate()"
              >
                <template #icon> <edit-icon /> </template>
                修改
              </t-button>
              <t-button
                v-hasPermi="['system:post:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="handleDelete()"
              >
                <template #icon> <delete-icon /> </template>
                删除
              </t-button>
              <t-button v-hasPermi="['system:post:export']" theme="default" variant="outline" @click="handleExport">
                <template #icon> <download-icon /> </template>
                导出
              </t-button>
            </t-col>
            <t-col flex="none">
              <t-button theme="default" shape="square" variant="outline" @click="showSearch = !showSearch">
                <template #icon> <search-icon /> </template>
              </t-button>
              <t-button theme="default" variant="outline" @click="columnControllerVisible = true">
                <template #icon> <setting-icon /> </template>
                列配置
              </t-button>
            </t-col>
          </t-row>
        </template>
        <template #status="{ row }">
          <dict-tag :options="sys_normal_disable" :value="row.status" />
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <t-link v-hasPermi="['system:post:query']" theme="primary" hover="color" @click.stop="handleDetail(row)">
              <browse-icon />详情
            </t-link>
            <t-link v-hasPermi="['system:post:edit']" theme="primary" hover="color" @click.stop="handleUpdate(row)">
              <edit-icon />修改
            </t-link>
            <t-link v-hasPermi="['system:post:remove']" theme="danger" hover="color" @click.stop="handleDelete(row)">
              <delete-icon />删除
            </t-link>
          </t-space>
        </template>
      </t-table>
    </t-space>

    <!-- 添加或修改岗位信息对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      width="500px"
      attach="body"
      @confirm="onConfirm"
    >
      <t-loading :loading="eLoading">
        <t-form
          ref="postRef"
          label-align="right"
          :data="form"
          :rules="rules"
          label-width="calc(4em + 41px)"
          @submit="submitForm"
        >
          <t-form-item label="岗位名称" name="postName">
            <t-input v-model="form.postName" placeholder="请输入岗位名称" />
          </t-form-item>
          <t-form-item label="岗位编码" name="postCode">
            <t-input v-model="form.postCode" placeholder="请输入编码名称" />
          </t-form-item>
          <t-form-item label="岗位顺序" name="postSort">
            <t-input-number v-model="form.postSort" :min="0" />
          </t-form-item>
          <t-form-item label="岗位状态" name="status">
            <t-radio-group v-model="form.status">
              <t-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{
                dict.label
              }}</t-radio>
            </t-radio-group>
          </t-form-item>
          <t-form-item label="备注" name="remark">
            <t-textarea v-model="form.remark" placeholder="请输入备注" />
          </t-form-item>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 岗位信息详情 -->
    <t-dialog
      v-model:visible="openView"
      header="岗位信息详情"
      placement="center"
      width="600px"
      attach="body"
      :footer="false"
    >
      <t-loading :loading="openViewLoading">
        <t-form label-align="right" colon label-width="calc(4em + 28px)">
          <t-row :gutter="[0, 20]">
            <t-col :span="6">
              <t-form-item label="岗位ID">{{ form.postId }}</t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="岗位编码">{{ form.postCode }}</t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="岗位名称">{{ form.postName }}</t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="显示顺序">{{ form.postSort }}</t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="状态">
                <dict-tag :options="sys_normal_disable" :value="form.status" />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="创建时间">{{ parseTime(form.createTime) }}</t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="更新时间">{{ parseTime(form.updateTime) }}</t-form-item>
            </t-col>
            <t-col :span="12">
              <t-form-item label="备注">{{ form.remark }}</t-form-item>
            </t-col>
          </t-row>
        </t-form>
      </t-loading>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'Post',
});
import {
  AddIcon,
  BrowseIcon,
  DeleteIcon,
  DownloadIcon,
  EditIcon,
  RefreshIcon,
  SearchIcon,
  SettingIcon,
} from 'tdesign-icons-vue-next';
import type {
  FormInstanceFunctions,
  FormRule,
  PageInfo,
  PrimaryTableCol,
  SubmitContext,
  TableSort,
} from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref } from 'vue';

import type { SysPostForm, SysPostQuery, SysPostVo } from '@/api/system/model/postModel';
import { addPost, delPost, getPost, listPost, updatePost } from '@/api/system/post';

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict('sys_normal_disable');

const openView = ref(false);
const openViewLoading = ref(false);
const postList = ref<SysPostVo[]>([]);
const open = ref(false);
const loading = ref(false);
const eLoading = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const postRef = ref<FormInstanceFunctions>();
const columnControllerVisible = ref(false);
const sort = ref<TableSort>();

// 校验规则
const rules = ref<Record<string, Array<FormRule>>>({
  postName: [{ required: true, message: '岗位名称不能为空' }],
  postCode: [{ required: true, message: '岗位编码不能为空' }],
  postSort: [{ required: true, message: '岗位顺序不能为空' }],
});

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `岗位编号`, colKey: 'postId', align: 'center' },
  { title: `岗位编码`, colKey: 'postCode', align: 'center' },
  { title: `岗位名称`, colKey: 'postName', align: 'center' },
  { title: `岗位排序`, colKey: 'postSort', align: 'center', sorter: true },
  { title: `状态`, colKey: 'status', align: 'center' },
  { title: `创建时间`, colKey: 'createTime', align: 'center', width: 180, sorter: true },
  { title: `操作`, colKey: 'operation', align: 'center', width: 180 },
]);
// 提交表单对象
const form = ref<SysPostForm & SysPostVo>({});
// 查询对象
const queryParams = ref<SysPostQuery>({
  pageNum: 1,
  pageSize: 10,
  postCode: undefined,
  postName: undefined,
  status: undefined,
});

// 分页
const pagination = computed(() => {
  return {
    current: queryParams.value.pageNum,
    pageSize: queryParams.value.pageSize,
    total: total.value,
    showJumper: true,
    onChange: (pageInfo: PageInfo) => {
      queryParams.value.pageNum = pageInfo.current;
      queryParams.value.pageSize = pageInfo.pageSize;
      getList();
    },
  };
});

/** 查询岗位信息列表 */
function getList() {
  loading.value = true;
  listPost(queryParams.value)
    .then((response) => {
      postList.value = response.rows;
      total.value = response.total;
    })
    .finally(() => (loading.value = false));
}
/** 表单重置 */
function reset() {
  form.value = {
    postId: undefined,
    postCode: undefined,
    postName: undefined,
    postSort: 0,
    status: '1',
    remark: undefined,
  };
  proxy.resetForm('postRef');
}
/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}
/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef');
  queryParams.value.pageNum = 1;
  handleSortChange(null);
}

/** 排序触发事件 */
function handleSortChange(value?: TableSort) {
  sort.value = value;
  if (Array.isArray(value)) {
    queryParams.value.orderByColumn = value.map((item) => item.sortBy).join(',');
    queryParams.value.isAsc = value.map((item) => (item.descending ? 'descending' : 'ascending')).join(',');
  } else {
    queryParams.value.orderByColumn = value?.sortBy;
    queryParams.value.isAsc = value?.descending ? 'descending' : 'ascending';
  }
  getList();
}

/** 多选框选中数据 */
function handleSelectionChange(selection: Array<string | number>) {
  ids.value = selection;
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = '添加岗位';
}

/** 详情按钮操作 */
function handleDetail(row: SysPostVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const postId = row.postId || ids.value.at(0);
  getPost(postId).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 修改按钮操作 */
function handleUpdate(row?: SysPostVo) {
  reset();
  open.value = true;
  title.value = '修改岗位';
  eLoading.value = true;
  const postId = row?.postId || ids.value.at(0);
  getPost(postId).then((response) => {
    form.value = response.data;
    eLoading.value = false;
  });
}

/** 提交按钮 */
function onConfirm() {
  postRef.value.submit();
}

/** 提交按钮 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.postId) {
      updatePost(form.value)
        .then(() => {
          proxy.$modal.msgSuccess('修改成功');
          open.value = false;
          getList();
        })
        .finally(() => proxy.$modal.msgClose(msgLoading));
    } else {
      addPost(form.value)
        .then(() => {
          proxy.$modal.msgSuccess('新增成功');
          open.value = false;
          getList();
        })
        .finally(() => proxy.$modal.msgClose(msgLoading));
    }
  } else {
    proxy.$modal.msgError(firstError);
  }
}
/** 删除按钮操作 */
function handleDelete(row?: SysPostVo) {
  const postIds = row?.postId || ids.value;
  proxy.$modal.confirm(`是否确认删除岗位编号为"${postIds}"的数据项？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delPost(postIds)
      .then(() => {
        if (!row) ids.value = [];
        getList();
        proxy.$modal.msgSuccess('删除成功');
      })
      .finally(() => proxy.$modal.msgClose(msgLoading));
  });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    'system/post/export',
    {
      ...queryParams.value,
    },
    `post_${new Date().getTime()}.xlsx`,
  );
}

getList();
</script>

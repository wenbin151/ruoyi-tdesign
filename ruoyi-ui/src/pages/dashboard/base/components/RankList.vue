<template>
  <t-row :gutter="16" class="row-container">
    <t-col :xs="12" :xl="6">
      <t-card title="销售订单排名" class="dashboard-rank-card" :bordered="false">
        <template #actions>
          <t-radio-group default-value="dateVal" variant="default-filled">
            <t-radio-button value="dateVal">本周</t-radio-button>
            <t-radio-button value="monthVal">近三个月</t-radio-button>
          </t-radio-group>
        </template>
        <t-table :data="SALE_TEND_LIST" :columns="SALE_COLUMNS" row-key="productName">
          <template #index="{ rowIndex }">
            <span :class="getRankClass(rowIndex)">
              {{ rowIndex + 1 }}
            </span>
          </template>
          <template #growUp="{ row }">
            <span>
              <trend :type="row.growUp > 0 ? 'up' : 'down'" :describe="Math.abs(row.growUp)" />
            </span>
          </template>
          <template #operation="slotProps">
            <t-link theme="primary" @click="rehandleClickOp(slotProps)">详情</t-link>
          </template>
        </t-table>
      </t-card>
    </t-col>
    <t-col :xs="12" :xl="6">
      <t-card title="销售订单排名" class="dashboard-rank-card" :bordered="false">
        <template #actions>
          <t-radio-group default-value="dateVal" variant="default-filled">
            <t-radio-button value="dateVal">本周</t-radio-button>
            <t-radio-button value="monthVal">近三个月</t-radio-button>
          </t-radio-group>
        </template>
        <t-table :data="BUY_TEND_LIST" :columns="BUY_COLUMNS" row-key="productName">
          <template #index="{ rowIndex }">
            <span :class="getRankClass(rowIndex)">
              {{ rowIndex + 1 }}
            </span>
          </template>
          <template #growUp="{ row }">
            <trend :type="row.growUp > 0 ? 'up' : 'down'" :describe="Math.abs(row.growUp)" />
          </template>
          <template #operation="slotProps">
            <t-link theme="primary" @click="rehandleClickOp(slotProps)">详情</t-link>
          </template>
        </t-table>
      </t-card>
    </t-col>
  </t-row>
</template>

<script lang="ts" setup>
// 导入样式
import Trend from '@/components/trend/index.vue';

import { BUY_COLUMNS, BUY_TEND_LIST, SALE_COLUMNS, SALE_TEND_LIST } from '../constants';

const rehandleClickOp = (val: MouseEvent) => {
  console.log(val);
};
const getRankClass = (index: number) => {
  return ['dashboard-rank', { 'dashboard-rank__top': index < 3 }];
};
</script>

<style lang="less" scoped>
.dashboard-rank-card {
  padding: var(--td-comp-paddingTB-xxl) var(--td-comp-paddingLR-xxl);

  :deep(.t-card__header) {
    padding: 0;
  }

  :deep(.t-card__title) {
    font: var(--td-font-title-large);
    font-weight: 400;
  }

  :deep(.t-card__body) {
    padding: 0;
    margin-top: var(--td-comp-margin-xxl);
  }
}

.dashboard-rank__cell {
  display: inline-flex;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  color: white;
  font-size: 14px;
  background-color: var(--td-gray-color-5);
  align-items: center;
  justify-content: center;
  font-weight: 700;

  &--top {
    background: var(--td-brand-color);
  }
}
</style>

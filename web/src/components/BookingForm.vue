<script setup>
import { reactive, computed } from 'vue'
import { formatMoney } from '@/utils/format'

const props = defineProps({
  route: { type: Object, required: true },
  loading: { type: Boolean, default: false },
})

const emit = defineEmits(['submit', 'cancel'])

const form = reactive({
  contactName: '',
  contactPhone: '',
  travelDate: '',
  persons: 1,
  remark: '',
})

const totalAmount = computed(() => {
  const amount = Number(props.route?.price || 0) * form.persons
  return formatMoney(amount)
})

function submit() {
  emit('submit', { ...form })
}
</script>

<template>
  <div class="booking-form">
    <div class="booking-route-info">
      <div class="route-name">{{ route.title }}</div>
      <div class="route-meta">{{ route.departure }} → {{ route.destination }} · {{ route.days }} 天</div>
    </div>

    <div class="form-grid">
      <div class="form-field">
        <label>联系人姓名 <span class="required">*</span></label>
        <input v-model="form.contactName" type="text" placeholder="请输入联系人姓名" />
      </div>

      <div class="form-field">
        <label>联系电话 <span class="required">*</span></label>
        <input v-model="form.contactPhone" type="tel" placeholder="请输入手机号码" />
      </div>

      <div class="form-field">
        <label>出行日期 <span class="required">*</span></label>
        <input v-model="form.travelDate" type="date" :min="new Date().toISOString().slice(0, 10)" />
      </div>

      <div class="form-field">
        <label>出行人数 <span class="required">*</span></label>
        <input
          v-model.number="form.persons"
          type="number"
          min="1"
          :max="route.maxGroupSize || 99"
          placeholder="人数"
        />
      </div>

      <div class="form-field form-field--full">
        <label>备注</label>
        <textarea v-model="form.remark" rows="2" placeholder="特殊需求或备注（可选）"></textarea>
      </div>
    </div>

    <div class="booking-total">
      <span class="total-label">合计费用</span>
      <span class="total-amount">￥{{ totalAmount }}</span>
      <small>（￥{{ formatMoney(route.price) }} × {{ form.persons }} 人）</small>
    </div>

    <div class="booking-actions">
      <button class="btn-cancel" type="button" @click="emit('cancel')">取消</button>
      <button class="btn-submit" type="button" :disabled="loading" @click="submit">
        {{ loading ? '提交中...' : '确认预订' }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.booking-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.booking-route-info {
  background: var(--brand-ghost);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  padding: 14px 18px;
}

.route-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--brand-dark);
  margin-bottom: 4px;
}

.route-meta {
  font-size: 13px;
  color: var(--text-muted);
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-field--full {
  grid-column: 1 / -1;
}

.form-field label {
  font-size: 13px;
  font-weight: 600;
  color: var(--text);
}

.required {
  color: var(--danger);
}

.form-field input,
.form-field textarea {
  border: 1px solid var(--border-strong);
  border-radius: var(--radius-sm);
  padding: 10px 14px;
  font-size: 14px;
  color: var(--text);
  outline: none;
  transition: border-color 180ms, box-shadow 180ms;
  background: #fff;
  resize: none;
}

.form-field input:focus,
.form-field textarea:focus {
  border-color: var(--brand);
  box-shadow: 0 0 0 3px var(--brand-ghost);
}

.booking-total {
  display: flex;
  align-items: baseline;
  gap: 8px;
  padding: 14px 18px;
  background: var(--bg-soft);
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
}

.total-label {
  font-size: 13px;
  color: var(--text-muted);
}

.total-amount {
  font-size: 24px;
  font-weight: 800;
  color: var(--brand);
}

.booking-total small {
  font-size: 12px;
  color: var(--text-soft);
}

.booking-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.btn-cancel {
  padding: 10px 24px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-strong);
  background: #fff;
  color: var(--text-muted);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background 180ms;
}

.btn-cancel:hover {
  background: var(--bg-soft);
}

.btn-submit {
  padding: 10px 28px;
  border-radius: var(--radius-sm);
  border: none;
  background: var(--brand);
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: background 180ms;
}

.btn-submit:hover:not(:disabled) {
  background: var(--brand-dark);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 480px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .form-field--full {
    grid-column: 1;
  }
}
</style>

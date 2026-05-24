<script setup>
defineProps({
  title: {
    type: String,
    required: true,
  },
  subtitle: {
    type: String,
    default: '',
  },
  summary: {
    type: String,
    default: '',
  },
})
</script>

<template>
  <div class="admin-page-shell">
    <header class="admin-page-shell__header">
      <div class="admin-page-shell__heading">
        <p class="admin-page-shell__title">{{ title }}</p>
        <p v-if="subtitle" class="admin-page-shell__subtitle">{{ subtitle }}</p>
      </div>

      <div class="admin-page-shell__actions">
        <slot name="actions" />
      </div>
    </header>

    <section v-if="$slots.filters || summary" class="admin-page-shell__filters">
      <div class="admin-page-shell__filters-inner">
        <slot name="filters" />
      </div>
      <el-tag v-if="summary" round effect="plain" type="info" class="admin-page-shell__summary">
        {{ summary }}
      </el-tag>
    </section>

    <section class="admin-page-shell__content">
      <slot />
    </section>
  </div>
</template>

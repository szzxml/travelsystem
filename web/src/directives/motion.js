const PRESET_MAP = {
  'reveal-up': { x: 0, y: 28, scale: 0.985, blur: 14 },
  'reveal-left': { x: -28, y: 0, scale: 0.985, blur: 14 },
  'reveal-right': { x: 28, y: 0, scale: 0.985, blur: 14 },
  'reveal-scale': { x: 0, y: 0, scale: 0.94, blur: 18 },
}

const DEFAULT_OPTIONS = {
  preset: 'reveal-up',
  delay: 0,
  duration: 720,
  threshold: 0.16,
  distance: 28,
  once: true,
  rootMargin: '0px 0px -12% 0px',
}

function getReducedMotionPreference() {
  return typeof window !== 'undefined' && window.matchMedia('(prefers-reduced-motion: reduce)').matches
}

function normalizeOptions(bindingValue) {
  if (!bindingValue || typeof bindingValue !== 'object') {
    return { ...DEFAULT_OPTIONS }
  }

  return {
    ...DEFAULT_OPTIONS,
    ...bindingValue,
  }
}

function serializeOptions(options) {
  return JSON.stringify(options)
}

function applyPresetVariables(element, options) {
  const preset = PRESET_MAP[options.preset] ?? PRESET_MAP['reveal-up']
  const offsetX = options.offsetX ?? preset.x
  const offsetY = options.offsetY ?? (preset.y === 0 ? 0 : Math.sign(preset.y) * options.distance)

  element.classList.add('motion-element', `motion-${options.preset}`)
  element.style.setProperty('--motion-delay', `${options.delay}ms`)
  element.style.setProperty('--motion-duration', `${options.duration}ms`)
  element.style.setProperty('--motion-x', `${offsetX}px`)
  element.style.setProperty('--motion-y', `${offsetY}px`)
  element.style.setProperty('--motion-scale', `${options.scale ?? preset.scale}`)
  element.style.setProperty('--motion-blur', `${options.blur ?? preset.blur}px`)
}

function cleanupMotion(element) {
  if (element.__motionCleanup) {
    element.__motionCleanup()
    delete element.__motionCleanup
  }
}

function mountMotion(element, bindingValue) {
  cleanupMotion(element)

  const options = normalizeOptions(bindingValue)
  element.__motionConfig = serializeOptions(options)
  applyPresetVariables(element, options)

  if (getReducedMotionPreference()) {
    element.classList.add('is-visible')
    return
  }

  element.classList.remove('is-visible')

  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          requestAnimationFrame(() => {
            element.classList.add('is-visible')
          })

          if (options.once) {
            observer.unobserve(element)
          }
        } else if (!options.once) {
          element.classList.remove('is-visible')
        }
      })
    },
    {
      threshold: options.threshold,
      rootMargin: options.rootMargin,
    },
  )

  observer.observe(element)
  element.__motionCleanup = () => observer.disconnect()
}

export const motionDirective = {
  mounted(element, binding) {
    mountMotion(element, binding.value)
  },
  updated(element, binding) {
    const nextOptions = normalizeOptions(binding.value)

    if (serializeOptions(nextOptions) !== element.__motionConfig) {
      mountMotion(element, binding.value)
    }
  },
  unmounted(element) {
    cleanupMotion(element)
    delete element.__motionConfig
  },
}

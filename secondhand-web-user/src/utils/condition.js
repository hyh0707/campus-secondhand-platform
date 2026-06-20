/**
 * 成色统一映射工具
 *
 * 后端 conditionLevel 可能的取值：
 *   新格式（数字字符串）: "10", "9", "8", "7", "6"
 *   旧格式（英文枚举）   : "brand_new", "like_new", "good", "fair", "poor", "old"
 *   异常格式               : "8 八成新" 等带中文的复合值
 */

// 成色中文标签映射
const CONDITION_LABELS = {
  // 数字字符串（新格式）
  '10': '全新',
  '9': '九成新',
  '8': '八成新',
  '7': '七成新',
  '6': '六成新及以下',
  // 英文枚举（旧格式）
  'brand_new': '全新',
  'like_new': '九成新',
  'good': '八成新',
  'fair': '七成新',
  'poor': '六成新及以下',
  'old': '六成新及以下'
}

// 成色对应的 Element Plus Tag 颜色类型
const CONDITION_TAG_TYPES = {
  '10': '',
  '9': 'success',
  '8': '',
  '7': 'warning',
  '6': 'danger',
  'brand_new': '',
  'like_new': 'success',
  'good': '',
  'fair': 'warning',
  'poor': 'danger',
  'old': 'danger'
}

// 下拉框选项（value 统一为数字字符串，label 为中文）
export const CONDITION_OPTIONS = [
  { value: '10', label: '全新' },
  { value: '9', label: '九成新' },
  { value: '8', label: '八成新' },
  { value: '7', label: '七成新' },
  { value: '6', label: '六成新及以下' }
]

/**
 * 将 conditionLevel 值标准化为数字字符串（用于表单回显/提交）
 * 兼容：数字字符串 "8"、英文枚举 "good"、复合值 "8 八成新"
 * @param {string} value
 * @returns {string} 标准化后的数字字符串，如 "8"
 */
export function conditionValue(value) {
  if (!value) return ''
  const str = String(value).trim()

  // 已经是纯数字字符串 "8" → "8"
  if (/^\d+$/.test(str)) {
    return str
  }

  // 英文枚举映射
  const enumToNum = {
    'brand_new': '10',
    'like_new': '9',
    'good': '8',
    'fair': '7',
    'poor': '6',
    'old': '6'
  }
  if (enumToNum[str]) {
    return enumToNum[str]
  }

  // 尝试提取数字部分，如 "8 八成新" → "8"
  const numMatch = str.match(/^(\d+)/)
  if (numMatch) {
    return numMatch[1]
  }

  return ''
}

/**
 * 将 conditionLevel 值转换为中文标签
 * 兼容：数字字符串 "8"、英文枚举 "good"、复合值 "8 八成新"
 * @param {string} value
 * @returns {string}
 */
export function conditionLabel(value) {
  if (!value) return '未知'
  const str = String(value).trim()

  // 直接匹配
  if (CONDITION_LABELS[str]) {
    return CONDITION_LABELS[str]
  }

  // 尝试提取数字部分，如 "8 八成新" → "8"
  const numMatch = str.match(/^(\d+)/)
  if (numMatch && CONDITION_LABELS[numMatch[1]]) {
    return CONDITION_LABELS[numMatch[1]]
  }

  // 尝试提取中文部分，如 "8 八成新" → "八成新"
  const cnMatch = str.match(/[全新|成新|以下]+/)
  // 如果包含中文，尝试匹配常见成色词
  if (/全新/.test(str)) return '全新'
  if (/九成新/.test(str)) return '九成新'
  if (/八成新/.test(str)) return '八成新'
  if (/七成新/.test(str)) return '七成新'
  if (/六成新|以下/.test(str)) return '六成新及以下'

  return str
}

/**
 * 将 conditionLevel 值转换为 Element Plus Tag 的 type 属性
 * @param {string} value
 * @returns {string}
 */
export function conditionTagType(value) {
  if (!value) return 'info'
  const str = String(value).trim()

  if (CONDITION_TAG_TYPES[str]) {
    return CONDITION_TAG_TYPES[str]
  }

  const numMatch = str.match(/^(\d+)/)
  if (numMatch && CONDITION_TAG_TYPES[numMatch[1]]) {
    return CONDITION_TAG_TYPES[numMatch[1]]
  }

  return 'info'
}
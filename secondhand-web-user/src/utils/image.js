/**
 * 图片路径工具
 * 确保后端图片路径正确拼接 /upload 前缀
 */

/**
 * 获取标准化图片 URL
 * @param {string|undefined|null} url - 原始图片路径
 * @returns {string} 标准化后的图片 URL，空值返回空字符串
 */
export function getImageUrl(url) {
  if (!url) return ''
  const str = String(url).trim()
  // 绝对路径直接返回
  if (str.startsWith('http://') || str.startsWith('https://')) return str
  // 已包含 /upload/ 前缀
  if (str.startsWith('/upload/')) return str
  // 缺少前导斜杠
  if (str.startsWith('upload/')) return '/' + str
  // 缺少 /upload 前缀
  if (str.startsWith('/goods/')) return '/upload' + str
  if (str.startsWith('goods/')) return '/upload/' + str
  if (str.startsWith('/avatar/')) return '/upload' + str
  if (str.startsWith('avatar/')) return '/upload/' + str
  // 日期路径如 2026/06/xxx.png
  if (/^\d{4}\/\d{2}\//.test(str)) return '/upload/' + str
  // 其他相对路径
  if (str.startsWith('/')) return str
  return '/upload/' + str
}

/**
 * 图片路径工具
 */

export function getImageUrl(url) {
  if (!url) return ''
  const str = String(url).trim()
  if (str.startsWith('http://') || str.startsWith('https://')) return str
  if (str.startsWith('/upload/')) return str
  if (str.startsWith('upload/')) return '/' + str
  if (str.startsWith('/goods/')) return '/upload' + str
  if (str.startsWith('goods/')) return '/upload/' + str
  if (/^\d{4}\/\d{2}\//.test(str)) return '/upload/' + str
  if (str.startsWith('/')) return str
  return '/upload/' + str
}

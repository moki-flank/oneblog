/**
 * 格式化时间
 * @param {string|number|Date} time 时间
 * @returns {string} 格式化后的时间
 */
export function formatTime(time) {
  const date = new Date(time)
  const now = new Date()
  const diff = (now - date) / 1000 // 转换为秒

  if (diff < 60) {
    return '刚刚'
  } else if (diff < 3600) {
    return Math.floor(diff / 60) + '分钟前'
  } else if (diff < 86400) {
    return Math.floor(diff / 3600) + '小时前'
  } else if (diff < 2592000) {
    return Math.floor(diff / 86400) + '天前'
  } else if (diff < 31536000) {
    return Math.floor(diff / 2592000) + '个月前'
  } else {
    return formatDate(date)
  }
}

/**
 * 格式化日期
 * @param {Date} date 日期对象
 * @returns {string} 格式化后的日期字符串 YYYY-MM-DD
 */
function formatDate(date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

/**
 * 获取相对时间
 * @param {string|number|Date} time 时间
 * @returns {string} 相对时间描述
 */
export function getRelativeTime(time) {
  const date = new Date(time)
  const now = new Date()
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const yesterday = new Date(today - 86400000)
  const thisYear = new Date(now.getFullYear(), 0, 1)

  if (date >= today) {
    return formatTime(time)
  } else if (date >= yesterday) {
    return '昨天'
  } else if (date >= thisYear) {
    return formatMonthDay(date)
  } else {
    return formatDate(date)
  }
}

/**
 * 格式化月日
 * @param {Date} date 日期对象
 * @returns {string} MM-DD 格式的日期
 */
function formatMonthDay(date) {
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${month}-${day}`
} 
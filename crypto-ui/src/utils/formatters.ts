export const formatCurrency = (v: number | string | null, d = 2) => {
  if (v === null || v === undefined) return 'N/A'
  const n = typeof v === 'string' ? parseFloat(v) : v
  if (isNaN(n)) return 'N/A'
  return '$' + n.toLocaleString(undefined, { minimumFractionDigits: d, maximumFractionDigits: d })
}

export const formatNumber = (v: number | null, d = 2) => {
  if (v === null || v === undefined) return 'N/A'
  return v.toLocaleString(undefined, { minimumFractionDigits: d, maximumFractionDigits: d })
}
